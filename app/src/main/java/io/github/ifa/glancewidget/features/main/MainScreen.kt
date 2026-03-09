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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.ifa.glancewidget.features.widget.widgetScreenRoute
import io.github.ifa.glancewidget.ui.localcomposition.LocalAnimatedVisibilityScope
import io.github.ifa.glancewidget.utils.startBatteryStatus
import io.github.ifa.glancewidget.utils.stopBatteryStatus

const val mainScreenRoute = "main_screen_route"

fun NavGraphBuilder.mainTabScreens(
    mainNavGraph: NavGraphBuilder.(NavController) -> Unit,
) {
    composable(mainScreenRoute) {
        CompositionLocalProvider(
            LocalAnimatedVisibilityScope provides this@composable,
        ) {
            MainScreen(
                mainNavGraph = mainNavGraph,
            )
        }
    }
}

@SuppressLint("ImplicitSamInstance")
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    mainNavGraph: NavGraphBuilder.(NavController) -> Unit,
) {
    val mainTabNavController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    LaunchedEffect(uiState.shouldStartNotification) {
        if (uiState.shouldStartNotification) {
            context.startBatteryStatus()
        } else {
            context.stopBatteryStatus()
        }
    }
    Scaffold(
        bottomBar = {
            FloatBottomBar(
                navController = mainTabNavController,
                colorScheme = colorScheme
            )
        }
    ) { contentPadding ->
        contentPadding
        NavHost(
            navController = mainTabNavController,
            startDestination = widgetScreenRoute,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { materialFadeThroughIn() },
            exitTransition = { materialFadeThroughOut() },
        ) {
            mainNavGraph(mainTabNavController)
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