package io.github.ifa.glancewidget.presentation.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.presentation.appusage.appUsageScreenRoute
import io.github.ifa.glancewidget.presentation.settings.settingsScreenRoute
import io.github.ifa.glancewidget.presentation.widget.widgetScreenRoute

enum class MainScreenTab(
    val route: String,
    @StringRes val label: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val icon: Int,
) {
    Widget(
        widgetScreenRoute, R.string.widget_tab, R.drawable.ic_widgets_filled, R.drawable.ic_widgets
    ),
//    AppUsage(
//        appUsageScreenRoute, R.string.app_usage, R.drawable.ic_analytics_filled, R.drawable.ic_analytics
//    ),
    Settings(
        settingsScreenRoute, R.string.settings, R.drawable.ic_setting_filled, R.drawable.ic_settings
    );

    companion object {
        fun indexOf(tab: MainScreenTab): Int = entries.indexOf(tab)
        fun routeToTab(route: String?): MainScreenTab = entries.find { it.route == route } ?: Widget
    }
}

fun NavigationSuiteScope.mainScreenNavigation(
    navController: NavController,
    currentTab: MainScreenTab,
    colorScheme: ColorScheme
) {
    MainScreenTab.entries.forEach { tab ->
        val isSelected = tab == currentTab
        item(
            selected = isSelected,
            label = {
                Text(
                    text = stringResource(id = tab.label),
                    color = colorScheme.secondary,
                )
            },
            icon = {
                Icon(
                    painter = painterResource(id = if (isSelected) tab.selectedIcon else tab.icon),
                    contentDescription = null,
                    modifier = Modifier,
                    tint = if (isSelected) colorScheme.surfaceTint else colorScheme.secondary
                )
            },
            onClick = {
                navController.navigate(tab.route) {
                    navController.graph.startDestinationRoute?.let { route ->
                        popUpTo(route) { saveState = true }
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            alwaysShowLabel = true,
        )
    }
}