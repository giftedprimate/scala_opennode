package com.github.scala_opennode.entities

import play.api.libs.json._

object AccountBalanceData {
  implicit val accountBalanceReads: Reads[AccountBalanceData] =
    (__ \ "data").read[Map[String, Map[String, Double]]].map(AccountBalanceData(_))
}

case class AccountBalanceData(
    data: Map[String, Map[String, Double]]
)
