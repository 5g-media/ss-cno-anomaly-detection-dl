package com.fivegmedia.predictor

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jackson.Serialization
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4s.Implicits._

import scala.collection.{immutable, mutable}

/** Parsing and formatting for [[com.fivegmedia.predictor]] */
object Formatter {

  /** Parse an incoming Kafka message
    *
    * Convert a stringified JSON to a scala Map
    *
    * @param recordEntry A Kafka message
    * @return The message.value as a mutable.Map
    */
  def parseRecordValue(recordEntry: String): mutable.Map[String, Any] = {
    // Process JSON providing a Map or None
    implicit val formats: DefaultFormats.type = DefaultFormats
    mutable.Map(parse(recordEntry).extract[immutable.Map[String, Any]].toSeq: _*)
  }

  /** Transform a Scala Map to stringified JSON
    *
    * @param map A mutable.Map to transform
    * @return A stringified JSON
    */
  def mapToJson(map: mutable.Map[String, Any]): String = {
    implicit val formats: DefaultFormats.type = DefaultFormats
    Serialization.write(map)
  }

  /** Parse a Kafka ConsumerRecord and format measurements to INDArray
    *
    * @param record A ConsumerRecord received from a topic
    * @return A tuple of the Data Model and input measurements
    */
  def formatMeasurements(record: ConsumerRecord[String, String]): (DataModel, INDArray) = {
    // Check if the received metrics are for UHDoCDN
    if (record.key != Settings.UHDoCDN)
      return null
    // Check if the VDU is an edge cache
    val resourceData = new DataModel(parseRecordValue(record.value))
    if (!resourceData.manoVduIsEdgeCache())
      return null
    // Get measurements and format accordingly
    val measurements = resourceData.measurements()
    val measurementsArray = Array(
      measurements("bytes_sent_rate").toString.toDouble / Settings.metricMaxValues("bytes_sent_rate"),
      measurements("bytes_recv_rate").toString.toDouble / Settings.metricMaxValues("bytes_recv_rate"),
      measurements("packets_sent_rate").toString.toDouble / Settings.metricMaxValues("packets_sent_rate"),
      measurements("packets_recv_rate").toString.toDouble / Settings.metricMaxValues("packets_sent_rate"),
      measurements("http.current_active_client_connections").toString.toDouble / Settings.metricMaxValues("http.current_active_client_connections"),
      measurements("http.current_client_connections").toString.toDouble / Settings.metricMaxValues("http.current_client_connections"),
      measurements("http.user_agent_current_connections_count").toString.toDouble / Settings.metricMaxValues("http.user_agent_current_connections_count"),
      measurements("response_data_stream_count").toString.toDouble / Settings.metricMaxValues("response_data_stream_count"),
      measurements("response_data_wan_bandwidth").toString.toDouble / Settings.metricMaxValues("response_data_wan_bandwidth"),
      measurements("response_data_lan_bandwidth").toString.toDouble / Settings.metricMaxValues("response_data_lan_bandwidth"),
      measurements("response_data_total_bandwidth").toString.toDouble / Settings.metricMaxValues("response_data_total_bandwidth"),
      measurements("response_data_stream_count_direct_play").toString.toDouble / Settings.metricMaxValues("response_data_stream_count_direct_play")
    )
    measurementsArray.map(x => if (x >= 1.0) 1.0)
    (resourceData, Array(measurementsArray).toNDArray)
  }

}
