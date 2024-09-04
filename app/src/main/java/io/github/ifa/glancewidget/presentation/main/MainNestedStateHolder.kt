package io.github.ifa.glancewidget.presentation.main

import androidx.navigation.NavController
import io.github.ifa.glancewidget.presentation.about.aboutScreenRoute
import io.github.ifa.glancewidget.presentation.widget.widgetScreenRoute

class MainNestedStateHolder {
    val startDestination: String = widgetScreenRoute

    fun onTabSelected(
        mainNestedNavController: NavController,
        tab: MainScreenTab,
    ) {
        when (tab) {
            MainScreenTab.Widget -> mainNestedNavController.navigate(widgetScreenRoute)
            MainScreenTab.About -> mainNestedNavController.navigate(aboutScreenRoute)
        }
    }
}