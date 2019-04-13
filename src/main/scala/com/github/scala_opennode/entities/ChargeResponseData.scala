package com.github.scala_opennode.entities

import play.api.libs.json._

object ChargeResponseData {
  implicit val chargeResponseDataFormat: OFormat[ChargeResponseData] = Json.format[ChargeResponseData]
}

/**
 * @param id Charge ID
 * @param name Customer Name
 * @param description Charge Description
 * @param created_at Charge creation timestamp
 * @param status Charge price in satoshis
 * @param amount Charge price in satoshis
 * @param callback_url URL to receive webhooks
 * @param success_url URL to redirect the user after payment
 * @param order_id External order ID (use your platform ID)
 * @param notes Order notes
 * @param currency Charge's currency
 * @param source_fiat_value Charge's currency
 * @param fiat_value Charge's currency value at issue time
 * @param auto_settle Charge requested instant exchange
 * @param lightning_invoice Invoice for lightning
 * @param chain_invoice Invoice for bitcoin
 * @param notif_email
 */
case class ChargeResponseData(
    id: String,
    name: Option[String],
    description: Option[String],
    created_at: Option[Long],
    status: Option[String], // unpaid, paid, processing
    amount: Option[Long],
    callback_url: Option[String],
    success_url: Option[String],
    order_id: Option[String],
    notes: Option[String],
    currency: Option[String],
    source_fiat_value: Option[Long],
    fiat_value: Option[Double],
    auto_settle: Option[Boolean],
    lightning_invoice: Option[LightningInvoice],
    chain_invoice: Option[ChainInvoice],
    notif_email: Option[String]
) {
  def tokenId: String = this.order_id.get.split('|').head
  def orderId: String = this.order_id.get.split('|').last
}
