package io.github.ifa.glancewidget.presentation.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.presentation.about.aboutScreenRoute
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
    Settings(settingsScreenRoute, R.string.settings, R.drawable.ic_setting_filled, R.drawable.ic_settings);

    companion object {
        fun indexOf(tab: MainScreenTab): Int = entries.indexOf(tab)
        fun routeToTab(route: String?): MainScreenTab = entries.find { it.route == route } ?: Widget
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentTab = MainScreenTab.routeToTab(navBackStackEntry?.destination?.route)

    NavigationBar {
        MainScreenTab.entries.forEach { tab ->
            val isSelected = tab == currentTab
            NavigationBarItem(
                modifier = Modifier.padding(1.dp),
                label = {
                    Text(
                        text = stringResource(id = tab.label),
                        color = colorScheme.surfaceTint,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                alwaysShowLabel = true, selected = isSelected,
                icon = {
                    Icon(
                        painter = painterResource(id = if (isSelected) tab.selectedIcon else tab.icon),
                        contentDescription = null,
                        modifier = Modifier,
                        tint = colorScheme.surfaceTint
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
            )
        }
    }
}