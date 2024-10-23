package io.github.ifa.glancewidget.presentation.widgetsettings

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val widgetSettingsScreenRoute = "widget_settings_screen_route"

fun NavGraphBuilder.widgetSettingsScreen() {
    composable(widgetSettingsScreenRoute) {
        WidgetSettingsScreen()
    }
}

@Composable
internal fun WidgetSettingsScreen(
    viewModel: WidgetSettingsViewModel = hiltViewModel(),
) {

}