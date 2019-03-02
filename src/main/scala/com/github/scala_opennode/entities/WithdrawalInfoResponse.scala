package com.github.scala_opennode.entities

import play.api.libs.json._

object WithdrawalInfoResponse {
  implicit val withdrawalInfoResponseReads: Reads[WithdrawalInfoResponse] =
    (__ \ "data").read[WithdrawalInfoData].map(WithdrawalInfoResponse(_))
}

case class WithdrawalInfoResponse(
    data: WithdrawalInfoData
)
