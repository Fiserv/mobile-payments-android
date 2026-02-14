# Overview
The MobilePayments SDK provides a collection of self-contained UI elements, both in individual widgets as well as larger, more comprehensive containers.  No one look can suit every app out there, however, so an extensive customization suite has been added to control the look and feel of these UI elements.  With these tools, you should be able to make the MobilePayments UI fit nearly any application design and aesthetic.

# How to Customize
The MobilePayments SDK uses the `MobilePaymentsStyleProvider` in order to control the look and feel of the UI elements.  This provider can be manipulated at any time prior to UI initialization to change the UI along these axes:

  * [Color](#color)
  * [Font](#font)
  * [Shape](#shape)
  * [Copy](#copy)

**Note:** For real time style updates (e.g., changing the colors in response to a button press), you must call `MobilePaymentsStyleProvider.applyStyle()` after you have updated the providers to your desired implementations, then restart any UI elements containing Mobile Payment SDK UI components.

## Color
Color in the MobilePayments UI is handled through a series of channels, where UI elements in similar design situations are put on a given channel.  Each channel is then able to be freely changed to any desired color.

These channels are specified in the `MobilePaymentsColorProvider` interface, with each color channel as a separate method:
```
interface MobilePaymentsColorProvider {
  fun getPrimary(): Color{
    return Blue
  }
  fun getDisabled(): Color{
    return Disabled
  }
  fun getSuccess(): Color{
    return Green
  }
  fun getError(): Color{
    return Red
  }
  fun getDarkText(): Color{
    return DarkText
  }
  fun getMediumText(): Color{
    return MediumText
  }
  fun getLightText(): Color{
    return White
  }
  fun getLightBackground(): Color{
    return White
  }
  fun getBackground(): Color{
    return BackgroundWhite
  }
}
```
In order to customize the colors, you simply implement this interface in a custom class and override the colors you wish to change.  For example:
```
object DarkMode: MobilePaymentsColorProvider{
  override fun getDarkText(): Color {
    return Color(0xFFEFEFEF)
  }
  override fun getMediumText(): Color {
    return Color(0xFF9B9B9B)
  }
  override fun getLightBackground(): Color {
    return Color(0xFF000000)
  }
  override fun getBackground(): Color {
    return Color(0xFF252829)
  }
}
```
Once specified, you pass the new MobilePaymentsColorProvider to the MobilePaymentsStyleProvider as follows:
```
MobilePaymentsStyleProvider.colors = DarkMode
```
### Example
<div class="container" style="width:100%; display: flex; justify-content: space-evenly;" align="center">
	<img src="/images/sheet_default_color.png" alt="MobilePaymentsPurchaseActivity with the default color provider" style="width:25%; height:auto;">&nbsp;&nbsp;<img src="/images/sheet_custom_color.png" alt="MobilePaymentsPurchaseActivity with the sample color provider" style="width:25%; height:auto;">
</div>

## Font
There are two fonts used in the MobilePayments UI, by default these are `OpenSans` and `Montserraf`.  Any valid font can be used to replace one or both of these as desired.

Similar to the above, these fonts are specified by the `MobilePaymentsFontProvider` interface:
```
interface MobilePaymentsFontProvider {
  fun getHeaderFont(): FontFamily{
    val fontFamily = FontFamily(
      Font(R.font.montserrat_regular, FontWeight.Normal),
      Font(R.font.montserrat_bold, FontWeight.Bold),
    )
    return fontFamily
  }
  fun getBodyFont(): FontFamily{
    val fontFamily = FontFamily(
      Font(R.font.opensans_regular, FontWeight.Normal),
      Font(R.font.opensans_bold, FontWeight.Bold),
    )
    return fontFamily
  }
}
```
Simply implement this interface and override the method for the font you wish to replace, like so:
```
object CustomFont: MobilePaymentsFontProvider{
  override fun getHeaderFont(): FontFamily {
    return val fontFamily = FontFamily(
      Font(R.font.roboto_regular, FontWeight.Normal),
      Font(R.font.roboto_bold, FontWeight.Bold),
    )
    return fontFamily
  }
}
```
And pass this new `MobilePaymentsFontProvider` to `MobilePaymentsStyleProvider` as follows:
```
MobilePaymentsStyleProvider.fonts = CustomFont
```

### Example
<div class="container" style="width:100%; display: flex; justify-content: space-evenly;" align="center">
	<img src="/images/sheet_default_font.png" alt="MobilePaymentsPurchaseActivity with the default font provider" style="width:25%; height:auto;">&nbsp;&nbsp;<img src="/images/sheet_custom_font.png" alt="MobilePaymentsPurchaseActivity with the sample font provider" style="width:25%; height:auto;">
</div>

## Shape
Shapes in the `MobilePaymentsStyleProvider` refers to general shaping of the app, typically rounded corner radii and border thickness.

These shapes are specified by the `MobilePaymentsShapeProvider` interface:
```
interface MobilePaymentsShapeProvider {
  fun getButtonCornerRadius(): Dp{
    return 40.dp
  }
  fun getCornerRadius(): Dp{
    return 10.dp
  }
  fun getTextFieldCornerRadius(): Dp{
    return 6.dp
  }
  fun getSelectedBorderThickness(): Dp{
    return 2.5.dp
  }
  fun getBorderThickness(): Dp{
    return 1.dp
  }
}
```
To change these parameters, simply implement and override as desired:
```
object BlockApp : MobilePaymentsShapeProvider {
  override fun getButtonCornerRadius(): Dp{
    return 4.dp
  }
  override fun getCornerRadius(): Dp{
    return 2.dp
  }
  override fun getTextFieldCornerRadius(): Dp{
    return 1.dp
  }
}
```
Then this new MobilePaymentsShapeProvider is provided to the MobilePaymentsStyleProvider:
```
MobilePaymentsStyleProvider.shapes = BlockApp
```

### Example
<div class="container" style="width:100%; display: flex; justify-content: space-evenly;" align="center">
	<img src="/images/sheet_default_shapes.png" alt="MobilePaymentsPurchaseActivity with the default shape provider" style="width:25%; height:auto;">&nbsp;&nbsp;<img src="/images/sheet_custom_shapes.png" alt="MobilePaymentsPurchaseActivity with the sample shape provider" style="width:25%; height:auto;">
</div>

## Copy
There is a small collection of string values baked into the UI elements, generally page titles and button labels, that are also freely customizable, utilizing the native platform’s string and localization management behavior.

**Warning:** Strings containing non-standard characters or of excessive length may impact UI negatively.  Test and review any changes before release to ensure compatibility with your designed copy.

The Mobile Payments Android SDK utilizes the standard system resource system, drawing from these values:
```
<resources>
  <!-- Credit Cards -->
  <string name="mp_creditCardListTitle">Saved Cards</string>
  <string name="mp_creditCardAddCardButton">Add Credit Card</string>

  <!-- Credit Card Details -->
  <string name="mp_creditCardDetailsTitle">Card Info</string>
  <string name="mp_creditCardDetailsAddCardButton">Add Card</string>
  <string name="mp_creditCardDetailsAddCardAndPayButton">Pay $%,.2f</string>
  <string name="mp_creditCardDetailsAddressTitle">Billing Address</string>

  <!-- Purchase Button -->
  <string name="mp_purchaseButtonLabel">Purchase</string>
  <string name="mp_purchaseButtonAddCardAtCheckoutLabel">Pay With Card</string>
  <string name="mp_purchaseButtonAddCardAtCheckoutPaymentLabel">Pay with %s</string>
  <string name="mp_purchaseButtonAmountLabel">Total</string>

  <!-- Address Container -->
  <string name="mp_addressContainerTitle">Billing Address</string>
  <string name="mp_addressContainerAddNewButton">Add new address</string>

  <!-- Address Details -->
  <string name="mp_addressDetailsTitle">Address</string>
  <string name="mp_addressDetailsConfirmAddressButton">Confirm</string>

  <!-- Purchase Activity -->
  <string name="mp_purchaseActivityTitle">Pay Now</string>

  <!-- Errors -->
  <string name="mp_errorGeneric">There was an error.  Please try again later.</string>
  <string name="mp_creditCardDetailsAddInvalidCardError">Card is invalid.  Please double check details and try again.</string>
  <string name="mp_addressDetailsAddressInvalidError">Address is invalid.  Please double check details and try again.</string>
</resources>
```
To customize, simply add any of these `<string>` resources to your projects `res/values/strings.xml` file and change the value as desired.
