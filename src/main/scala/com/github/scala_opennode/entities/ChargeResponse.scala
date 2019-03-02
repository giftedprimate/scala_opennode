package com.github.scala_opennode.entities

import play.api.libs.json._

object ChargeResponse {
  implicit val chargeResponseReads: Reads[ChargeResponse] =
    (__ \ "data").read[ChargeResponseData].map(ChargeResponse(_))
}

case class ChargeResponse(
    data: ChargeResponseData
)
