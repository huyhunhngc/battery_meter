package io.github.ifa.glancewidget.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.github.ifa.glancewidget.features.about.aboutScreen
import io.github.ifa.glancewidget.features.appusage.appUsageScreen
import io.github.ifa.glancewidget.features.paywall.paywallPremiumScreen
import io.github.ifa.glancewidget.features.main.mainTabScreens
import io.github.ifa.glancewidget.features.settings.settingsScreen
import io.github.ifa.glancewidget.features.battery.wattsmonitor.navigateToWattsDetailScreen
import io.github.ifa.glancewidget.features.battery.wattsmonitor.wattsDetailScreen
import io.github.ifa.glancewidget.features.battery.batteryMonitorScreen
import io.github.ifa.glancewidget.features.health.healthScreen
import io.github.ifa.glancewidget.ui.localcomposition.LocalSharedTransitionScope
import io.github.ifa.glancewidget.utils.navigateUrl

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
) {
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSharedTransitionScope provides this,
        ) {
            NavHostWithSlideEffect(
                navController = navController,
                startDestination = startDestination,
                modifier = modifier,
            ) {
                mainScreen(navController)
                paywallPremiumScreen(onNavigationIconClick = navController::popBackStack)
                aboutScreen(
                    onNavigationIconClick = navController::popBackStack,
                    onExternalUrlClick = { navigateUrl(it) },
                )
                wattsDetailScreen(
                    onNavigationIconClick = navController::popBackStack
                )
            }
        }
    }
}

private fun NavGraphBuilder.mainScreen(
    navController: NavHostController,
) {
    mainTabScreens { navMainController, contentPadding ->
        batteryMonitorScreen(
            contentPadding = contentPadding,
            onOpenWattsDetailScreen = navController::navigateToWattsDetailScreen
        )
        healthScreen(
            contentPadding = contentPadding
        )
        appUsageScreen()
        settingsScreen(
            contentPadding = contentPadding,
            onOpenAboutScreen = navController::navigateToAboutScreen,
        )
    }
}
