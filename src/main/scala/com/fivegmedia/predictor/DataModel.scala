package com.fivegmedia.predictor

import scala.collection.mutable
import scala.collection.immutable

/** A class representing the data model information
  * of the received messages.
  *
  * @param message An incoming Kafka message as a mutable.Map
  */
class DataModel(message: mutable.Map[String, Any]) {

  /* The incoming Kafka message as mutable.Map */
  val dataModel: mutable.Map[String, Any] = message

  /* The MANO content of the incoming message */
  val mano: Map[String, Map[String, Any]] = dataModel("mano").asInstanceOf[Map[String, Map[String, String]]]

  /** Retrieve the Mano Network Service (NS) ID
    *
    * @return The ID of the MANO Network Service as String
    */
  def manoNsId(): String = mano("ns").getOrElse("id", null).asInstanceOf[String]

  /** Retrieve the Mano Virtualized Network Function (VNF) ID
    *
    * @return The ID of the MANO VNF as String
    */
  def manoVnfId(): String = mano("vnf").getOrElse("id", null).asInstanceOf[String]

  /** Retrieve the Mano Virtual Deployment Unit (VDU) ID
    *
    * @return The ID of the MANO VDU as String
    */
  def manoVduId(): String = mano("vdu").getOrElse("id", null).asInstanceOf[String]

  /** Checks if VDU is an Edge vCache
    *
    * @return true if the VDU is an edge vCache
    */
  def manoVduIsEdgeCache(): Boolean = mano("vdu").getOrElse("name", null).asInstanceOf[String] contains "edge_vdu-1"

  /** Retrieve the VDU's Measurements
    *
    * @return The feature vector as a String
    */
  def measurements(): immutable.Map[String, Double] = dataModel("measurements").asInstanceOf[immutable.Map[String, Double]]

}
