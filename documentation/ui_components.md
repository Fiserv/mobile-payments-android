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
  * **(Optional)** Modifier
    * A standard composable Modifier, used to specify the size and layout of the CreditCardListView in the host UI.
  * **(Optional)** Model
    * A `CreditCardListViewModel` instantiated by the host UI.  Providing this to the `CreditCardListView` will hoist the List’s state and allow the host UI to access information within the UI, such as which Credit Card is currently selected, the full list of Credit Cards, and adding or removing a Credit Card from the list
  * **(Optional)** Customer ID
    * A unique alphanumeric string identifying a single user in order to access previously saved Credit Cards and save new ones for a future transaction.  This value is the same as that passed to `MobilePayments.setCustomerId`, and can be omitted if you have set it there or if you do not wish to allow users to save Credit Cards and use them again in future.
  * **(Optional)** Scrolling Enabled
    * Whether or not scrolling the List is possible.  This only matters in the event the space provided to the `CreditCardListView` through the Modifier or host UI arrangement is smaller than its contents, or the List is embedded inside another scrolling Composable.
  * **(Optional)** Show Selectors
    * Flag to show or hide the radio button selection indicator.  Defaults to true
  * **(Optional)** Requie CVV
    * Flag to require CVV value when reusing previously saved `CreditCards`.  Defaults to false
  * **(Optional)** Can Add Cards
    * Flag to allow adding a `CreditCard` through the `CreditCardListView`.  Defaults to true
  * **(Optional)** Default Enabed
    * Flag to allow the list to automatically select the user's Default `CreditCard`, if one exists.  Defaults to true
  * **(Optional)** Mode
    * Display mode of the list.  Options are `PAYMENT` and `MANAGE`.  `PAYMENT` allows Credit Cards to be selected from the list, while `MANAGE` allows only Credit Card List management actions such as add and delete cards.  Defaults to `PAYMENT`  
  * **(Optional)** On Credit Card Selected
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
  * **(Optional)** Can Save Card
    * Flag to allow the customer to save the `CreditCard` to the provided Customer ID.  Defaults to true
  * **(Optional)** Address
    * `Address` to use as the billing address for this transaction.  To be paired with `CreditCardDetailsAddressMode.NONE`
  * **(Optional)** Address Mode
    * Mode for entering Credit Card billing address.  Options are `POSTAL_CODE`, `FULL_ADDRESS`, and `NONE`
  * **(Optional)** onCardAdded
    * Listener invoked when a CreditCard is successfully tokenized and is passed to the calling UI
  * **(OPTIONAL)** onDismissRequest
    * Listener invoked when the ModalBottomSheet system API triggers a close event.  This is standard ModalBottomSheet behavior and you will need to suppress the Composable in response to this event.

The CreditCardDetailsModalViewModel contains these methods of note:
  * clearContents()
    * Clears the current UI state and reverts to a fresh slate
  * updateErrorMessage(message: String?)
    * Updates the error banner at the top of the modal with a provided message, or hides it if the provided value is null
