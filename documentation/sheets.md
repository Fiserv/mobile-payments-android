# Sheets
Sheets integration is as simple as firing an intent and handling the response.  This is a small handful of steps, but they are all standard aspects of Android development.

## Launching the UI
First, you must define the `Intent` directed to `MobilePaymentsPurchaseActivity`
```
val intent = Intent(this@MainActivity, MobilePaymentsPurchaseActivity::class.java)
```

Once defined, you must add the parameters as Extras to the Intent, as so:
```
val amount = 15.84
intent.putExtra(AMOUNT_KEY, amount)
intent.putExtra(CUSTOMER_ID_KEY, "<CUSTOMER_ID>")
intent.putExtra(TRANSACTION_TYPE_KEY, TransactionType.SALE)
intent.putExtra(CLIENT_TRANSACTION_ID_KEY, true)
intent.putExtra(MERCHANT_REFERENCE_KEY, true)
intent.putExtra(CREDIT_CARD_REQUIRE_CVV_KEY, true)
intent.putExtra(CREDIT_CARD_ADDRESS_MODE_KEY, CreditCardDetailsAddressMode.POSTAL_CODE)
intent.putExtra(CREDIT_CARD_ADDRESS_KEY, address.toJson().toString())
```

### Parameters
  * Amount 
    * A Double that represents the amount to charge the selected Payment method.  If this value is not set, then the Activity will immediately close and return RESULT_CANCELED
  * **(Optional)** Customer ID
    * A unique alphanumeric string identifying a single user in order to access previously saved Credit Cards and save new ones for a future transaction.  This value is the same mentioned in Step 1, and can be omitted if set through `MobilePayments.setCustomerId`, or if you do not wish to allow users to save Credit Cards and use them again in future.
  * **(Optional)** Transaction Type
    * The type of Payment you are seeking to collect.  The options are `TransactionType.SALE` or `TransactionType.AUTH`.  Simply put, `SALE` is used to collect funds immediately, while `AUTH` will reserve funds on the payment method, but will not collect them until a `CAPTURE` transaction is run in the future. MobilePayments will default to SALE if this parameter is not set.
  * **(Optional)** Client Transaction Id
    * An identifier for the transaction, used for tracking purposes. Defaults to a randomly generated UUID if not supplied.
  * **(Optional)** Merchant Reference
    * A reference value for the transaction, usually the ticket or order number
  * **(Optional)** Credit Card Require CVV
    * Flag to require CVV when re-using previously saved `CreditCards` for this transaction
  * **(Optional)** Credit Card Address Mode
    * Mode for entering Credit Card billing address.  Options are `POSTAL_CODE`, `FULL_ADDRESS`, and `NONE`
  * **(Optional)** Credit Card Address
    * `Address` to use as the billing address for this transaction.  To be paired with `CreditCardDetailsAddressMode.NONE`


The intent declaration must then be fed to an `ActivityResultLauncher` in your Activity.  This is defined as follows:
```
private val activityLauncher = registerForActivityResult(contract = StartActivityForResult(),
  callback ={ result: ActivityResult? ->
    //TODO
  }
)
```

And when ready to launch the UI, you pass your Intent to the `ActivityResultLauncher` as shown here:
```
activityLauncher.launch(intent)
```

## Receiving the Response
When the user has finished and the `MobilePaymentsPurchaseActivity` has completed, it will return a result to the `ActivityResultLauncher` using the callback fed into it when it was created.  This `ActivityResult` contains a code and an optional `Intent` containing information provided by the `MobilePaymentsPurchaseActivity`.  It is up to you to parse the response and react accordingly.

An example looks like this:
```
private val activityLauncher = registerForActivityResult(contract = StartActivityForResult(),
  callback ={ result: ActivityResult? ->
    if( result?.resultCode == RESULT\_OK ){
      //Transaction was complated
      val transactionString = result.data?.getStringExtra(TRANSACTION\_KEY)
      if( transactionString != null ){
        val jsonHandler = Json{ ignoreUnknownKeys = true }
        val transaction = jsonHandler.decodeFromString<Transaction>(transactionString!!)
        model.updateTransactionMessage("Transaction ID: ${transaction.transactionId}\\nAmount: ${transaction.amount}")
      }
    }else if( result?.resultCode == RESULT\_CANCELED ){
      //User backed out or no amount was provided; transaction is not completed
    }
  }
)
```

The `resultCode` of the response will always be either `RESULT\_OK` for a successful payment, or `RESULT\_CANCELED` for any other scenario.  Errors during the transaction, such as from incorrect Credit Card information are handled inside the `MobilePaymentsPurchaseActivity`.

Once there is a successful payment, a Transaction object is serialized into a JSON String and returned inside the data Intent in the `ActivityResult` for your records and for future processing should it prove necessary.
