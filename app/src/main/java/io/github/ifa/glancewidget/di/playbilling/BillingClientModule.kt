package io.github.ifa.glancewidget.di.playbilling

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PendingPurchasesParams
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.ifa.glancewidget.data.playbilling.PurchaseListener
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BillingClientModule {
    @Provides
    @Singleton
    fun provideBillingClient(
        @ApplicationContext applicationContext: Context,
        purchaseListener: PurchaseListener
    ): BillingClient {
        val pendingPurchasesParams = PendingPurchasesParams
            .newBuilder()
            .enableOneTimeProducts()
            .build()
        return BillingClient.newBuilder(applicationContext)
            .setListener(purchaseListener.onPurchasesUpdated)
            .enablePendingPurchases(pendingPurchasesParams)
            .build()
    }
}