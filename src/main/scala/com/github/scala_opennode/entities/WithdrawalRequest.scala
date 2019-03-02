package com.github.scala_opennode.entities

import play.api.libs.json._

object WithdrawalRequest {
  implicit val withdrawalRequest: OFormat[WithdrawalRequest] = Json.format[WithdrawalRequest]
}

case class WithdrawalRequest(
    `type`: String,
    amount: Int,
    address: String
)
