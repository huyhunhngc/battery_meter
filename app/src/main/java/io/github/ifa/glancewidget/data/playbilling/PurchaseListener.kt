package io.github.ifa.glancewidget.data.playbilling

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PurchaseListener @Inject constructor() {
    private val listeners = mutableListOf<OnPurchaseListener>()

    val onPurchasesUpdated = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            listeners.forEach { listener ->
                purchases.forEach { purchase ->
                    listener.onPurchase(purchase)
                }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            listeners.forEach { listener ->
                listener.onPurchaseCancelled()
            }
        } else {
            listeners.forEach { listener ->
                listener.onPurchaseError(billingResult)
            }
        }
    }

    fun registerPurchaseListener(onPurchaseListener: OnPurchaseListener) {
        listeners.add(onPurchaseListener)
    }

    fun unregisterPurchaseListener(onPurchaseListener: OnPurchaseListener) {
        listeners.remove(onPurchaseListener)
    }
}

interface OnPurchaseListener {
    fun onPurchase(purchase: Purchase)
    fun onPurchaseError(billingResult: BillingResult)
    fun onPurchaseCancelled()
}