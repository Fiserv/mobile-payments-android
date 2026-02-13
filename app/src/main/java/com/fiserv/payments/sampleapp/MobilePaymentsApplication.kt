package com.fiserv.payments.sampleapp

import android.app.Application
import com.fiserv.payments.api.core.MobilePayments
import com.fiserv.payments.api.core.Response
import com.fiserv.payments.api.http.data.Environment
import com.fiserv.payments.api.payment.data.PaymentGateway

class MobilePaymentsApplication : Application() {
    var userId: String? = null

    override fun onCreate() {
        super.onCreate()

        MobilePayments.initialize(
            application = this,
            environment = Environment.SANDBOX,
            clientToken = "ee6e63dae8e7467280e9e6ce6ffe3aa4",
            businessLocationId = "1001",
            response = object: Response<List<PaymentGateway>?>{
                override fun success(response: List<PaymentGateway>?) {

                }

                override fun error(exception: Throwable?) {

                }
            }
        )
    }
}