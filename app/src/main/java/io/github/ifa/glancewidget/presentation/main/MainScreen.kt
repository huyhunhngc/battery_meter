package io.github.ifa.glancewidget.presentation.main

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.zIndex
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.ifa.glancewidget.presentation.widget.widgetScreenRoute

const val mainScreenRoute = "main_screen_route"

fun NavGraphBuilder.mainTabScreens(
    mainNavGraph: NavGraphBuilder.(NavController, PaddingValues) -> Unit,
) {
    composable(mainScreenRoute) {
        MainScreen(
            mainNavGraph = mainNavGraph,
        )
    }
}

@Composable
fun MainScreen(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    mainNavGraph: NavGraphBuilder.(NavController, PaddingValues) -> Unit,
) {
    val mainTabNavController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = mainTabNavController, modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { padding ->
        NavHost(
            navController = mainTabNavController,
            startDestination = widgetScreenRoute,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { materialFadeThroughIn() },
            exitTransition = { materialFadeThroughOut() },
        ) {
            mainNavGraph(mainTabNavController, padding)
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