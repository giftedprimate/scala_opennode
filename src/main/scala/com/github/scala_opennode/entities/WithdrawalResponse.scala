package com.github.scala_opennode.entities

import play.api.libs.json._

object WithdrawalResponseData {
  implicit val withdrawalResponseFormat: OFormat[WithdrawalResponseData] = Json.format[WithdrawalResponseData]
}

/**
 * @param id Withdrawal ID
 * @param `type` Withdrawal type
 * @param amount Amount withdrew (satoshis)
 * @param reference transaction id (chain) or payment request (In)
 * @param processed_at Withdrawal processing timestamp
 * @param address Withdrawal destination
 * @param fee Withdrawal fee
 */
case class WithdrawalResponseData(
    id: String,
    `type`: String,
    amount: Int,
    reference: String,
    processed_at: Int,
    address: String,
    fee: Int
)

object WithdrawalResponse {
  implicit val withdrawalResponseReads: Reads[WithdrawalResponse] =
    (__ \ "data").read[WithdrawalResponseData].map(WithdrawalResponse(_))
}

case class WithdrawalResponse(
    data: WithdrawalResponseData
)
