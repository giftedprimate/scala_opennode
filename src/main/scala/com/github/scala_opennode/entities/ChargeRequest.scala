package com.github.scala_opennode.entities

import play.api.libs.json.{Json, OFormat}

object ChargeRequest {
  implicit val chargeRequestFormat: OFormat[ChargeRequest] = Json.format[ChargeRequest]
}

/**
 * @param description Charge Description
 * @param amount Charge price in satoshis
 * @param callback_url URL to receive webhooks
 * @param success_url URL to redirect the user after payment
 * @param order_id External order ID (use your platform ID) - tokenId|orderId
 * @param auto_settle Charge requested instant exchange
 */
case class ChargeRequest(
    description: String,
    amount: Long,
    order_id: String,
    customer_email: String,
    customer_name: String,
    callback_url: Option[String],
    success_url: Option[String],
    auto_settle: Boolean
)
