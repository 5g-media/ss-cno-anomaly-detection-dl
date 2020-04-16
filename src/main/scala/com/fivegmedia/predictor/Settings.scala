package com.fivegmedia.predictor

import $exec.FixedLearningRate

import java.util.Properties

import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import com.thoughtworks.deeplearning.plugins.Builtins
import com.thoughtworks.feature.Factory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.immutable

/** Settings holder for [[com.fivegmedia.predictor]] */
object Settings {

  // General Settings
  var KAFKA_HOST: String = ""
  val KAFKA_PREP_TOPIC: String = "ns.instances.prep"
  val SPARK_HOST: String = "spark://localhost:7077"
  val KAFKA_GROUP_ID: String = "CNO_UHDoCDN"
  val KAFKA_CLIENT_ID: String = "deep-learning-scala-predictor"
  val KAFKA_EXEC_TOPIC: String = "ns.instances.exec"

  // Kafka Parameters for Streaming
  // See more: http://kafka.apache.org/documentation.html#newconsumerconfigs
  val streamingKafkaParametersTesting: immutable.Map[String, Object] = immutable.Map[String, Object](
    "bootstrap.servers" -> KAFKA_HOST,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> KAFKA_GROUP_ID,
    "client.id" -> KAFKA_CLIENT_ID,
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (true: java.lang.Boolean)
  )

  // Kafka Parameters for Producer
  // See more: http://kafka.apache.org/documentation.html#newconsumerconfigs
  val producerKafkaParameters = new Properties()
  producerKafkaParameters.put("bootstrap.servers", KAFKA_HOST)
  producerKafkaParameters.put("key.serializer", classOf[StringSerializer])
  producerKafkaParameters.put("value.serializer", classOf[StringSerializer])

  // Spark Batch Duration Settings
  val BATCH_DURATION = 5

  // Deeplearning.scala Hyperparameters settings
  val hyperparameters = Factory[Builtins with FixedLearningRate].newInstance(learningRate = 0.003)
  val numberOfIterations = 500
  val numberOfFeatures = 12
  val numberOfColumns = 1

  // DataSet max values
  val metricMaxValues: immutable.Map[String, Double] = immutable.Map[String, Double](
    "bytes_sent_rate" -> 27.008863758774552,
    "bytes_recv_rate" -> 27.57807896287566,
    "packets_sent_rate" -> 20.652548916525653,
    "packets_recv_rate" -> 26.666666666666668,
    "hostdb.cache.total_hits_rate" -> 0.38888052586682725,
    "http.current_active_client_connections" -> 18,
    "http.current_client_connections" -> 19,
    "http.user_agent_current_connections_count" -> 19,
    "response_data_stream_count" -> 3,
    "response_data_wan_bandwidth" -> 42287,
    "response_data_lan_bandwidth" -> 375961,
    "response_data_total_bandwidth" -> 397926,
    "response_data_stream_count_direct_play" -> 3
  )

  // Possible operations
  val publishToExec: String = "publish_to_exec"
  val printToLog: String = "print"

  // UHDoCDN
  val UHDoCDN: String = "UHDoCDN"
}
