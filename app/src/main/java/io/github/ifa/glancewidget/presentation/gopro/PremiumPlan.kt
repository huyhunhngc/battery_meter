package io.github.ifa.glancewidget.presentation.gopro

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import io.github.ifa.glancewidget.R

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
        @StringRes val text: Int,
        @StringRes val description: Int,
        val price: String,
    )
}

object PremiumPlan {
    private val premiumPlans = listOf(
        PremiumPlanData(
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
                price = "0.79€"
            ),
            secondaryAction = PremiumPlanData.ActionPlanButton(
                text = R.string.lifetime_plan,
                description = R.string.lifetime_plan_price,
                price = "2.19€"
            )
        ),
        PremiumPlanData(
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
                price = "0.99€"
            ),
            secondaryAction = PremiumPlanData.ActionPlanButton(
                text = R.string.lifetime_plan,
                description = R.string.lifetime_plan_price,
                price = "2.99€"
            )
        )
    )
    @Composable
    fun Provide(content: @Composable () -> Unit) {
        CompositionLocalProvider(LocalPremiumPlan provides premiumPlans) {
            content()
        }
    }
}