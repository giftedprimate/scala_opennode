package com.github.scala_opennode.entities

import play.api.libs.json._

object ChargeInfoResponse {
  implicit val chargeInfoResponseReads: Reads[ChargeInfoResponse] =
    (__ \ "data").read[ChargeInfoData].map(ChargeInfoResponse(_))
}

case class ChargeInfoResponse(
    data: ChargeInfoData
)
