package io.github.ifa.glancewidget.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.ifa.glancewidget.features.appusage.appUsageScreen
import io.github.ifa.glancewidget.features.paywall.paywallPremiumScreen
import io.github.ifa.glancewidget.features.main.mainTabScreens
import io.github.ifa.glancewidget.features.settings.settingsScreen
import io.github.ifa.glancewidget.features.battery.wattsmonitor.navigateToWattsDetailScreen
import io.github.ifa.glancewidget.features.battery.wattsmonitor.wattsDetailScreen
import io.github.ifa.glancewidget.features.battery.batteryMonitorScreen
import io.github.ifa.glancewidget.features.health.healthScreen
import io.github.ifa.glancewidget.features.settings.inquiry.inquiryScreen
import io.github.ifa.glancewidget.features.settings.inquiry.navigateToInquiryScreen
import io.github.ifa.glancewidget.ui.localcomposition.LocalSharedTransitionScope

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String,
) {
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSharedTransitionScope provides this,
        ) {
            NavHostWithSlideEffect(
                navController = navController,
                startDestination = startDestination,
            ) {
                mainScreen(navController)
                paywallPremiumScreen(onNavigationIconClick = navController::popBackStack)
                wattsDetailScreen(onNavigationIconClick = navController::popBackStack)
                inquiryScreen(onNavigationIconClick = navController::popBackStack)
            }
        }
    }
}

private fun NavGraphBuilder.mainScreen(
    navController: NavHostController,
) {
    mainTabScreens { contentPadding, snackbarHostState ->
        batteryMonitorScreen(
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onOpenWattsDetailScreen = navController::navigateToWattsDetailScreen,
            onOpenInquiryScreen = navController::navigateToInquiryScreen
        )
        healthScreen(contentPadding = contentPadding)
        appUsageScreen()
        settingsScreen(contentPadding = contentPadding)
    }
}
