# Direct
Direct integration is the most powerful and flexible of the integrations available, but that comes with some tradeoffs.  You have complete freedom to build any UI design, but you will need to manage additional elements of the design, such as UI state, and provide the necessary information from the UI to the MobilePayments SDK.  Once you have constructed the UI to your specifications, you then need to invoke the relevant Mobile Payments API where appropriate.

The MobilePayments APIs are broken into several distinct categories, which you can find below.

### Table of Contents
  * [Credit Cards](#credit-cards)
  * [Payments](#payments)
  * [General](#general)

## Credit Cards
Credit Cards are handled through the `CreditCardManager` object.  This is a singleton reference to contain all Credit Card interactions with the MobilePayments API.  Within it, there are three functions.

### Retrieving Saved Credit Cards
```
retrieveCreditCards(customerId: String, response: Response<List<CreditCard>>?)
```
This method will query the remote server for all Credit Cards saved to the provided User ID value and return to the result to the provided `Response`.  On a successful query, the result will also be stored in memory, accessible through `CreditCardManager.creditCards`
  * Customer ID
    * A unique alphanumeric string identifying a single customer
  * **(OPTIONAL)** Response
    * The Response object to receive the result.
      * Success will return a `List<CreditCard>`
      * Error will return a throwable, most commonly a `WebErrorSet` containing error information

### Adding a Credit Card
```
addCreditCard(creditCard: CreditCard, customerId: String?, response: Response<CreditCard>?)
```
This method will tokenize a Credit Card, translating the user-provided card information into a token with supporting information, such as the last four digits of the number and so on.  If a Customer ID value is provided, the tokenized card will be saved to that Customer ID and returned to a `CreditCardManager.retrieveCreditCards` call that is provided the same Customer ID value.
  * Credit Card
    * A CreditCard object containing all the necessary information for a Credit Card to be tokenized
  * **(OPTIONAL)** Customer ID
    * A unique alphanumeric string identifying a single customer
  * **(OPTIONAL)** Response
    * The Response object to receive the result.
      * Success will return a `CreditCard`
      * Error will return a throwable, most commonly a `WebErrorSet` containing error information

### Deleting a Credit Card
```
deleteCreditCard(creditCard: CreditCard, response: Response<Any?>?)
```
This method will delete a tokenized card from the remote server.  If it had been saved to a Customer ID, it will be removed from that reference as well.
  * Credit Card
    * A tokenized `CreditCard` object to be deleted
  * **(OPTIONAL)** Response
    * The Response object to receive the result.
      * Success will return a `CreditCard`
      * Error will return a throwable, most commonly a `WebErrorSet` containing error information

## Payments
Payments are handled through the `PaymentManager` object.  Like `CreditCardManager`, it is a singleton that contains all Payments interactions with the MobilePayments API.  It contains six functions.

### Google Pay Config
```
getGooglePayRequestConfig()
```
This method returns the Google Pay JSON object for your payment gateway.  It will detail supported payment methods, required customer information, and so on and will need only minor modifications (e.g., adding an amount to charge) to be ready to pass to Google Pay in your UI

### Payments
Payments are payment run against a `Payment` object.  There are two functions available for Payments, and both are executed against by a `Payment` object, which is defined as:
```
  Payment(amount: Double, method: PaymentMethod, clientTransactionId: String?, merchantReference: String?)
```
  * Amount
    * The amount of money to charge the provided PaymentMethod
  * Method
    * The PaymentMethod to charge against.  This can be CreditCard or GooglePay
  * **(OPTIONAL)** Client Transaction Id
    * An identifier for the transaction, used for tracking purposes. Defaults to a randomly generated UUID if not supplied.
  * **(OPTIONAL)** Merchant Reference
    * A reference value for the transaction, usually the ticket or order number

#### Sale
```
sale(payment: Payment, response: Response<Transaction>?)
```
This method will perform a `SALE` transaction against the provided `Payment`, capturing the charge and immediately transferring funds
  * **(OPTIONAL)** Response
    * The Response object to receive the result.
      * Success will return a `Transaction`
      * Error will return a throwable, most commonly a `WebErrorSet` containing error information

#### Authorize
```
auth(payment: Payment, response: Response<Transaction>?)
```
This method will perform an `AUTH` transaction against the provided `Payment`, placing an authorization on the provided `PaymentMethod` for the provided amount but not transferring funds until the transaction is captured.
  * **(OPTIONAL)** Response
    * The Response object to receive the result.
      * Success will return a `Transaction`
      * Error will return a throwable, most commonly a `WebErrorSet` containing error information

### Transactions
Transactions are operations run against previously made `Payments`.  There are two functions available, both of which require a `Transaction` object with a valid Transaction ID.
```
Transaction(transactionId: String)
```
  *  Transaction ID
    * The Mobile Payments `Transaction ID` value, provided by the `Transaction` object return to `PaymentManager` methods

#### Capture
```
capture(transaction: Transaction, response: Response<Transaction>?)
```
This method will `CAPTURE` the `Transaction`, transferring the authorized funds from the original PaymentMethod immediately
  * **(OPTIONAL)** Response
    * The Response object to receive the result.
      * Success will return a `Transaction`
      * Error will return a throwable, most commonly a `WebErrorSet` containing error information

Void
```
void(transaction: Transaction, response: Response<Transaction>?)
```
This method will `VOID` the `Transaction`, canceling the previous authorization and freeing the funds for the customer to use elsewhere.
  * **(OPTIONAL)** Response
    * The Response object to receive the result.
      * Success will return a `Transaction`
      * Error will return a throwable, most commonly a `WebErrorSet` containing error information

### Transaction Details
```
retrieveTransaction(transactionId: String, response: Response<Transaction>?)
```
This method will retrieve the current details of a transaction through its `transactionId`
  * Transaction ID
    * An identifier provided by a `Transaction` after it is processed
  * **(OPTIONAL)** Response
    * The Response object to receive the result.
      * Success will return a `Transaction`
      * Error will return a throwable, most commonly a `WebErrorSet` containing error information

## General
### MobilePayments
The `MobilePayments` class is a utility singleton for initialization and setup.  Most of its contents are for UI behavior discussed in previous tutorials.  For Direct integration, there are only two relevant functions.

#### Initialize
```
initialize(application: Application, environment: Environment, clientToken: String, businessLocationId: String?)
```
This method should be called when your app launches, ideally in the Application.onCreate method.  It must be invoked before any other interaction with the MobilePayments SDK can function.

  * Application
    * The Application instance of the host app
  * Environment
    * The Environment the MobilePayments SDK should operate in.  Possible values are Environment.SANDBOX or Environment.PRODUCTION
  * Client Token
    * The alphanumeric string value provided when you created your merchant account
  * **(OPTIONAL)** Business Location ID
    * The ID of the store to associate with any payments made during this session

#### Business Location Id
```
setBusinessLocationId(businessLocationId: String?)
```
This method will update the Business Location ID and change the store associated with any payments made afterwards.  It is a supplementary/helper function used in the event the store or location can change after initialization.

### Web Errors
Errors from the MobilePayments API are broadly handled by the `WebErrorSet` class.  This is returned to `Response.error` when an error occurred and provides a summary of the error(s) that occurred during the call.

`WebErrorSet.primaryCode` and `WebErrorSet.primaryMessage` will provide the primary driver of the error, being the error code and the message returned from the remote server.  If more information is required, `WebErrorSet.errors` is a list of `WebError` objects containing the full set of errors returned from the operation and can be examined to determine the root cause or adjust messaging to the user.

**Note:** In some rare cases, the returned error may be a `TokenExpiredException`. This is analogous to an HTTP 403 error code and means you should double check that your Client Token value provided to MobilePayments.initialize is both correct and valid.

