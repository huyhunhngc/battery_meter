package io.github.ifa.glancewidget.data.playbilling

import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.queryProductDetails
import io.github.ifa.glancewidget.domain.PlayBillingRepository
import io.github.ifa.glancewidget.model.PremiumBillingProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DefaultPlayBillingRepository(
    private val billingClient: BillingClient
) : PlayBillingRepository {

    private val premiumBillingProducts = MutableStateFlow<List<PremiumBillingProduct>>(listOf())

    override suspend fun startConnection() = suspendCoroutine { continuation ->
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "onBillingSetupFinished: success")
                    continuation.resume(true)
                }
            }

            override fun onBillingServiceDisconnected() {
                continuation.resume(retryBillingServiceConnection())
            }
        })
    }

    private fun retryBillingServiceConnection(): Boolean {
        // TODO
        return false
    }

    override suspend fun processPurchases(
        productId: String,
        launchBillingFlow: BillingClient.(BillingFlowParams) -> Unit
    ) {
        val productList = listOf(createInAppProductParams(productId))
        val params = QueryProductDetailsParams.newBuilder()
        params.setProductList(productList)

        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params.build())
        }
        val billingResult = productDetailsResult.billingResult
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) return
        val productDetails = productDetailsResult.productDetailsList?.firstOrNull() ?: return

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        withContext(Dispatchers.Main) {
            launchBillingFlow(billingClient, billingFlowParams)
        }
    }

    override suspend fun processSubscriptions(
        productId: String,
        launchBillingFlow: BillingClient.(BillingFlowParams) -> Unit
    ) {
        val productList = listOf(createSubscriptionParams(productId))
        val params = QueryProductDetailsParams.newBuilder()
        params.setProductList(productList)

        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params.build())
        }
        val billingResult = productDetailsResult.billingResult
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) return
        val productDetails = productDetailsResult.productDetailsList?.firstOrNull() ?: return
        val selectedOfferToken =
            productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: return
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(selectedOfferToken)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        withContext(Dispatchers.Main) {
            launchBillingFlow(billingClient, billingFlowParams)
        }
    }

    override suspend fun premiumBillingProducts(): List<PremiumBillingProduct> {
        return queryInAppProducts() + querySubscriptions()
    }

    override fun premiumProductsFlow(): Flow<List<PremiumBillingProduct>> {
        return premiumBillingProducts.onStart {
            startConnection()
            premiumBillingProducts.value = premiumBillingProducts()
        }
    }

    private suspend fun queryInAppProducts(): List<PremiumBillingProduct> {
        val productList = listOf(
            createInAppProductParams(PREMIUM_LIFETIME_PLAN_ID),
            createInAppProductParams(STANDARD_LIFETIME_PLAN_ID)
        )
        val params = QueryProductDetailsParams.newBuilder()
        params.setProductList(productList)

        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params.build())
        }
        val productDetails = productDetailsResult.productDetailsList ?: emptyList()
        return productDetails.map {
            val planType = when {
                it.productId.contains(STANDARD_PLAN_ID) -> PremiumBillingProduct.PlanType.Standard
                it.productId.contains(PREMIUM_PLAN_ID) -> PremiumBillingProduct.PlanType.Premium
                else -> PremiumBillingProduct.PlanType.Unknown
            }
            val formattedPrice = it.oneTimePurchaseOfferDetails?.formattedPrice.orEmpty()
            PremiumBillingProduct(
                productId = it.productId,
                productType = PremiumBillingProduct.ProductType.InApp,
                planType = planType,
                formattedPrice = formattedPrice,
                billingPeriod = PremiumBillingProduct.BillingPeriod.Once
            )
        }
    }

    private suspend fun querySubscriptions(): List<PremiumBillingProduct> {
        val productList = listOf(
            createSubscriptionParams(PREMIUM_YEARLY_PLAN_ID),
            createSubscriptionParams(STANDARD_YEARLY_PLAN_ID),
        )
        val params = QueryProductDetailsParams.newBuilder()
        params.setProductList(productList)

        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params.build())
        }
        val productDetails = productDetailsResult.productDetailsList ?: emptyList()
        return productDetails.map {
            val planType = when {
                it.productId.contains(STANDARD_PLAN_ID) -> PremiumBillingProduct.PlanType.Standard
                it.productId.contains(PREMIUM_PLAN_ID) -> PremiumBillingProduct.PlanType.Premium
                else -> PremiumBillingProduct.PlanType.Unknown
            }
            val formattedPrice = it.subscriptionOfferDetails?.firstOrNull()?.pricingPhases
                ?.pricingPhaseList?.firstOrNull()?.formattedPrice.orEmpty()
            PremiumBillingProduct(
                productId = it.productId,
                productType = PremiumBillingProduct.ProductType.Subscription,
                planType = planType,
                formattedPrice = formattedPrice,
                billingPeriod = PremiumBillingProduct.BillingPeriod.Yearly
            )
        }
    }

    private fun createSubscriptionParams(productId: String): Product {
        return Product.newBuilder()
            .setProductId(productId)
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
    }

    private fun createInAppProductParams(productId: String): Product {
        return Product.newBuilder()
            .setProductId(productId)
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
    }

    companion object {
        private const val TAG = "PlayBillingRepository"
        const val STANDARD_PLAN_ID = "standard"
        const val PREMIUM_PLAN_ID = "premium"
        private const val PREMIUM_YEARLY_PLAN_ID = "premium_yearly_plan"
        private const val STANDARD_YEARLY_PLAN_ID = "standard_yearly_plan"
        private const val PREMIUM_LIFETIME_PLAN_ID = "premium_lifetime_plan"
        private const val STANDARD_LIFETIME_PLAN_ID = "standard_lifetime_plan"
    }
}