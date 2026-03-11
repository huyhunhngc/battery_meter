package io.github.ifa.glancewidget.features.paywall

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.premium.PremiumBillingProduct

internal val LocalPremiumPlan = compositionLocalOf<List<PremiumPlanData>> { listOf() }

data class PremiumPlanData(
    @StringRes val title: Int,
    val features: List<Feature>,
    @StringRes val description: Int,
    val primaryAction: ActionPlanButton,
    val secondaryAction: ActionPlanButton
) {
    data class Feature(
        @DrawableRes val icon: Int,
        @StringRes val text: Int
    )

    data class ActionPlanButton(
        val planId: String = "",
        @StringRes val text: Int,
        @StringRes val description: Int,
        val price: String,
    )
}

object PremiumPlan {
    val standardPlanSample = PremiumPlanData(
        title = R.string.standard,
        description = R.string.free_trial_description,
        features = listOf(
            PremiumPlanData.Feature(
                icon = R.drawable.ic_palette,
                text = R.string.change_your_theme
            ),
            PremiumPlanData.Feature(
                icon = R.drawable.ic_sync,
                text = R.string.change_your_theme_sync_widget
            ),
            PremiumPlanData.Feature(
                icon = R.drawable.ic_contrast,
                text = R.string.more_widget_style
            ),
        ),
        primaryAction = PremiumPlanData.ActionPlanButton(
            text = R.string.yearly_plan,
            description = R.string.yearly_plan_price,
            price = ""
        ),
        secondaryAction = PremiumPlanData.ActionPlanButton(
            text = R.string.lifetime_plan,
            description = R.string.lifetime_plan_price,
            price = ""
        )
    )
    val premiumPlanSample = PremiumPlanData(
        title = R.string.premium,
        description = R.string.free_trial_description,
        features = listOf(
            PremiumPlanData.Feature(
                icon = R.drawable.ic_palette,
                text = R.string.change_your_theme
            ),
            PremiumPlanData.Feature(
                icon = R.drawable.ic_sync,
                text = R.string.change_your_theme_sync_widget
            ),
            PremiumPlanData.Feature(
                icon = R.drawable.ic_contrast,
                text = R.string.more_widget_style
            ),
            PremiumPlanData.Feature(
                icon = R.drawable.ic_monitoring,
                text = R.string.history_stats_monitor
            ),
            PremiumPlanData.Feature(
                icon = R.drawable.ic_widgets,
                text = R.string.floating_battery_monitor
            ),
        ),
        primaryAction = PremiumPlanData.ActionPlanButton(
            text = R.string.yearly_plan,
            description = R.string.yearly_plan_price,
            price = ""
        ),
        secondaryAction = PremiumPlanData.ActionPlanButton(
            text = R.string.lifetime_plan,
            description = R.string.lifetime_plan_price,
            price = ""
        )
    )

    val premiumPlans = listOf(standardPlanSample, premiumPlanSample)

    @Composable
    fun Provide(
        premiumPlans: List<PremiumPlanData>,
        content: @Composable () -> Unit,
    ) {
        CompositionLocalProvider(LocalPremiumPlan provides premiumPlans) {
            content()
        }
    }
}

fun List<PremiumBillingProduct>.toPremiumPlanData(): List<PremiumPlanData> {
    val plans = this.partition {
        it.planType == PremiumBillingProduct.PlanType.Standard
    }
    val standardPlan = plans.first.chunked(2).map { products ->
        val subscriptions =
            products.firstOrNull { it.productType == PremiumBillingProduct.ProductType.Subscription }
        val inAppPurchases =
            products.firstOrNull { it.productType == PremiumBillingProduct.ProductType.InApp }
        PremiumPlan.standardPlanSample.copy(
            primaryAction = PremiumPlan.standardPlanSample.primaryAction.copy(
                planId = subscriptions?.productId.orEmpty(),
                price = subscriptions?.formattedPrice.orEmpty()
            ),
            secondaryAction = PremiumPlan.standardPlanSample.secondaryAction.copy(
                planId = inAppPurchases?.productId.orEmpty(),
                price = inAppPurchases?.formattedPrice.orEmpty()
            )
        )
    }
    val premiumPlan = plans.second.chunked(2).map { products ->
        val subscriptions =
            products.firstOrNull { it.productType == PremiumBillingProduct.ProductType.Subscription }
        val inAppPurchases =
            products.firstOrNull { it.productType == PremiumBillingProduct.ProductType.InApp }
        PremiumPlan.premiumPlanSample.copy(
            primaryAction = PremiumPlan.premiumPlanSample.primaryAction.copy(
                planId = subscriptions?.productId.orEmpty(),
                price = subscriptions?.formattedPrice.orEmpty()
            ),
            secondaryAction = PremiumPlan.premiumPlanSample.secondaryAction.copy(
                planId = inAppPurchases?.productId.orEmpty(),
                price = inAppPurchases?.formattedPrice.orEmpty()
            )
        )
    }
    return standardPlan + premiumPlan
}