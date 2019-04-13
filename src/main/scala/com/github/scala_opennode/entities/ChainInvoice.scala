package com.github.scala_opennode.entities

import play.api.libs.json.{Json, OFormat}

object ChainInvoice {
  implicit val chainInvoiceFormat: OFormat[ChainInvoice] = Json.format[ChainInvoice]
}

/**
 * @param address Bitcoin address
 */
case class ChainInvoice(
    address: String, // charge charges
    settled_at: Option[Long], // charges
    tx: Option[String] // charges
)
