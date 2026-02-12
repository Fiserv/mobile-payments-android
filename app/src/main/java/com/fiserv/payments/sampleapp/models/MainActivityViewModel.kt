package com.fiserv.payments.sampleapp.models

import android.app.Application
import com.fiserv.payments.api.core.MobilePayments
import com.fiserv.payments.ui.views.models.LoadingListener
import com.fiserv.payments.ui.views.models.MobilePaymentsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MainActivityState(
    var transactionMessage: String = "",
    var userId: String = "",
    var isLoading: Boolean = false,
)

class MainActivityViewModel(application: Application) : MobilePaymentsViewModel(application), LoadingListener {
    private val _state = MutableStateFlow(MainActivityState())
    val state = _state.asStateFlow()

    fun updateUserId(userId: String){
        _state.update { currentState ->
            currentState.copy(
                userId = userId,
            )
        }
    }

    fun updateTransactionMessage(transactionMessage: String){
        _state.update { currentState ->
            currentState.copy(
                transactionMessage = transactionMessage,
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
}
