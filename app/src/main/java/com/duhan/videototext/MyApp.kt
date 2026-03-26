package com.duhan.videototext

import android.app.Application
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp: Application(){
    override fun onCreate() {
        super.onCreate()
        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(PurchasesConfiguration.Builder(this, "git init").build())
    }
}


//219505712898-2bb9iq08lsupgduhs6kfqkutk2hcvh0k.apps.googleusercontent.com id

//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    secret

