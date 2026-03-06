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
            clientToken = "FISERV_TOKEN",
            businessLocationId = "LOCATION_ID",
        )
    }
}