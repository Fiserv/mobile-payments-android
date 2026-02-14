package com.fiserv.payments.sampleapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiserv.payments.api.payment.data.Transaction
import com.fiserv.payments.api.payment.data.TransactionType
import com.fiserv.payments.sampleapp.models.MainActivityViewModel
import com.fiserv.payments.sampleapp.ui.theme.FiservMobilePaymentsSampleTheme
import com.fiserv.payments.sampleapp.ui.theme.Green
import com.fiserv.payments.sampleapp.ui.theme.HalfTrans
import com.fiserv.payments.ui.activities.MobilePaymentsPurchaseActivity
import com.fiserv.payments.ui.activities.data.AMOUNT_KEY
import com.fiserv.payments.ui.activities.data.CUSTOMER_ID_KEY
import com.fiserv.payments.ui.activities.data.TRANSACTION_KEY
import com.fiserv.payments.ui.activities.data.TRANSACTION_TYPE_KEY
import com.fiserv.payments.ui.theme.MobilePaymentsStyleProvider
import com.fiserv.payments.ui.theme.interfaces.MobilePaymentsColorProvider
import com.fiserv.payments.ui.theme.interfaces.MobilePaymentsFontProvider
import com.fiserv.payments.ui.theme.interfaces.MobilePaymentsShapeProvider
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    val model: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by model.state.collectAsState()

            FiservMobilePaymentsSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)){
                        Column(
                            modifier = Modifier.fillMaxSize().scrollable(state = rememberScrollState(), orientation = Orientation.Vertical),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            if( state.transactionMessage.isNotEmpty() ){
                                Text(
                                    textAlign = TextAlign.Start,
                                    text = state.transactionMessage,
                                    modifier = Modifier
                                        .padding(16.dp, 8.dp)
                                        .background(color = Green)
                                )
                            }

                            Text(
                                text = "Mobile Payments Sample App",
                                modifier = Modifier.padding(16.dp),
                            )
                            OutlinedTextField(
                                shape = RoundedCornerShape(MobilePaymentsStyleProvider.shapes.getTextFieldCornerRadius()),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MobilePaymentsStyleProvider.colors.getPrimary(),
                                    unfocusedBorderColor = MobilePaymentsStyleProvider.colors.getMediumText(),
                                ),
                                value = state.customerId,
                                onValueChange = {it ->
                                    model.updateCustomerId(it)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                label = {
                                    Text(
                                        textAlign = TextAlign.Start,
                                        text = "User ID",
                                        modifier = Modifier.padding(8.dp, 0.dp)
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 16.dp),
                            )
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                onClick = {
                                    val intent = Intent(this@MainActivity, MobilePaymentsPurchaseActivity::class.java)
                                    intent.putExtra(AMOUNT_KEY, 24.68)
                                    if( state.customerId.isNotEmpty() ){
                                        intent.putExtra(CUSTOMER_ID_KEY, state.customerId)
                                    }
                                    intent.putExtra(TRANSACTION_TYPE_KEY, TransactionType.SALE)
                                    activityLauncher.launch(intent)
                                },
                            ){
                                Text(
                                    text = "Sheets",
                                    modifier = Modifier,
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                onClick = {
                                    val intent = Intent(this@MainActivity, UIComponentsActivity::class.java)
                                    if( state.customerId.isNotEmpty() ){
                                        intent.putExtra(CUSTOMER_ID_KEY, state.customerId)
                                    }
                                    startActivity(intent)
                                },
                            ){
                                Text(
                                    text = "UI Components",
                                    modifier = Modifier,
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                onClick = {
                                    val intent = Intent(this@MainActivity, SingleCardActivity::class.java)
                                    if( state.customerId.isNotEmpty() ){
                                        intent.putExtra(CUSTOMER_ID_KEY, state.customerId)
                                    }
                                    startActivity(intent)
                                },
                            ){
                                Text(
                                    text = "Single Card",
                                    modifier = Modifier,
                                )
                            }
                            Text(
                                text = "Styles",
                                modifier = Modifier.padding(top = 4.dp),
                            )
                            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp)){
                                Button(
                                    modifier = Modifier.weight(1f).height(48.dp)
                                        .padding(horizontal = 4.dp),
                                    onClick = {
                                        MobilePaymentsStyleProvider.colors = UPSColorProvider
                                        MobilePaymentsStyleProvider.fonts = UPSFontProvider
                                        MobilePaymentsStyleProvider.shapes = UPSShapeProvider
                                        MobilePaymentsStyleProvider.applyStyle()
                                        Toast.makeText(application, "Style #1 applied", Toast.LENGTH_SHORT).show()
                                    },
                                ){
                                    Text(
                                        text = "Style #1",
                                        modifier = Modifier,
                                        fontSize = 12.sp,
                                    )
                                }
                                Button(
                                    modifier = Modifier.weight(1f).height(48.dp)
                                        .padding(horizontal = 4.dp),
                                    onClick = {
                                        MobilePaymentsStyleProvider.colors = DarkModeColorProvider
                                        MobilePaymentsStyleProvider.fonts = DarkModeFontProvider
                                        MobilePaymentsStyleProvider.shapes = DarkModeShapeProvider
                                        MobilePaymentsStyleProvider.applyStyle()
                                        Toast.makeText(application, "Style #2 applied", Toast.LENGTH_SHORT).show()
                                    },
                                ){
                                    Text(
                                        text = "Style #2",
                                        modifier = Modifier,
                                        fontSize = 12.sp,
                                    )
                                }
                                Button(
                                    modifier = Modifier.weight(1f).height(48.dp)
                                        .padding(horizontal = 4.dp),
                                    onClick = {
                                        MobilePaymentsStyleProvider.colors = object: MobilePaymentsColorProvider{}
                                        MobilePaymentsStyleProvider.fonts = object: MobilePaymentsFontProvider{}
                                        MobilePaymentsStyleProvider.shapes = object: MobilePaymentsShapeProvider{}
                                        MobilePaymentsStyleProvider.applyStyle()
                                        Toast.makeText(application, "Default Style applied", Toast.LENGTH_SHORT).show()
                                    },
                                ){
                                    Text(
                                        text = "Default",
                                        modifier = Modifier,
                                        fontSize = 12.sp,
                                    )
                                }
                            }
                        }
                        if( state.isLoading ){
                            val interactionSource = remember { MutableInteractionSource() }
                            Box( modifier = Modifier
                                .background(color = HalfTrans)
                                .matchParentSize()
                                .clickable(
                                    indication = null,
                                    interactionSource = interactionSource,
                                ) {
                                    //Do nothing
                                },
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.widthIn(min= 36.dp, max= 60.dp),
                                    color = MobilePaymentsStyleProvider.colors.getMediumText(),
                                    trackColor = MobilePaymentsStyleProvider.colors.getPrimary(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private val activityLauncher = registerForActivityResult(contract = StartActivityForResult(),
        callback ={ result: ActivityResult? ->
            if( result?.resultCode == RESULT_OK ){
                //Transaction was completed
                val transactionString = result.data?.getStringExtra(TRANSACTION_KEY)
                if( transactionString != null ){
                    val jsonHandler = Json{ ignoreUnknownKeys = true }
                    val transaction = jsonHandler.decodeFromString<Transaction>(transactionString)
                    model.updateTransactionMessage("Transaction ID: ${transaction.transactionId}\nAmount: ${transaction.amount}")
                }
            }else if( result?.resultCode == RESULT_CANCELED ){
                //User backed out or no amount was provided; transaction is not completed
            }
        }
    )
}

object UPSColorProvider: MobilePaymentsColorProvider{
    override fun getPrimary(): Color {
        return Color(0xFFfec400)
    }

    override fun getLightText(): Color {
        return Color(0xFF654431)
    }

    override fun getError(): Color {
        return Color(0xFFF28D8D)
    }

    override fun getBackground(): Color {
        return Color(0xFFf2f0ef)
    }
}

object UPSFontProvider: MobilePaymentsFontProvider{
    override fun getHeaderFont(): FontFamily {
        val fontFamily = FontFamily(
            Font(R.font.roboto_regular, FontWeight.Normal),
            Font(R.font.roboto_bold, FontWeight.Bold),
        )
        return fontFamily
    }
    override fun getBodyFont(): FontFamily {
        val fontFamily = FontFamily(
            Font(R.font.roboto_regular, FontWeight.Normal),
            Font(R.font.roboto_bold, FontWeight.Bold),
        )
        return fontFamily
    }
}

object UPSShapeProvider : MobilePaymentsShapeProvider {
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

object DarkModeColorProvider: MobilePaymentsColorProvider{
    override fun getBackground(): Color {
        return Color(0xFF252829)
    }

    override fun getDarkText(): Color {
        return Color(0xFFFFFFFF)
    }

    override fun getLightBackground(): Color {
        return Color(0xFF000000)
    }

    override fun getLightText(): Color {
        return Color(0xFFFFFFFF)
    }

    override fun getMediumText(): Color {
        return Color(0xFF9E9E9E)
    }
}

object DarkModeFontProvider: MobilePaymentsFontProvider{
    override fun getHeaderFont(): FontFamily {
        val fontFamily = FontFamily(
            Font(R.font.poppins_regular, FontWeight.Normal),
            Font(R.font.poppins_bold, FontWeight.Bold),
        )
        return fontFamily
    }
    override fun getBodyFont(): FontFamily {
        val fontFamily = FontFamily(
            Font(R.font.raleway_regular, FontWeight.Normal),
            Font(R.font.raleway_bold, FontWeight.Bold),
        )
        return fontFamily
    }
}

object DarkModeShapeProvider : MobilePaymentsShapeProvider {

}