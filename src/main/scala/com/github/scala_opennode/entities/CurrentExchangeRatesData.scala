package com.github.scala_opennode.entities
import play.api.libs.json._

object CurrentExchangeRatesData {
  implicit val currencyExchangeRatesReads: Reads[CurrentExchangeRatesData] =
    (__ \ "data").read[Map[String, Map[String, Double]]].map(CurrentExchangeRatesData(_))
}

case class CurrentExchangeRatesData(
    data: Map[String, Map[String, Double]]
)
