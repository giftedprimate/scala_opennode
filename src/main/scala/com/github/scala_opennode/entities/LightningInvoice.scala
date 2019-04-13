package com.github.scala_opennode.entities

import play.api.libs.json.{Json, OFormat}

object LightningInvoice {
  implicit val lightningInvoiceFormat: OFormat[LightningInvoice] = Json.format[LightningInvoice]
}

/**
 * @param expires_at LN charge expiration. Renewed automatically when /orders/{ID} called.
 * @param payreq Payment Request hash
 *
 */
case class LightningInvoice(
    expires_at: Option[Long], // charge
    payreq: String, // charge charges
    settled_at: Option[Long] // charges
)
