# Scala OpenNode

A library for utilizing [OpenNode](https://opennode.co)'s amazing api.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.giftedprimate/scala_opennode_2.12/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/com.github.giftedprimate/scala_opennode_2.12/rsql-parser)

## Installation

Add the following to your `build.sbt`

```scala
libraryDependencies += "com.github.giftedprimate" % "scala_opennode_2.12" % "<current version>"
```

## Usage

```scala
import com.github.scala_opennode.OpenNode
import com.github.scala_opennode.entities._

object Example extends App {
    val openNode = new OpenNode(
        apiKey = "your-opennode-api-key", // required  
        successUrl = Some("https://placetoredirectto.com"), // optional
        callbackUrl = Some("https://yourcallbackforpaymentupdates.com"), // optional
        mode = "dev" // required <dev || normal>
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
   * POST https://dev-api.opennode.co/v2/withdrawals
   * Minimum withdrawal for on-chain is $20
   * No minimum for LN
   */
   openNode.initiateWithdrawal(
       `type` = "ln", // 'ln' or 'chain'
       amount = 10000L,
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
    
  /**
   * Account balance
   * GET https://api.opennode.co/v1/account/balance
   */
   openNode.accountBalance() map {
    case Left(accountBalanceData) => accountBalanceData // success
    case Right(openNodeError) => openNodeError // error
   }
}
```

## Todos
- [ ] Use [Ngrok](https://ngrok.com) to create a tunnel for OpenNode to call during development
- [ ] Create a config option that can read from the environment for OpenNode class params
- [x] Convert all `Int` types to `Long`
- [ ] Extend test coverage to handle Lightning transaction (Lightning works fine, this is just referring to testing)

## Development

### Prerequisites
  - Java -v 1.8
    - [Java](https://java.com/en/download/)
  - Scala -v 2.12.2
    - [Scala](https://www.scala-lang.org/download/)
  - Lightning(testnet) full node locally
    - Optional - for unit testing
    - [Setup Instructions](https://andrewgriffithsonline.com/blog/180330-how-to-setup-a-lightning-node/)
    
### Api Keys Needed
 - [OpenNode(dev)](https://dev.opennode.co)

### Tests
In order to run unit tests you must have a lightning/bitcoin(testnet) running locally as well as a OpenNode account. 
Before running tests be sure to fund your wallet locally with at least $100 worth of tBTC.

```
>  sbt test
```

#### OpenNodeSpec
Currently this spec will create a charge then fund the charge then immediately withdrawal the funds back into your
local wallet. The test is only for `chain` transactions, this is because there isn't an effective way to test LN
without a consistent node running, this will be implemented later. 

### Run App

```
> sbt run
```

