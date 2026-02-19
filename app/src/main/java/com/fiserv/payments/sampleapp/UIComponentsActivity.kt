package com.fiserv.payments.sampleapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiserv.payments.api.core.Response
import com.fiserv.payments.api.payment.data.Transaction
import com.fiserv.payments.api.payment.data.TransactionType
import com.fiserv.payments.sampleapp.models.UIComponentsActivityListener
import com.fiserv.payments.sampleapp.models.UIComponentsActivityViewModel
import com.fiserv.payments.sampleapp.ui.theme.DarkText
import com.fiserv.payments.sampleapp.ui.theme.Disabled
import com.fiserv.payments.sampleapp.ui.theme.FiservMobilePaymentsSampleTheme
import com.fiserv.payments.sampleapp.ui.theme.HalfTrans
import com.fiserv.payments.sampleapp.ui.theme.Typography
import com.fiserv.payments.ui.theme.MobilePaymentsStyleProvider
import com.fiserv.payments.ui.views.CardNumberMaskMode
import com.fiserv.payments.ui.views.CreditCardListMode
import com.fiserv.payments.ui.views.CreditCardListView
import com.fiserv.payments.ui.views.PurchaseButton
import com.fiserv.payments.ui.views.models.CreditCardDetailsAddressMode
import com.fiserv.payments.ui.views.models.CreditCardListViewModel
import com.fiserv.payments.ui.views.models.PurchaseButtonModel
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.google.android.gms.wallet.contract.TaskResultContracts
import com.google.pay.button.PayButton

class UIComponentsActivity : ComponentActivity(), UIComponentsActivityListener {
    private lateinit var paymentsClient: PaymentsClient
    val model: UIComponentsActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by model.state.collectAsState()
            val cardListModel: CreditCardListViewModel by viewModels()
            cardListModel.addLoadingListener( model)
            val purchaseButtonModel: PurchaseButtonModel by viewModels()
            purchaseButtonModel.addLoadingListener(model)

            model.updateAmountInput("28.94")

            FiservMobilePaymentsSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier.padding(innerPadding)
                            .background(color = MobilePaymentsStyleProvider.colors.getBackground())
                    ){
                        Column(
                            modifier = Modifier.fillMaxSize().scrollable(
                                state = rememberScrollState(),
                                orientation = Orientation.Vertical
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            if( state.transactionMessage.isNotEmpty() ){
                                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                                    .background(color= MobilePaymentsStyleProvider.colors.getSuccess(), shape = RoundedCornerShape(MobilePaymentsStyleProvider.shapes.getCornerRadius())),){
                                    Text(
                                        text =  state.transactionMessage,
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                                        color = MobilePaymentsStyleProvider.colors.getLightText(),
                                    )
                                }
                            }
                            if( state.errorMessage.isNotEmpty() ){
                                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                                    .background(color= MobilePaymentsStyleProvider.colors.getError(), shape = RoundedCornerShape(MobilePaymentsStyleProvider.shapes.getCornerRadius())),){
                                    Text(
                                        text =  state.errorMessage,
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                                        color = MobilePaymentsStyleProvider.colors.getLightText(),
                                    )
                                }
                            }

                            CreditCardListView(
                                modifier = Modifier.fillMaxWidth(),
                                model = cardListModel,
                                customerId = state.customerId,
                                scrollingEnabled = true,
                                showSelectors = true,
                                requireCvv = true,
                                canAddCards = true,
                                defaultEnabled = true,
                                cardNumberMaskMode = CardNumberMaskMode.LAST_FOUR_VISIBLE,
                                addressMode = CreditCardDetailsAddressMode.FULL_ADDRESS,
                                mode = CreditCardListMode.PAYMENT,
                                onCreditCardSelected = {card ->
                                    purchaseButtonModel.updatePaymentMethod(card)
                                }
                            )

                            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)){
                                Text(
                                    textAlign = TextAlign.Start,
                                    text = "Your Cart",
                                    style = Typography.headlineSmall,
                                    modifier = Modifier,
                                    color = MobilePaymentsStyleProvider.colors.getDarkText(),
                                )
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 0.dp, vertical = 8.dp)) {
                                    Text(
                                        textAlign = TextAlign.Start,
                                        text = "Gemini Chart",
                                        style = Typography.labelLarge,
                                        modifier = Modifier.weight(1f),
                                        color = MobilePaymentsStyleProvider.colors.getDarkText(),
                                    )
                                    Text(
                                        textAlign = TextAlign.Start,
                                        text = "$10.99",
                                        style = Typography.bodyLarge,
                                        modifier = Modifier,
                                        color = MobilePaymentsStyleProvider.colors.getDarkText(),
                                    )
                                }
                                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).height(1.dp).background(color = Disabled))
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 0.dp, vertical = 8.dp)) {
                                    Text(
                                        textAlign = TextAlign.Start,
                                        text = "Quickstart Kit",
                                        style = Typography.labelLarge,
                                        modifier = Modifier.weight(1f),
                                        color = MobilePaymentsStyleProvider.colors.getDarkText(),
                                    )
                                    Text(
                                        textAlign = TextAlign.Start,
                                        text = "$12.00",
                                        style = Typography.bodyLarge,
                                        modifier = Modifier,
                                        color = MobilePaymentsStyleProvider.colors.getDarkText(),
                                    )
                                }
                                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp).height(1.dp).background(color = DarkText))
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 0.dp, vertical = 8.dp)) {
                                    Text(
                                        textAlign = TextAlign.Start,
                                        text = "Taxes & Fees",
                                        style = Typography.bodyMedium,
                                        modifier = Modifier.weight(1f),
                                        color = MobilePaymentsStyleProvider.colors.getDarkText(),
                                    )
                                    Text(
                                        textAlign = TextAlign.Start,
                                        text = "$5.95",
                                        style = Typography.bodyMedium,
                                        modifier = Modifier,
                                        color = MobilePaymentsStyleProvider.colors.getDarkText(),
                                    )
                                }
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 0.dp, vertical = 8.dp)) {
                                    Text(
                                        textAlign = TextAlign.Start,
                                        text = "Total",
                                        style = Typography.headlineMedium,
                                        modifier = Modifier.weight(1f),
                                        color = MobilePaymentsStyleProvider.colors.getDarkText(),
                                    )
                                    Text(
                                        textAlign = TextAlign.Start,
                                        text = "$${state.amountInput.toDoubleOrNull() ?: 0.0}",
                                        style = Typography.headlineLarge,
                                        modifier = Modifier,
                                        color = MobilePaymentsStyleProvider.colors.getDarkText(),
                                    )
                                }
                            }
                            Text(
                                textAlign = TextAlign.Start,
                                text = "Acme provides information you submit through this site to a vendor for security purposes.  Please see the Privacy Policy for more information.",
                                style = Typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                color = MobilePaymentsStyleProvider.colors.getMediumText(),
                            )
                            val annotatedLinkString: AnnotatedString = remember {
                                buildAnnotatedString {
                                    val styleCenter = SpanStyle(
                                        color = Color(0xff64B5F6),
                                        fontSize = 14.sp,
                                        textDecoration = TextDecoration.Underline
                                    )

                                    withStyle(
                                        style = styleCenter
                                    ) {
                                        append("Terms and Conditions of Service")
                                    }
                                }
                            }
                            Text(
                                textAlign = TextAlign.Start,
                                text = annotatedLinkString,
                                style = Typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                color = MobilePaymentsStyleProvider.colors.getDarkText(),
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                PayButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        model.requestGooglePay()
                                    },
                                    allowedPaymentMethods = model.getAllowedPaymentMethods(),
                                )
                            }
                            PurchaseButton(
                                amount = state.amountInput.toDoubleOrNull() ?: 0.0,
                                payment = cardListModel.getSelectedCard(),
                                modifier = Modifier.imePadding(),
                                model = purchaseButtonModel,
                                requireCvv = true,
                                transactionType = TransactionType.SALE,
                                purchaseListener = object: Response<Transaction>{
                                    override fun success(response: Transaction) {
                                        cardListModel.resetList()
                                        model.updateTransactionMessage("Transaction ID: ${response.transactionId}\nAmount: ${response.amount}")
                                        model.updateErrorMessage("")
                                    }

                                    override fun error(exception: Throwable?) {
                                        model.updateTransactionMessage("")
                                        model.updateErrorMessage("Error: $exception")
                                    }
                                }
                            )
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

        paymentsClient = Wallet.getPaymentsClient(
            this,
            Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                .build()
        )
        model.initialize(this, paymentsClient, intent)
    }


    private val paymentDataLauncher = registerForActivityResult(TaskResultContracts.GetPaymentDataResult()) { taskResult ->
        when (taskResult.status.statusCode) {
            CommonStatusCodes.SUCCESS -> {
                model.handlePaymentData(taskResult.result!!)
            }
        }
    }
    override fun launchGooglePlay(task: Task<PaymentData>) {
        task.addOnCompleteListener(paymentDataLauncher::launch)
    }

    override fun closeAndReturnResult(result: Int, data: Intent?) {
    }
}