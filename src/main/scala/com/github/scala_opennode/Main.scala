package com.github.scala_opennode

import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {
  val openNode = new OpenNode(sys.env("mathbot_payments_openNode_apiKey"))
  /*
  openNode.generateCharge(amount = 1,
                          description = "testing lib",
                          customerEmail = "1@1.com",
                          customerName = "fred",
                          autoSettle = false) map {
    case Left(chargeResponseData) => println(Json.toJson(chargeResponseData))
    case Right(openNodeError) => println(Json.toJson(openNodeError))
  }
  openNode.availableCurrencies() map {
    case Left(currentExchangeRatesData) => println(currentExchangeRatesData)
    case Right(openNodeError) => println(openNodeError)
  }
 */
}
