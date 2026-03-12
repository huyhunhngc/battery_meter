package io.github.ifa.glancewidget.features.main

import android.annotation.SuppressLint
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.ifa.glancewidget.features.battery.batteryMonitorScreenRoute
import io.github.ifa.glancewidget.ui.localcomposition.LocalAnimatedVisibilityScope
import io.github.ifa.glancewidget.ui.theme.topBarColors

const val mainScreenRoute = "main_screen_route"

fun NavGraphBuilder.mainTabScreens(
    mainNavGraph: NavGraphBuilder.(PaddingValues, SnackbarHostState) -> Unit,
) {
    composable(mainScreenRoute) {
        CompositionLocalProvider(
            LocalAnimatedVisibilityScope provides this@composable,
        ) {
            MainScreen(mainNavGraph = mainNavGraph)
        }
    }
}

@SuppressLint("ImplicitSamInstance")
@Composable
fun MainScreen(
    mainNavGraph: NavGraphBuilder.(PaddingValues, SnackbarHostState) -> Unit,
) {
    val mainTabNavController = rememberNavController()
    val colorScheme = MaterialTheme.colorScheme
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        bottomBar = {
            MainFloatBottomBar(
                navController = mainTabNavController,
                colorScheme = colorScheme,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = topBarColors.containerColor
    ) { contentPadding ->
        NavHost(
            navController = mainTabNavController,
            startDestination = batteryMonitorScreenRoute,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { materialFadeThroughIn() },
            exitTransition = { materialFadeThroughOut() },
        ) {
            mainNavGraph(contentPadding, snackbarHostState)
        }
    }
}

private fun materialFadeThroughIn(): EnterTransition = fadeIn(
    animationSpec = tween(
        durationMillis = 195,
        delayMillis = 105,
        easing = LinearOutSlowInEasing,
    ),
) + scaleIn(
    animationSpec = tween(
        durationMillis = 195,
        delayMillis = 105,
        easing = LinearOutSlowInEasing,
    ),
    initialScale = 0.92f,
)

private fun materialFadeThroughOut(): ExitTransition = fadeOut(
    animationSpec = tween(
        durationMillis = 105,
        delayMillis = 0,
        easing = FastOutLinearInEasing,
    ),
)