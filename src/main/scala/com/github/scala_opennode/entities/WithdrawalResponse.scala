package com.github.scala_opennode.entities

import play.api.libs.json._

object WithdrawalResponse {
  implicit val withdrawalResponseReads: Reads[WithdrawalResponse] =
    (__ \ "data").read[WithdrawalResponseData].map(WithdrawalResponse(_))
}

case class WithdrawalResponse(
    data: WithdrawalResponseData
)
