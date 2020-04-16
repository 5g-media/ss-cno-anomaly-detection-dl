package com.fivegmedia.predictor

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4s.Implicits._
import com.thoughtworks.each.Monadic._
import com.thoughtworks.future._
import com.thoughtworks.deeplearning.plugins.Builtins
import com.thoughtworks.feature.Factory

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scalaz.std.stream._

import Settings.hyperparameters.implicits._
import Settings.hyperparameters.INDArrayWeight
import Settings.hyperparameters.INDArrayLayer
import Settings.hyperparameters.DoubleLayer
import com.fivegmedia.predictor.datasets.vCacheEdgeDataSet.{VCacheEdgeDataSet => TrainingDataSet}

import scala.collection.mutable

/** Deep Learning Classifier for [[com.fivegmedia.predictor]] */
object DLClassifier {

  // Publish to executor or simply print
  var outputOperation: String = ""

  // Definition of a Producer to forward the prediction
  // See more: https://kafka.apache.org/10/javadoc/org/apache/kafka/clients/producer/KafkaProducer.html
  val producer = new KafkaProducer[String, String](Settings.producerKafkaParameters)

  // Has the vCDN Network Service been already scaled out?
  var isScaledOut: mutable.Map[String, Boolean] = mutable.Map[String, Boolean]()

  // Setting initial Weight Values & Total Iterations
  val weights: INDArrayWeight = INDArrayWeight(initialValueOfWeight)
  val totalIterations: Int = Settings.numberOfIterations

  def main(args: Array[String]): Unit = {
    // Check if publishing to Kafka or printing is needed
    if (args.length == 0 || args.length == 1) {
      println("The operation [ publish_to_exec | print ] is a required argument.")
      println("The Kafka host to connect is a required argument.")
      return
    }
    else if (args(0) != Settings.printToLog && args(0) != Settings.publishToExec) {
      println("The available output operations are [ publish_to_exec | print ].")
      return
    }
    outputOperation = args(0)
    Settings.KAFKA_HOST = args(1)

    // Configuration for a Spark application.
    // See more: https://spark.apache.org/docs/2.3.0/api/java/org/apache/spark/SparkConf.html
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("CNO-UHDoCDN")

    // Main entry point for Spark Streaming functionality.
    // Create a StreamingContext by providing the configuration necessary for a new SparkContext.
    // See more: https://spark.apache.org/docs/2.3.0/api/java/org/apache/spark/streaming/StreamingContext.html
    val streamingContext = new StreamingContext(conf, Seconds(Settings.BATCH_DURATION))

    // Create InputDStream from Kafka Topic
    val testData: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](List(Settings.KAFKA_PREP_TOPIC), Settings.streamingKafkaParametersTesting)
    )

    // Train Neural Network
    // See more: https://deeplearning.thoughtworks.school/demo/GettingStarted.html
    // val lossByTime: Stream[Double] = Await.result(train.toScalaFuture, Duration.Inf)

    // Start streamimg context
    testData.map(record => Formatter.formatMeasurements(record))
      .map(formatted =>
        if (formatted != null)
          forwardPrediction(formatted._1, Await.result(test(formatted._2).predict.toScalaFuture, Duration.Inf))).print()
    streamingContext.start()
    streamingContext.awaitTermination()
  }

  @monadic[Future]
  def train: Future[Stream[Double]] = {
    for (_ <- (0 until totalIterations).toStream) yield {
      squareLoss(TrainingDataSet.TrainingQuestions, TrainingDataSet.ExpectedAnswers).train.each
    }
  }

  /** Sets initial values of weights
    *
    * @return Weights: INDArray
    */
  def initialValueOfWeight: INDArray = {
    // Nd4j.randn(Settings.numberOfFeatures, Settings.numberOfColumns)
    Array(Array(1.17), Array(-1.70), Array(-1.08), Array(-1.33), Array(1.43), Array(-0.50),
          Array(0.68), Array(0.90), Array(-0.20), Array(0.16), Array(0.23), Array(-0.36)).toNDArray
  }


  def test(questions: INDArray): INDArrayLayer = {
    INDArrayWeight(questions) dot weights
  }

  /** Computes the square loss
    *
    * @param questions Test Vectors: INDArray
    * @param expectedAnswer Expected Answers: INDArray
    * @return The square loss: DoubleLayer
    */
  def squareLoss(questions: INDArray, expectedAnswer: INDArray): DoubleLayer = {
    val difference = test(questions) - expectedAnswer
    (difference * difference).mean
  }

  /** Publishes the prediction to the execution part of MAPE
    *
    * @param resourceData A Map of the data received previously
    * @param predictionArray The prediction of an ML Algorithm
    */
  def forwardPrediction(resourceData: DataModel, predictionArray: INDArray): Unit = {
    var prediction: Int = scala.math.round(predictionArray(0)).asInstanceOf[Int]
    if (predictionArray(0) > 1)
      return
    if (predictionArray(0) < 0.6)
      prediction = 0
    else if (predictionArray(0) >= 0.60)
      prediction = 1
    val manoNsID: String = resourceData.manoNsId()
    var action: Int = -1

    if (isScaledOut.keys.exists(manoNsID == _)) {
      if (!isScaledOut(manoNsID)) {
        action = prediction
        if (action == 1) isScaledOut(manoNsID) = true
      }
      else {
        if (prediction == 1) {
          action = 0
        }
        else {
          action = 2
          isScaledOut(manoNsID) = false
        }
      }
    }
    else {
      action = prediction
      isScaledOut(manoNsID) = action match {
        case 0 => false
        case 1 => true
      }
    }

    val analysis: Map[String, Any] = action match {
      case 1 => Map("analysis" -> ("action" -> true)) ++
        Map("execution" -> Map("planning" -> "faas_vnf_scale_out", "value" -> "edge_vcache"))
      case 2 => Map("analysis" -> ("action" -> true)) ++
        Map("execution" -> Map("planning" -> "faas_vnf_scale_in", "value" -> "edge_vcache"))
      case 0 => Map("analysis" -> ("action" -> false))
    }

    val record = new ProducerRecord(
      s"${Settings.KAFKA_EXEC_TOPIC}",
      s"${Settings.UHDoCDN}",
      s"${Formatter.mapToJson(resourceData.dataModel ++ analysis)}")

    if (outputOperation == Settings.publishToExec) {
      producer.send(record)
      println("Predicted Value: %s".format(predictionArray(0)),"isScaled: %s".format(isScaledOut(manoNsID)), record.value)
    }
    else if (outputOperation == Settings.printToLog) {
      println("Predicted Value: %s".format(predictionArray(0)),"isScaled: %s".format(isScaledOut(manoNsID)), record.value)
    }
  }

}
