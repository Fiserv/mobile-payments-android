package com.fiserv.payments.sampleapp.models

import android.app.Application
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.application
import com.fiserv.payments.api.core.MobilePayments
import com.fiserv.payments.api.core.Response
import com.fiserv.payments.api.googlepay.GooglePay
import com.fiserv.payments.api.payment.PaymentManager
import com.fiserv.payments.api.payment.data.PaymentType
import com.fiserv.payments.api.payment.data.Transaction
import com.fiserv.payments.ui.activities.data.USER_ID_KEY
import com.fiserv.payments.ui.views.models.LoadingListener
import com.fiserv.payments.ui.views.models.MobilePaymentsViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONObject

data class UIComponentsActivityState(
    var transactionMessage: String = "",
    var amountInput: String = "",
    var isLoading: Boolean = false,
    var userId: String? = null
)
class UIComponentsActivityViewModel(application: Application) : MobilePaymentsViewModel(application), LoadingListener {
    private val _state = MutableStateFlow(UIComponentsActivityState())
    val state = _state.asStateFlow()

    lateinit var paymentsClient: PaymentsClient
    var activityListener: UIComponentsActivityListener? = null

    fun initialize(activityListener: UIComponentsActivityListener, paymentsClient: PaymentsClient, intent: Intent?){
        this.activityListener = activityListener
        this.paymentsClient = paymentsClient

        updateUserId(intent?.getStringExtra(USER_ID_KEY))
    }

    fun updateTransactionMessage(transactionMessage: String){
        _state.update { currentState ->
            currentState.copy(
                transactionMessage = transactionMessage,
            )
        }
    }

    fun updateAmountInput(amountInput: String){
        _state.update { currentState ->
            currentState.copy(
                amountInput = amountInput,
            )
        }
    }
    fun updateUserId(userId: String?){
        _state.update { currentState ->
            currentState.copy(
                userId = userId,
            )
        }
    }

    fun updateLoading(isLoading: Boolean){
        _state.update { currentState ->
            currentState.copy(
                isLoading = isLoading,
            )
        }
    }
    override fun onLoading(isLoading: Boolean): Boolean {
        updateLoading(isLoading)
        return true
    }
    private fun getGooglePayRequest(): String{
        val gateways = PaymentManager.getPaymentGateways()

        if( gateways != null && gateways.size > 0){
            val gateway = gateways[0]
            return gateway.googlePayConfig.toString()
        }

        return ""
    }

    fun getAllowedPaymentMethods(): String{
        val googlePayJson = getGooglePayRequest()
        if( googlePayJson.isEmpty() ){
            MobilePayments.setGooglePayEnabled(false)
            return ""
        }
        val allowedPaymentMethods = JSONObject(googlePayJson)
            .getJSONArray("allowedPaymentMethods")
            .toString()

        return allowedPaymentMethods
    }

    fun requestGooglePay(){
        if( state.value.amountInput.toDoubleOrNull() == null ){
            return
        }
        val googlePayJson = getGooglePayRequest()
        if( googlePayJson.isEmpty() ){
            MobilePayments.setGooglePayEnabled(false)
            return
        }
        val requestJson = JSONObject(googlePayJson).apply {
            if( !has("apiVersion") ){
                put("apiVersion", 2)
            }
            if( !has("apiVersionMinor") ){
                put("apiVersionMinor", 0)
            }
            if( has("transactionInfo") ){
                getJSONObject("transactionInfo").apply {
                    put("totalPrice", state.value.amountInput.toDoubleOrNull().toString())
                    put("totalPriceStatus", "FINAL")
                }
            }else{
                put("transactionInfo", JSONObject().apply {
                    put("totalPrice", state.value.amountInput.toDoubleOrNull().toString())
                    put("totalPriceStatus", "FINAL")
                    put("currencyCode", "USD")
                })
            }
        }

        val request = PaymentDataRequest.fromJson(requestJson.toString(4))
        val task = paymentsClient.loadPaymentData(request)
        activityListener?.launchGooglePlay(task)
    }

    internal fun handlePaymentData(paymentData: PaymentData?) {
        val json = paymentData?.toJson() ?: return

        val googlePay = GooglePay(
            walletToken = json
        )

        makePayment(
            amount = state.value.amountInput.toDoubleOrNull() ?: 0.0,
            paymentMethod = googlePay,
            paymentType = PaymentType.SALE,
            listener = object: Response<Transaction> {
                override fun success(response: Transaction) {
                    Toast.makeText(application, "Payment for $${response.amount} Successful", Toast.LENGTH_SHORT).show()
                }

                override fun error(exception: Throwable?) {
                    Toast.makeText(application, "Error: ${parseError(exception)}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

interface UIComponentsActivityListener{
    fun launchGooglePlay(task: Task<PaymentData>)
    fun closeAndReturnResult(result: Int, data: Intent?)
}