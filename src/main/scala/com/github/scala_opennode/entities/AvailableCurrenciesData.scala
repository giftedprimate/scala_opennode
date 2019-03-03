package com.github.scala_opennode.entities

import play.api.libs.json._

object AvailableCurrenciesData {
  implicit val avaialableCurrenciesDataReads: Reads[AvailableCurrenciesData] =
    (__ \ "data").read[List[String]].map(AvailableCurrenciesData(_))
}

case class AvailableCurrenciesData(data: List[String])
