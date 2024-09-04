package io.github.ifa.glancewidget.presentation.about

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val aboutScreenRoute = "about_screen_route"

fun NavGraphBuilder.aboutScreen() {
    composable(aboutScreenRoute) {
        AboutScreen()
    }
}

@Composable
fun AboutScreen() {

}