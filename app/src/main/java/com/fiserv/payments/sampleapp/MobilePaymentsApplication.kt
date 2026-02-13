package com.fiserv.payments.sampleapp

import android.app.Application
import com.fiserv.payments.api.core.MobilePayments
import com.fiserv.payments.api.http.data.Environment

class MobilePaymentsApplication : Application() {
    var customerId: String? = null

    override fun onCreate() {
        super.onCreate()

        MobilePayments.initialize(
            application = this,
            environment = Environment.SANDBOX,
            clientToken = "ee6e63dae8e7467280e9e6ce6ffe3aa4",
            businessLocationId = "1001",
        )
    }
}