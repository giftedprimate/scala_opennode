package com.github.scala_opennode

import entities._
import org.scalatest.concurrent.Waiters
import org.scalatest.time.SpanSugar._
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.test.Injecting
import scala.math.BigDecimal
import sys.process._
import scala.concurrent.ExecutionContext.Implicits.global
import com.github.scala_opennode.OpenNode

class OpenNodeSpec extends PlaySpec with Waiters with GuiceOneAppPerSuite with Injecting {
  val openNode = new OpenNode(sys.env("mathbot_payments_openNode_apiKey"))

  var chargeResponseDataOpt: Option[ChargeResponseData] = None
  var withdrawalId: Option[String] = None

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
  "generateCharge" must {
    "respond with correct data with proper inputs" in {
      val wait = new Waiter
      openNode.generateCharge(amount = 1,
                              description = "unit test",
                              customerEmail = "1@1.com",
                              customerName = "fred",
                              autoSettle = false) map {
        case Left(chargeResponseData) =>
          chargeResponseDataOpt = Some(chargeResponseData)
          wait { assert(chargeResponseData.isInstanceOf[ChargeResponseData]) }
          wait.dismiss()
        case Right(_) =>
          wait { fail() }
          wait.dismiss()
      }
      wait.await(timeout(3.minutes))
    }

    "respond with OpenNodeError with bad input" in {
      val wait = new Waiter
      openNode.generateCharge(amount = 0,
                              description = "unit test",
                              customerEmail = "1@1.com",
                              customerName = "fred",
                              autoSettle = false) map {
        case Left(_) =>
          wait { fail() }
          wait.dismiss()
        case Right(openNodeError) =>
          wait { assert(openNodeError.isInstanceOf[OpenNodeError]) }
          wait.dismiss()
      }
      wait.await(timeout(3.seconds))
    }
  }

  "be able to pay charge" in {
    chargeResponseDataOpt match {
      case Some(chargeResponseData) =>
        val txId =
          s"bitcoin-cli sendtoaddress ${chargeResponseData.chain_invoice.get.address} ${BigDecimal(chargeResponseData.amount.get.toDouble / 100000000.0)}".!!
        assert(txId.isInstanceOf[String])
      case None => fail()
    }
  }

  "getCharges" must {
    "respond with correct data" in {
      val wait = new Waiter
      openNode.getCharges() map {
        case Left(chargeInfoDatas) =>
          wait { assert(chargeInfoDatas.isInstanceOf[List[ChargeInfoData]]) }
          wait.dismiss()
        case Right(_) =>
          wait { fail() }
      }
      wait.await(timeout(3.seconds))
    }
  }

  "getCharge" must {
    "respond with correct data if sent a valid id" in {
      val wait = new Waiter
      chargeResponseDataOpt match {
        case Some(chargeResponseData) =>
          openNode.getCharge(chargeResponseData.id) map {
            case Left(chargeInfoData) =>
              wait { assert(chargeInfoData.isInstanceOf[ChargeInfoData]) }
              wait.dismiss()
            case Right(_) =>
              wait { fail() }
          }
        case None => wait { fail() }
      }
      wait.await(timeout(3.seconds))
    }

    "respond with OpenNodeError if sent an invalid id" in {
      val wait = new Waiter
      openNode.getCharge("not-an-id-at-all") map {
        case Left(_) => wait { fail() }
        case Right(openNodeError) =>
          wait { assert(openNodeError.isInstanceOf[OpenNodeError]) }
          wait.dismiss()
      }
      wait.await(timeout(3.seconds))
    }
  }

  "initiateWithdrawal" must {
    "respond with correct data when sent property address" in {
      val wait = new Waiter
      val address = "bitcoin-cli getnewaddress".!!.trim
      val amount = chargeResponseDataOpt.get.amount.get // using get here since earlier tests will fail if None
      openNode.initiateWithdrawal(
        `type` = "chain",
        amount = amount,
        address = address
      ) map {
        case Left(withdrawalResponseData) =>
          withdrawalId = Some(withdrawalResponseData.id)
          wait { assert(withdrawalResponseData.isInstanceOf[WithdrawalResponseData]) }
          wait.dismiss()
        case Right(_) =>
          wait { fail() }
      }
      wait.await(timeout(3.seconds))
    }
  }

  "getAllWithdrawals" must {
    "respond with correct data" in {
      val wait = new Waiter
      openNode.getAllWithdrawals() map {
        case Left(withdrawalInfoResponses) =>
          wait { assert(withdrawalInfoResponses.isInstanceOf[List[WithdrawalInfoData]]) }
          wait.dismiss()
        case Right(_) => wait { fail() }
      }
      wait.await(timeout(3.seconds))
    }
  }

  "withdrawalInfo" must {
    "respond with correct data if sent a valid id" in {
      val wait = new Waiter
      val id = withdrawalId.get
      openNode.withdrawalInfo(id) map {
        case Left(chargeInfoData) =>
          wait {
            assert(chargeInfoData.isInstanceOf[WithdrawalInfoData])
            assert(chargeInfoData.id == id)
            assert(chargeInfoData.status.contains("pending") || chargeInfoData.status.contains("confirmed"))
          }
          wait.dismiss()
        case Right(_) => wait { fail() }
      }
      wait.await(timeout(3.seconds))
    }
  }
}
