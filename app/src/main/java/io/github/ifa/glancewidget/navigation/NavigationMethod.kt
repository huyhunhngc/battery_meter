package io.github.ifa.glancewidget.navigation

import androidx.navigation.NavController
import io.github.ifa.glancewidget.features.about.aboutScreenRoute
import io.github.ifa.glancewidget.features.gopro.paywallPremiumScreenRoute

fun NavController.navigateToAboutScreen() {
    navigate(aboutScreenRoute)
}

fun NavController.navigateToGoPremiumScreen() {
    navigate(paywallPremiumScreenRoute)
}
