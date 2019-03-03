package com.github.scala_opennode

import com.github.scala_opennode.entities._
import org.scalatest.AsyncFlatSpec
import play.api.libs.json.{Json, OFormat}
import org.apache.logging.log4j.scala.Logging

import scala.sys.process._

object OpenNodeSpec {
  var chargeResponseDataOpt: Option[ChargeResponseData] = None
  var withdrawalId: Option[String] = None
}

class OpenNodeSpec extends AsyncFlatSpec with Logging {
  import OpenNodeSpec._
  val openNode = new OpenNode(sys.env("mathbot_payments_openNode_apiKey"))

  object Transaction {
    implicit val transactionFormat: OFormat[Transaction] = Json.format[Transaction]
  }

  case class Transaction(
      address: Option[String],
      category: Option[String],
      amount: Option[Double],
      label: Option[String],
      vout: Option[Int],
      fee: Option[Double],
      confirmations: Option[Int],
      blockhash: Option[String],
      blockindex: Option[Int],
      blocktime: Option[Int],
      txid: Option[String],
      walletconflicts: Option[List[String]],
      time: Option[Int],
      timereceived: Option[Int],
      `bip125-replaceable`: Option[String],
      abandoned: Option[Boolean]
  )

  behavior of "generateCharge"
  it should "respond with ChargeResponseData when given proper inputs" in {
    for {
      chargeResponseData <- openNode.generateCharge(amount = 753394,
                                                    description = "unit test",
                                                    customerEmail = "1@1.com",
                                                    customerName = "fred",
                                                    autoSettle = false)
    } yield {
      chargeResponseDataOpt = Some(chargeResponseData.left.get)
      assert(chargeResponseData.isLeft && chargeResponseData.left.get.isInstanceOf[ChargeResponseData])
    }
  }
  it should "respond with OpenNodeError with bad input" in {
    for {
      openNodeError <- openNode.generateCharge(amount = 0,
                                               description = "unit test",
                                               customerEmail = "1@1.com",
                                               customerName = "fred",
                                               autoSettle = false)
    } yield assert(openNodeError.isRight && openNodeError.right.get.isInstanceOf[OpenNodeError])
  }

  "bitcoin-cli" should "be able to pay the charge" in {
    chargeResponseDataOpt match {
      case Some(chargeResponseData) =>
        val txId =
          s"bitcoin-cli sendtoaddress ${chargeResponseData.chain_invoice.get.address} ${BigDecimal(chargeResponseData.amount.get.toDouble / 100000000.0)}".!!
        assert(txId.isInstanceOf[String])
      case None => fail()
    }
  }

  behavior of "getCharges"
  it should "respond with List[ChargeInfoData]" in {
    for {
      chargeInfoData <- openNode.getCharges()
    } yield assert(chargeInfoData.isLeft && chargeInfoData.left.get.isInstanceOf[List[ChargeInfoData]])
  }

  behavior of "getCharge"
  it should "respond with ChargeInfoData when sent valid ID" in {
    for {
      chargeInfoData <- openNode.getCharge(chargeResponseDataOpt.get.id)
    } yield assert(chargeInfoData.isLeft && chargeInfoData.left.get.isInstanceOf[ChargeInfoData])
  }
  it should "respond with OpenNodeError if sent invalid id" in {
    for {
      openNodeError <- openNode.getCharge("not-an-id")
    } yield assert(openNodeError.isRight && openNodeError.right.get.isInstanceOf[OpenNodeError])
  }

  behavior of "initiateWithdrawal"
  it should "respond with WithdrawalResponseData when sent valid info" in {
    val address = "bitcoin-cli getnewaddress".!!.trim
    val amount = chargeResponseDataOpt.get.amount.get
    for {
      withdrawalResponseData <- openNode.initiateWithdrawal(
        `type` = "chain",
        amount = amount,
        address = address
      )
    } yield {
      withdrawalId = Some(withdrawalResponseData.left.get.id)
      assert(withdrawalResponseData.isLeft && withdrawalResponseData.left.get.isInstanceOf[WithdrawalResponseData])
    }
  }

  behavior of "getAllWithdrawals"
  it should "respond with List[WithdrawalInfoData" in {
    for {
      withdrawalInfoData <- openNode.getAllWithdrawals()
    } yield assert(withdrawalInfoData.isLeft && withdrawalInfoData.left.get.isInstanceOf[List[WithdrawalInfoData]])
  }

  behavior of "withdrawalInfo"
  it should "respond with WithdrawalInfoData when sent a valid ID" in {
    for {
      withdrawalInfoData <- openNode.withdrawalInfo(withdrawalId.get)
    } yield assert(withdrawalInfoData.isLeft && withdrawalInfoData.left.get.isInstanceOf[WithdrawalInfoData])
  }

  behavior of "currentExchangeRates"
  it should "respond with CurrentExchangeRatesData" in {
    for {
      currentExchangeRatesData <- openNode.currentExchangeRates()
    } yield
      assert(
        currentExchangeRatesData.isLeft && currentExchangeRatesData.left.get.isInstanceOf[CurrentExchangeRatesData]
      )
  }

  behavior of "availableCurrencies"
  it should "respond with AvailableCurrenciesData" in {
    for {
      availableCurrenciesData <- openNode.availableCurrencies()
    } yield
      assert(
        availableCurrenciesData.isLeft && availableCurrenciesData.left.get.isInstanceOf[AvailableCurrenciesData]
      )
  }
}
