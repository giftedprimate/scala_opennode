package com.github.scala_opennode.entities

import play.api.libs.json.{Json, OFormat}

object ChargeInfoData {
  implicit val chargesResponseData: OFormat[ChargeInfoData] = Json.format[ChargeInfoData]
}

/**
 *
 * @param id - Charge ID
 * @param name - Charge name (this is never sent back from opennode)
 * @param description - Charge description
 * @param amount - Charge  price in satoshis
 * @param status - Charge status
 * @param created_at - number
 * @param fee - Charge fee in satoshis
 * @param fiat_value - Charge value at issue time
 * @param notes -  Charge notes
 * @param auto_settle - Charge requested instant exchange
 * @param chain_invoice - On chain invoice
 * @param lightning_invoice - Lightning invoice
 */
case class ChargeInfoData(
    id: String,
    name: Option[String],
    description: Option[String],
    amount: Option[Long],
    status: Option[String],
    created_at: Option[Long],
    fee: Option[Long],
    fiat_value: Option[Double],
    notes: Option[String],
    auto_settle: Option[Boolean],
    chain_invoice: Option[ChainInvoice],
    lightning_invoice: Option[LightningInvoice]
)
