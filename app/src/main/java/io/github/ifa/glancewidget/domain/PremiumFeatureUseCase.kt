package io.github.ifa.glancewidget.domain

import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PremiumFeatureUseCase @Inject constructor(
    private val playBillingRepository: PlayBillingRepository,
    private val billingClient: BillingClient
) {
    suspend fun checkPremiumStatus(): Boolean {
        if (!playBillingRepository.startConnection()) return false
        val historyPurchaseRecords = playBillingRepository.historyPurchaseRecords()
        return true
    }
    suspend fun handlePurchases(purchases: List<Purchase>) {
        purchases.forEach { handlePurchase(it) }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                withContext(Dispatchers.IO) {
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams) {

                    }
                }
            }
        }
    }
}