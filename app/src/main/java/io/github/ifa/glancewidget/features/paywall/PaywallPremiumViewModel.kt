package io.github.ifa.glancewidget.features.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ifa.glancewidget.data.playbilling.OnPurchaseListener
import io.github.ifa.glancewidget.data.playbilling.PurchaseListener
import io.github.ifa.glancewidget.domain.PlayBillingRepository
import io.github.ifa.glancewidget.domain.PremiumFeatureUseCase
import io.github.ifa.glancewidget.utils.buildUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaywallPremiumViewModel @Inject constructor(
    private val playBillingRepository: PlayBillingRepository,
    private val premiumFeatureUseCase: PremiumFeatureUseCase,
    private val purchaseListener: PurchaseListener
) : ViewModel() {
    private val onPurchaseListener by lazy {
        object : OnPurchaseListener {
            override fun onPurchase(purchases: List<Purchase>) {
                handlePurchase(purchases)
            }

            override fun onPurchaseError(billingResult: BillingResult) {
                _purchaseState.value = GoPremiumUiState.PurchaseState.ERROR
            }

            override fun onPurchaseCancelled() {
                _purchaseState.value = GoPremiumUiState.PurchaseState.CANCELED
            }
        }
    }

    init {
        purchaseListener.registerPurchaseListener(onPurchaseListener)
    }

    data class GoPremiumUiState(
        val premiumPlanData: List<PremiumPlanData>,
        val purchaseState: PurchaseState = PurchaseState.IDLE,
    ) {
        val showPurchaseStateBottomSheet =
            purchaseState != PurchaseState.IDLE && purchaseState != PurchaseState.CANCELED

        enum class PurchaseState {
            IDLE, PURCHASED, CANCELED, ERROR
        }
    }

    private val _purchaseState = MutableStateFlow(GoPremiumUiState.PurchaseState.IDLE)
    private val _premiumProducts =
        playBillingRepository.premiumProductsFlow().map { it.toPremiumPlanData() }.catch { }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PremiumPlan.premiumPlans
            )

    val uiState =
        buildUiState(_premiumProducts, _purchaseState) { premiumPlanData, purchaseState ->
            GoPremiumUiState(
                premiumPlanData = premiumPlanData,
                purchaseState = purchaseState
            )
        }

    fun handlePurchase(purchases: List<Purchase>) {
        viewModelScope.launch {
            premiumFeatureUseCase.handlePurchases(purchases)
        }
    }

    fun processSubscription(
        planId: String,
        launchBillingFlow: BillingClient.(BillingFlowParams) -> Unit
    ) {
        viewModelScope.launch {
            playBillingRepository.processSubscriptions(planId, launchBillingFlow)
        }
    }

    fun processPurchases(
        planId: String,
        launchBillingFlow: BillingClient.(BillingFlowParams) -> Unit
    ) {
        viewModelScope.launch {
            playBillingRepository.processPurchases(planId, launchBillingFlow)
        }
    }

    override fun onCleared() {
        purchaseListener.unregisterPurchaseListener(onPurchaseListener)
        super.onCleared()
    }

    fun hidePurchaseStateBottomSheet() {
        _purchaseState.value = GoPremiumUiState.PurchaseState.IDLE
    }
}