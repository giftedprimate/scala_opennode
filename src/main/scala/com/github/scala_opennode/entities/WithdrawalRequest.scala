package com.github.scala_opennode.entities

import play.api.libs.json._

object WithdrawalRequest {
  implicit val withdrawalRequest: OFormat[WithdrawalRequest] = Json.format[WithdrawalRequest]
}

case class WithdrawalRequest(
    `type`: String,
    amount: Long,
    address: String,
    callback_url: Option[String] = None
)
