package com.github.scala_opennode

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.scala_opennode.entities._
import org.apache.logging.log4j.scala.Logging
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.JsonBodyWritables._
import play.api.libs.ws.ahc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OpenNode(apiKey: String,
               successUrl: Option[String] = None,
               callbackUrl: Option[String] = None,
               contentType: String = "application/json",
               mode: String)
    extends Logging {
  if (mode != "dev" && mode != "normal") throw new Exception("Mode must be set to either 'dev' of 'normal'")
  // Create Akka system for thread and streaming management
  implicit val system: ActorSystem = ActorSystem()

  system.registerOnTermination {
    System.exit(0)
  }
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  private val openNodeUrl = mode match {
    case "dev" => "https://dev-api.opennode.co"
    case "normal" => "https://api.opennode.co"
  }

  private def http(endPoint: String, method: String, jsonBody: Option[JsValue] = None) = {
    val base = StandaloneAhcWSClient()
      .url(openNodeUrl + endPoint)
      .addHttpHeaders("Content-Type" -> contentType, "Authorization" -> apiKey)
    method match {
      case "post" =>
        if (jsonBody.isEmpty) throw new Exception("jsonBody required for post requests")
        else base.post(jsonBody.get)
      case "get" => base.get()
    }
  }

  private def processError(jsValue: JsValue): OpenNodeError = jsValue.validate[OpenNodeError].asOpt match {
    case Some(openNodeError) => openNodeError
    case None =>
      logger.warn(s"[scala_opennode] $jsValue could not be parsed.")
      OpenNodeError(success = false, message = s"response from opennode is un-parsable $jsValue")
  }

  /**
   * Generate a Charge
   * Use this endpoint to generate a charge. This charge is payable through the Lightning Network or an on-chain bitcoin transaction.
   * POST https://dev-api.opennode.co/v1/charges
   */
  def generateCharge(orderId: String = UUID.randomUUID().toString,
                     amount: Long,
                     description: String,
                     customerEmail: String,
                     customerName: String,
                     autoSettle: Boolean): Future[Either[ChargeResponseData, OpenNodeError]] = {
    val body =
      ChargeRequest(
        description = description,
        amount = amount,
        order_id = orderId,
        success_url = successUrl,
        callback_url = callbackUrl,
        customer_email = customerEmail,
        customer_name = customerName,
        auto_settle = autoSettle
      )
    for {
      response <- http("/v1/charges", "post", Some(Json.toJson(body)))
    } yield {
      val parsedJson = Json.parse(response.body)
      parsedJson.validate[ChargeResponse].asOpt match {
        case Some(ChargeResponse(chargeResponseData)) => Left(chargeResponseData)
        case None => Right(processError(parsedJson))
      }
    }
  }

  /**
   * All Paid Charges
   * GET https://dev-api.opennode.co/v1/charges
   * Use this endpoint to fetch all paid and processing charges (charges awaiting 1-confirmation).
   */
  def getCharges(): Future[Either[List[ChargeInfoData], OpenNodeError]] = {
    for {
      response <- http("/v1/charges", "get")
    } yield {
      val parsedJson = Json.parse(response.body)
      parsedJson.validate[ChargesInfoResponse].asOpt match {
        case Some(ChargesInfoResponse(chargeInfoData)) => Left(chargeInfoData)
        case None => Right(processError(parsedJson))
      }
    }
  }

  /**
   * Charge Info
   * GET https://dev-api.opennode.co/v1/charge/:id
   */
  def getCharge(id: String): Future[Either[ChargeInfoData, OpenNodeError]] = {
    for {
      response <- http(s"/v1/charge/$id", "get")
    } yield {
      val parsedJson = Json.parse(response.body)
      parsedJson.validate[ChargeInfoResponse].asOpt match {
        case Some(ChargeInfoResponse(chargeInfoData)) => Left(chargeInfoData)
        case None => Right(processError(parsedJson))
      }
    }
  }

  /**
   * Initiate a Withdrawal
   * POST https://dev-api.opennode.co/v1/withdrawals
   * Minimum withdrawal for on-chain is $20
   * No minimum for LN
   */
  def initiateWithdrawal(`type`: String,
                         amount: Long,
                         address: String): Future[Either[WithdrawalResponseData, OpenNodeError]] = {
    val body = WithdrawalRequest(`type`, amount, address, callbackUrl)
    for {
      response <- http("/v2/withdrawals", "post", Some(Json.toJson(body)))
    } yield {
      val parsedJson = Json.parse(response.body)
      parsedJson.validate[WithdrawalResponse].asOpt match {
        case Some(WithdrawalResponse(withdrawalResponseData)) => Left(withdrawalResponseData)
        case None => Right(processError(parsedJson))
      }
    }
  }

  /**
   * All Withdrawals
   * GET https://dev-api.opennode.co/v1/withdrawalsJson.parse(
   */
  def getAllWithdrawals(): Future[Either[List[WithdrawalInfoData], OpenNodeError]] = {
    for {
      response <- http("/v1/withdrawals", "get")
    } yield {
      val parsedJson = Json.parse(response.body)
      parsedJson.validate[WithdrawalsInfoResponse].asOpt match {
        case Some(WithdrawalsInfoResponse(withdrawalInfoData)) => Left(withdrawalInfoData)
        case None => Right(processError(parsedJson))
      }
    }
  }

  /**
   * Withdrawal Info
   * GET https://dev-api.opennode.co/v1/withdrawal/:id
   */
  def withdrawalInfo(id: String): Future[Either[WithdrawalInfoData, OpenNodeError]] = {
    for {
      response <- http(s"/v1/withdrawal/$id", "get")
    } yield {
      val parsedJson = Json.parse(response.body)
      parsedJson.validate[WithdrawalInfoResponse].asOpt match {
        case Some(WithdrawalInfoResponse(withdrawalInfoData)) => Left(withdrawalInfoData)
        case None => Right(processError(parsedJson))
      }
    }
  }

  /**
   * Current Exchange Rates
   * GET https://dev-api.opennode.co/v1/rates
   */
  def currentExchangeRates(): Future[Either[CurrentExchangeRatesData, OpenNodeError]] = {
    for {
      response <- http("/v1/rates", "get")
    } yield {
      val parsedJson = Json.parse(response.body)
      parsedJson.validate[CurrentExchangeRatesData].asOpt match {
        case Some(currentExchangeRatesData) => Left(currentExchangeRatesData)
        case None => Right(processError(parsedJson))
      }
    }
  }

  /**
   * Available Currencies
   * GET https://dev-api.opennode.co/v1/currencies
   */
  def availableCurrencies(): Future[Either[AvailableCurrenciesData, OpenNodeError]] = {
    for {
      response <- http("/v1/currencies", "get")
    } yield {
      val parsedJson = Json.parse(response.body)
      parsedJson.validate[AvailableCurrenciesData].asOpt match {
        case Some(availableCurrenciesData) => Left(availableCurrenciesData)
        case None => Right(processError(parsedJson))
      }
    }
  }

  /**
   * Account balance
   * GET https://api.opennode.co/v1/account/balance
   */
  def accountBalance(): Future[Either[AccountBalanceData, OpenNodeError]] = {
    for {
      response <- http("/v1/account/balance", "get")
    } yield {
      val parsedJson = Json.parse(response.body)
      parsedJson.validate[AccountBalanceData].asOpt match {
        case Some(accountBalanceData) => Left(accountBalanceData)
        case None => Right(processError(parsedJson))
      }
    }
  }
}
