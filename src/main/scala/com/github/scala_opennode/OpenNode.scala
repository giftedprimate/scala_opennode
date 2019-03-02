package com.github.scala_opennode

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.scala_opennode.entities._
import org.apache.logging.log4j.scala.Logging
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.JsonBodyWritables._
import play.api.libs.ws.ahc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class OpenNode(apiKey: String,
               successUrl: Option[String] = None,
               callbackUrl: Option[String] = None,
               contentType: String = "application/json")
    extends Logging {
  // Create Akka system for thread and streaming management
  implicit val system: ActorSystem = ActorSystem()

  system.registerOnTermination {
    System.exit(0)
  }
  implicit val materializer = ActorMaterializer()

  val http = StandaloneAhcWSClient()

  private def processError(jsValue: JsValue): OpenNodeError = jsValue.validate[OpenNodeError].asOpt match {
    case Some(openNodeError) => openNodeError
    case None =>
      logger.warn(s"[scala_opennode] $jsValue could not be parsed.")
      OpenNodeError(success = false, message = "response from opennode is un-parsable")
  }

  val openNodeUrl = "https://dev-api.opennode.co/v1/"

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
        callback_url = callbackUrl.map(cb => s"$cb/$orderId"),
        customer_email = customerEmail,
        customer_name = customerName,
        auto_settle = autoSettle
      )
    for {
      response <- http
        .url(s"$openNodeUrl/charges")
        .addHttpHeaders("Content-Type" -> contentType, "Authorization" -> apiKey)
        .post(Json.toJson(body))
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
      response <- http
        .url(s"$openNodeUrl/charges")
        .addHttpHeaders("Content-type" -> contentType, "Authorization" -> apiKey)
        .get()
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
      response <- http
        .url(s"$openNodeUrl/charge/$id")
        .addHttpHeaders("Content-type" -> contentType, "Authorization" -> apiKey)
        .get()
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
                         amount: Int,
                         address: String): Future[Either[WithdrawalResponseData, OpenNodeError]] = {
    val body = WithdrawalRequest(`type`, amount, address)
    for {
      response <- http
        .url(s"$openNodeUrl/withdrawals")
        .addHttpHeaders("Content-type" -> contentType, "Authorization" -> apiKey)
        .post(Json.toJson(body))
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
      response <- http
        .url(s"$openNodeUrl/withdrawals")
        .addHttpHeaders("Content-type" -> contentType, "Authorization" -> apiKey)
        .get()
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
      response <- http
        .url(s"$openNodeUrl/withdrawal/$id")
        .addHttpHeaders("Content-type" -> contentType, "Authorization" -> apiKey)
        .get()
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
   * todo -> implement if needed
   */
  //  def currentExchangeRates() = ???

  /**
   * Available Currencies
   * GET https://dev-api.opennode.co/v1/currencies
   * todo -> implement if needed
   */
  //  def availableCurrencies() = ???
}
