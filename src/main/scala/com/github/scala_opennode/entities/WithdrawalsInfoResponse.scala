package com.github.scala_opennode.entities

import play.api.libs.json._

object WithdrawalsInfoResponse {
  implicit val chargesResponseReads: Reads[WithdrawalsInfoResponse] =
    (__ \ "data").read[List[WithdrawalInfoData]].map(WithdrawalsInfoResponse(_))
}

case class WithdrawalsInfoResponse(
    data: List[WithdrawalInfoData]
)
