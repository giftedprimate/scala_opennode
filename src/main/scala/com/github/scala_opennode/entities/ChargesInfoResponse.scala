package com.github.scala_opennode.entities

import play.api.libs.json._

object ChargesInfoResponse {
  implicit val chargesResponseReads: Reads[ChargesInfoResponse] =
    (__ \ "data").read[List[ChargeInfoData]].map(ChargesInfoResponse(_))
}

case class ChargesInfoResponse(
    data: List[ChargeInfoData]
)
