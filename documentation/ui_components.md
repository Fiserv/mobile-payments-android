# UI Integration
Component integration is a more in-depth, and simultaneously more flexible integration process.  To embed UI components, you first need to select the desired components for the behavior you want and attach them to your existing UI in your desired arrangement.  

### Table of Contents
  * [Credit Cards](#credit-cards)
  * [Purchase](#purchase)
  * [Google Pay](#google-pay)
  * [Utility](#utility)

## Credit Cards
Credit Cards in MobilePayments have two major UI elements, a Credit Card List primarily used for saving and managing Credit Cards associated with a provided User ID value, and a Credit Card Details UI used to collect the user’s Credit Card information and tokenize the data for usage elsewhere.

### Credit Card List
The Credit Card List is provided through the `CreditCardListView` Composable.  It can be treated the same as any other Composable, inserted into any other Composable or, if using a traditional view-based layout, into a [ComposeView](https://www.geeksforgeeks.org/kotlin/android-jetpack-compose-interoperability-using-compose-in-xml-layouts/).  When doing so, the `CreditCardListView` will look like this:
```
CreditCardListView(
  modifier = Modifier,
  model = creditCardListViewModel,
  customerId = "<CUSTOMER_ID>",
  scrollingEnabled = true,
  showSelectors = true,
  requireCvv = false,
  canAddCards = true,
  defaultEnabled = true,
  mode = CreditCardListMode.PAYMENT,
  onCreditCardSelected = {card ->
    handleCardSelection(card)
  }
)
```

#### Parameters
  * **(OPTIONAL)** Modifier
    * A standard composable Modifier, used to specify the size and layout of the CreditCardListView in the host UI.
  * **(OPTIONAL)** Model
    * A `CreditCardListViewModel` instantiated by the host UI.  Providing this to the `CreditCardListView` will hoist the List’s state and allow the host UI to access information within the UI, such as which Credit Card is currently selected, the full list of Credit Cards, and adding or removing a Credit Card from the list
  * **(OPTIONAL)** Customer ID
    * A unique alphanumeric string identifying a single user in order to access previously saved Credit Cards and save new ones for a future transaction.  This value is the same as that passed to `MobilePayments.setCustomerId`, and can be omitted if you have set it there or if you do not wish to allow users to save Credit Cards and use them again in future.
  * **(OPTIONAL)** Scrolling Enabled
    * Whether or not scrolling the List is possible.  This only matters in the event the space provided to the `CreditCardListView` through the Modifier or host UI arrangement is smaller than its contents, or the List is embedded inside another scrolling Composable.
  * **(OPTIONAL)** Show Selectors
    * Flag to show or hide the radio button selection indicator.  Defaults to true
  * **(OPTIONAL)** Requie CVV
    * Flag to require CVV value when reusing previously saved `CreditCards`.  Defaults to false
  * **(OPTIONAL)** Can Add Cards
    * Flag to allow adding a `CreditCard` through the `CreditCardListView`.  Defaults to true
  * **(OPTIONAL)** Default Enabed
    * Flag to allow the list to automatically select the user's Default `CreditCard`, if one exists.  Defaults to true
  * **(OPTIONAL)** Mode
    * Display mode of the list.  Options are `PAYMENT` and `MANAGE`.  `PAYMENT` allows Credit Cards to be selected from the list, while `MANAGE` allows only Credit Card List management actions such as add and delete cards.  Defaults to `PAYMENT`  
  * **(OPTIONAL)** On Credit Card Selected
    * A callback invoked whenever the user selects a card in the list.

#### The Model
The `CreditCardListView` is also paired with a `CreditCardListViewModel` that controls and provides access to the state of the List.  You can use this to achieve particular behaviors as part of your UI flow.  Of particular import amongst the `CreditCardListViewModel` are the following method calls:
  * refreshCreditCardList()
    * Retrieve a new copy of the list of all credit cards associated with the User ID value provided to MobilePayments, and update the UI accordingly
  * updateCreditCardList(cards: MutableList<CreditCard>)
    * Replace the currently displayed list of Credit Cards with the provided set
  * getSelectedCard(): CreditCard?
    * Return the currently selected card in the List, or null if none are selected
  * updateSelectedCard(selectedCard: CreditCard?)
    * Update the currently selected card with the provided card.  If there is no matching Credit Card, or the provided value is null, the selection will be cleared
  * addCreditCard()
    * Display the CreditCardDetailsModal to collect Credit Card information from the user and add it to the current list
  * deleteCard(card: CreditCard, listener: Response?)
    * Remove the provided Credit Card from the list and, if saved, from the User ID

### Credit Card Details
Credit Card Details is handled through two UI components.  The `CreditCardDetailsModal`, for a modal, modular display that can easily slot anywhere its needed, and the `CreditCardDetailsView` for embedding the UI directly into the host UI.

#### Modal
`CreditCardDetailsModal` is a self-contained modal UI widget designed to be a quick and easy way to slot `CreditCardDetailsView` into nearly any UI design.  It is a `ModalBottomSheet` containing the `CreditCardDetailsView` plus some supporting functionality that is as easy as [adding a Bottom Sheet to your app](https://developer.android.com/develop/ui/compose/components/bottom-sheets).

Adding the Composable looks like this:
```
if( showModal ){
  val modalModel: CreditCardDetailsModalViewModel = viewModel()
  CreditCardDetailsModal(
    model = modalModel,
    customerId = "<Customer_ID>",
    canSaveCard = true,
    address = null,
    addressMode = CreditCardDetailsAddressMode.POSTAL_CODE,
    onCardAdded = {card ->
      toggleModal = false
    },
    onDismissRequest = {
      toggleModal = false
    }
  )
}
```

##### Parameters
  * **(OPTIONAL)** Model
    * A `CreditCardDetailsModalViewModel` instantiated by the host UI.  Providing this to the `CreditCardDetailsModal` will hoist the modal's state and allow the host UI to access information within the UI as well as initiate actions within it.
  * **(OPTIONAL)** Customer ID
    * A unique alphanumeric string identifying a single user in order to access previously saved Credit Cards and save new ones for a future transaction.  This value is the same as that passed to `MobilePayments.setCustomerId`, and can be omitted if you have set it there or if you do not wish to allow users to save Credit Cards and use them again in future.
  * **(OPTIONAL)** Can Save Card
    * Flag to allow the customer to save the `CreditCard` to the provided Customer ID.  Defaults to true
  * **(OPTIONAL)** Address
    * `Address` to use as the billing address for this transaction.  To be paired with `CreditCardDetailsAddressMode.NONE`
  * **(OPTIONAL)** Address Mode
    * Mode for entering Credit Card billing address.  Options are `POSTAL_CODE`, `FULL_ADDRESS`, and `NONE`
  * **(OPTIONAL)** On Card Added
    * Listener invoked when a CreditCard is successfully tokenized and is passed to the calling UI
  * **(OPTIONAL)** On Dismiss Request
    * Listener invoked when the ModalBottomSheet system API triggers a close event.  This is standard ModalBottomSheet behavior and you will need to suppress the Composable in response to this event.

##### The Model
The CreditCardDetailsModalViewModel contains these methods of note:
  * clearContents()
    * Clears the current UI state and reverts to a fresh slate
  * updateErrorMessage(message: String?)
    * Updates the error banner at the top of the modal with a provided message, or hides it if the provided value is null

#### View
**Note:** It is strongly recommended you do not use the CreditCardDetailsView if you are already making use of the CreditCardListView with canAddCards = true or if you are leveraging the CreditCardDetailsModal in any way.

Credit Card Details is handled through the `CreditCardDetailsView`, a Composable containing a collection of input fields.  It is designed to take user text input and when prompted convert it into a tokenized `CreditCard` object and, if a Customer ID value is provided and the user allows it, save the card to the provided Customer ID.

To embed the `CreditCardDetailsView`, you must simply add the Composable where you wish in your UI, as so:
```
CreditCardDetailsView(
  modifier = Modifier,
  model = detailsModel, 
  customerId = "CUSTOMER_ID",
  canSaveCard = true,
  address = null,
  addressMode = CreditCardDetailsAddressMode.POSTAL_CODE,
  isCardReady = { isValid ->
    handleCardReady(isValid)
  }
)
```

##### Parameters
  * **(OPTIONAL)** Modifier
    * A standard composable Modifier, used to specify the size and layout of the CreditCardListView in the host UI.
  * **(OPTIONAL)** Model
    * A `CreditCardDetailsModalViewModel` instantiated by the host UI.  Providing this to the `CreditCardDetailsModal` will hoist the modal's state and allow the host UI to access information within the UI as well as initiate actions within it.
  * **(OPTIONAL)** Customer ID
    * A unique alphanumeric string identifying a single user in order to access previously saved Credit Cards and save new ones for a future transaction.  This value is the same as that passed to `MobilePayments.setCustomerId`, and can be omitted if you have set it there or if you do not wish to allow users to save
  * **(OPTIONAL)** Can Save Card
    * Flag to allow the customer to save the `CreditCard` to the provided Customer ID.  Defaults to true
  * **(OPTIONAL)** Address
    * `Address` to use as the billing address for this transaction.  To be paired with `CreditCardDetailsAddressMode.NONE`
  * **(OPTIONAL)** Address Mode
    * Mode for entering Credit Card billing address.  Options are `POSTAL_CODE`, `FULL_ADDRESS`, and `NONE`
  * **(OPTIONAL)** Is Card Ready
    * Listener invoked when the card state changes.  It receives a boolean, `true` for `CreditCard` details are valid and ready to tokenize, `false` otherwise.

##### The Model
The `CreditCardDetailsViewModel` has a collection of methods that are of import to control its behavior and design.  These are:
  * clearContents()
    * Resets the current state of the `CreditCardDetailsView`
  * getCreditCardValid()
    * Returns the validity of the currently entered Credit Card information.  A true value means all checks are passed and the card is ready to be tokenized.
  * addCreditCard(listener: Response?)
    * Initializes the tokenization process, assigns the card to the provided Customer ID (if any), and then returns the tokenized CreditCard to `Response.success`.  Or, if there is an error, returns error information to `Response.error`
   
### Purchase
Purchase in MobilePayments has only one UI element, the `PurchaseButton` Composable.  This element will display the amount being charged, and display a button that will automatically be enabled when all parameters are ready for a transaction.  When pressed, the button will charge the provided amount to the provided payment method and return the resulting `Transaction`.

`PurchaseButton` operates in two ways, `MULTI_CARD` and `SINGLE_CARD`.
  1. `MULTI_CARD` mode is an integration with the `CreditCardListView` or similar widget from the host UI that will provide ultimately a valid `PaymentMethod` from the customer.  When the PaymentMethod and amount are provided to the `PurchaseButton` in this mode, the button becomes enabled and the customer can press the button to initiate the transaction
  2. `SINGLE_CARD` mode is a standalone mode where, if a `PaymentMethod` is not provided, when the customer presses the button, the `CreditCardDetailsModal` will open, allowing the customer to enter their `CreditCard` information and proceed through the transaction immediately

To add the `PurchaseButton` to your UI, simply declare the Composable:
```
PurchaseButton(
  modifier = Modifier,
  model = purchaseButtonModel,
  amount = amount,
  payment = selectedCard,
  requireCvv = false,
  mode = PurchaseButtonOperationMode.MULTI_CARD,
  customerId = "<CUSTOMER_ID>",
  canSaveCard = true,
  autoSubmitAfterAddingCard = false,
  singleCardAddressMode = CreditCardDetailsAddressMode.POSTAL_CODE,
  transactionType = TransactionType.SALE,
  clientTransactionId = null,
  merchantReference = null,
  purchaseListener = object: Response{
    override fun success(response: Any?) {
      val transaction = response as Transaction
      updateSuccessMessage("Transaction ID: ${transaction.transactionId}\nAmount: ${transaction.amount}")
    }

    override fun error(exception: Throwable?) {
      updateErrorMessage("Error: $exception")
    }
  }
)
```

#### Parameters
  * Amount
    * The amount to charge the provided `PaymentMethod` when the button is pressed.  This can also be updated through `PurchaseButtonModel.updateAmount(Double)`
  * **(OPTIONAL)** Mode
    * Flag to control the operation mode of the `PurchaseButton`.  Defaults to `MULTI_CARD`
  * **(OPTIONAL)** Modifier
    * A standard composable Modifier, used to specify the size and layout of the `PurchaseButton` in the host UI.
  * **(OPTIONAL)** Model
    * A `PurchaseButtonModel` instantiated by the host UI.  Providing this to the `PurchaseButton` will hoist its state and allow the host UI to control the Button’s behavior
  * **(OPTIONAL)** Transaction Type
    * The type of Payment you are seeking to collect.  The options are `TransactionType.SALE` or `TransactionType.AUTH`.  Simply put, `SALE` is used to collect funds immediately, while `AUTH` will reserve funds on the payment method, but will not collect them until a `CAPTURE` transaction is run in the future. 
  * **(OPTIONAL)** Client Transaction Id
    * An identifier for the transaction, used for tracking purposes. Defaults to a randomly generated UUID if not supplied.
  * **(OPTIONAL)** Merchant Reference
    * A reference value for the transaction, usually the ticket or order number
  * **(OPTIONAL)** Purchase Listener
    * Listener invoked when the `Payment` is completed.  A successful transaction will return a `Transaction` object to `Response.success`, while an error will return details to `Response.error`

##### `MULTI_CARD` Parameters
  * Payment
    * The `PaymentMethod` to charge when the button is pressed.  This can also be updated through `PurchaseButtonModel.updatePaymentMethod(PaymentMethod)`
   
##### `SINGLE_CARD` Parameters
  * **(OPTIONAL)** Payment
    * The `PaymentMethod` to charge when the button is pressed.  This can also be updated through `PurchaseButtonModel.updatePaymentMethod(PaymentMethod)`.  Providing this will bypass the `CreditCardDetailsModal` step of `SINGLE_CARD` mode
  * **(OPTIONAL)** Customer ID
    * A unique alphanumeric string identifying a single user in order to access previously saved Credit Cards and save new ones for a future transaction.  This value is the same as that passed to `MobilePayments.setCustomerId`, and can be omitted if you have set it there or if you do not wish to allow users to save
  * **(OPTIONAL)** Can Save Card
    * Flag to allow the customer to save the `CreditCard` to the provided Customer ID.  Defaults to true
  * **(OPTIONAL)** Auto Submit After Adding Card
    * Flag to automatically proceed with transaction after completing the `CreditCardDetailsModal`.  Defaults to false
  * **(OPTIONAL)** Single Card Address Mode
    * Mode for entering Credit Card billing address in `CreditCardDetailsModal`.  Options are `POSTAL_CODE`, `FULL_ADDRESS`, and `NONE`

### Google Pay
Google Pay is fully supported in the MobilePayments system.  Adding it to your application can be done through [this tutorial](https://developers.google.com/pay/api/android/guides/tutorial) from Google.

Once Google Pay is set up and you have collected `PaymentData` from Google Pay, you need only create a `GooglePay` object and provide the `PaymentData` as a json.  The `GooglePay` object is a `PaymentMethod` fully supported throughout the MobilePayments API.  Declaring the `GooglePay` object looks like this:
```
val json = paymentData?.toJson() ?: return
val googlePay = GooglePay(
  walletToken = json
)
```

### Utility
#### Shared MobilePaymentsViewModel Utility Methods
Here are some useful utility methods shared across all `MobilePaymentsViewModel` classes that can be quite useful for customizing the exact experience you seek.
  * showLoading()
    * Show a loading throbber to cover the UI widget
  * hideLoading()
    * Remove loading throbber and allow normal UI operations
  * addLoadingListener(listener: LoadingListener)
    * Inject a LoadingListener into the model.  This listener is invoked when showLoading() or hideLoading() is called and if it returns true, the loading throbber within the UI widget is bypassed
    * **Note:** Use this to suppress throbber behavior if necessary
  * parseError(exception: Throwable?): String
    * Return the primary error message of a `WebErrorSet` provided to a `Response.error`, or the `mp_errorGeneric` string if no message can be found.
  * makePayment(amount: Double, paymentMethod: PaymentMethod, transactionType: TransactionType, listener:Response?)
    * Charge the provided `amount` to the provided `PaymentMethod` through the `TransactionType`, and return the resulting `Transaction` to `Response.success`, or the error to `Response.error`


