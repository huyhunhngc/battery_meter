package io.github.ifa.glancewidget.domain

import androidx.compose.runtime.Composable
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import io.github.ifa.glancewidget.di.LocalRepositories
import io.github.ifa.glancewidget.model.premium.HistoryPurchaseRecord
import io.github.ifa.glancewidget.model.premium.PremiumBillingProduct
import kotlinx.coroutines.flow.Flow

interface PlayBillingRepository {
    suspend fun startConnection(): Boolean
    /**
     * This maybe anti pattern, but so hard to follow clean architecture.
     *
     * This params below come from Android Billing library:
     * - [BillingClient]
     * - [BillingFlowParams]
     * - [Purchase]
     *
     * Because I need the [BillingClient] to launch the billing flow, instead of passing the [Activity] to repository
     * ```
     * billingClient.launchBillingFlow(activity, params)
     * ```
     */
    suspend fun processPurchases(
        productId: String,
        launchBillingFlow: BillingClient.(BillingFlowParams) -> Unit
    )
    suspend fun processSubscriptions(
        productId: String,
        launchBillingFlow: BillingClient.(BillingFlowParams) -> Unit
    )
    suspend fun premiumBillingProducts(): List<PremiumBillingProduct>
    fun premiumProductsFlow(): Flow<List<PremiumBillingProduct>>
}

@Composable
fun localPlayBillingRepository(): PlayBillingRepository {
    return LocalRepositories.current[PlayBillingRepository::class] as PlayBillingRepository
}