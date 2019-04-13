package com.github.scala_opennode.entities

import play.api.libs.json._

object WithdrawalInfoData {
  implicit val withdrawalInfoDataFormat: OFormat[WithdrawalInfoData] = Json.format[WithdrawalInfoData]
}

/**
 * @param id Withdrawal ID
 * @param amount Amount withdrew (satoshis)
 * @param `type` Withdrawal type
 * @param reference Withdrawal transaction ID
 * @param processed_at Withdrawal processed timestamp
 * @param status Withdrawal status pending/confirmed
 * @param address Withdrawal destination
 * @param fee Withdrawal fee (chain & In - satoshis)
 * @param fiat_value Amount withdrew in merchants currency
 */
case class WithdrawalInfoData(
    id: String,
    amount: Option[Long],
    `type`: Option[String],
    reference: Option[String],
    processed_at: Option[Long],
    status: Option[String],
    address: Option[String],
    fee: Option[Long],
    fiat_value: Option[Double]
)
