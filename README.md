# Scala OpenNode

A library for utilizing [OpenNode](https://opennode.co)'s amazing api.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.giftedprimate/scala_opennode_2.12/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/com.github.giftedprimate/scala_opennode_2.12/rsql-parser)

##Installation

Add the following to your `build.sbt`

```scala
libraryDependencies += "com.github.giftedprimate" % "scala_opennode_2.12" % "0.1"
```

##Usage

```scala
import com.github.scala_opennode.OpenNode
import com.github.scala_opennode.entities._

object Example extends App {
    val openNode = new OpenNode(
        apiKey = "your-opennode-api-key",  
        successUrl = Some("https://placetoredirectto.com"),
        callbackUrl = Some("https://yourcallbackforpaymentupdates.com")
    )
    
    /**
    * Generate a Charge
    * Use this endpoint to generate a charge. This charge is payable through the Lightning Network or an on-chain bitcoin transaction.
    * POST https://dev-api.opennode.co/v1/charges
    */ 
    openNode.generateCharge(
      amount = 100000L, 
      description = "just because you are awesome", 
      customerEmail = "fred@fred.com", 
      customerName = "fred fanagle", 
      autoSettle = false
    ) map {
        case Left(chargeResponseData) => chargeResponseData // success
        case Right(openNodeError) => openNodeError // error 
    }
  
    /**
     * All Paid Charges
     * GET https://dev-api.opennode.co/v1/charges
     * Use this endpoint to fetch all paid and processing charges (charges awaiting 1-confirmation).
     */
     openNode.getCharges() map {
        case Left(chargeInfoDatas) => chargeInfoDatas // success
        case Right(openNodeError) => openNodeError // error 
     }
     
     
    /**
    * Charge Info
    * GET https://dev-api.opennode.co/v1/charge/:id
    */
    openNode.getCharge(id = "qwerty-1234") map {
        case Left(chargeInfoData) => chargeInfoData // success
        case Right(openNodeError) => openNodeError // error 
    }
    
    
  /**
   * Initiate a Withdrawal
   * POST https://dev-api.opennode.co/v1/withdrawals
   * Minimum withdrawal for on-chain is $20
   * No minimum for LN
   */
   openNode.initiateWithdrawal(
       `type` = "ln", // 'ln' or 'chain'
       amount = 10000,
       address = "qwer;safo9y9yihfagh"
   ) map {
       case Left(withdrawalResponseData) => withdrawalResponseData // success
       case Right(openNodeError) => openNodeError // error 
   }
   
    /**
    * All Withdrawals
    * GET https://dev-api.opennode.co/v1/withdrawalsJson.parse(
    */
    openNode.getAllWithdrawals() map {
       case Left(withdrawalInfoDatas) => withdrawalInfoDatas // success
       case Right(openNodeError) => openNodeError // error
    }
    
    /**
    * Withdrawal Info
    * GET https://dev-api.opennode.co/v1/withdrawal/:id
    */
    openNode.withdrawalInfo(id = "qwerty-1234") map {
       case Left(withdrawalInfoData) => withdrawalInfoData // success
       case Right(openNodeError) => openNodeError // error
    }
    
    /**
    * Current Exchange Rates
    * GET https://dev-api.opennode.co/v1/rates
    */
    openNode.currentExchangeRates() map {
       case Left(currentExchangeRatesData) => currentExchangeRatesData // success
       case Right(openNodeError) => openNodeError // error
    }
    
    /**
    * Available Currencies
    * GET https://dev-api.opennode.co/v1/currencies
    */
    openNode.availableCurrencies() map {
       case Left(availableCurrenciesData) => availableCurrenciesData // success
       case Right(openNodeError) => openNodeError // error
    }
}
```

## Todos
- [ ] Use [Ngrok](https://ngrok.com) to create a tunnel for OpenNode to call during development
- [ ] Create a config option that can read from the environment for OpenNode class params
- [ ] Convert all `Int` types to `Long`
