package org.beerfactory.casquette.client

import java.net.URL

import org.beerfactory.casquette.mqtt.QualityOfService.QualityOfService

sealed trait APIMessage
sealed trait CommandMessage extends APIMessage
sealed trait ResponseMessage extends APIMessage

case class Status() extends CommandMessage
case class Connect(brokerUri: URL,
                   clientId:Option[String],
                   cleanSession: Option[Boolean],
                   keepAlive: Option[Int],
                   willTopic: Option[String],
                   willMessage: Option[String],
                   userName: Option[String],
                   password: Option[String]
                  )
case class Disconnect() extends CommandMessage

case class Disconnected() extends ResponseMessage
case class Connected(clientId: String, maxQos: QualityOfService) extends ResponseMessage
