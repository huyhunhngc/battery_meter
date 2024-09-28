package io.github.ifa.glancewidget.domain

import androidx.compose.runtime.Composable
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import io.github.ifa.glancewidget.di.LocalRepositories
import io.github.ifa.glancewidget.model.PremiumBillingProduct
import kotlinx.coroutines.flow.Flow

interface PlayBillingRepository {
    suspend fun startConnection(): Boolean
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