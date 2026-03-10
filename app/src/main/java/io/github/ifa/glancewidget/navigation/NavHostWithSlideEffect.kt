package io.github.ifa.glancewidget.navigation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun NavHostWithSlideEffect(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    route: String? = null,
    builder: NavGraphBuilder.() -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        contentAlignment = contentAlignment,
        route = route,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(durationMillis = 350, easing = Easing)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis = 350, easing = Easing)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(durationMillis = 350, easing = Easing)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(durationMillis = 350, easing = Easing)
            )
        },
        builder = builder,
    )
}

private val Easing = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)