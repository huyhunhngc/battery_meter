package io.github.ifa.glancewidget.features.battery.wattsmonitor

import androidx.annotation.Keep
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.features.battery.component.WattsMonitor
import io.github.ifa.glancewidget.ui.component.AnimatedTextTopAppBar
import io.github.ifa.glancewidget.ui.localcomposition.LocalAnimatedVisibilityScope
import io.github.ifa.glancewidget.ui.localcomposition.LocalSharedTransitionScope
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class WattsDetailDestination(
    val power: Float = 1.0f,
    val powerPercentage: Float = 0.5f
) {
    companion object {
        const val STATE_KEY = "watts_detail_destination_key"
    }
}

fun NavGraphBuilder.wattsDetailScreen(
    onNavigationIconClick: () -> Unit,
) {
    composable<WattsDetailDestination> {
        CompositionLocalProvider(
            LocalAnimatedVisibilityScope provides this@composable,
        ) {
            WattsDetailScreen(
                animatedVisibilityScope = this@composable,
                onNavigationIconClick = onNavigationIconClick,
            )
        }
    }
}

fun NavController.navigateToWattsDetailScreen(destination: WattsDetailDestination) {
    navigate(destination)
}

@Composable
fun WattsDetailScreen(
    viewModel: WattsDetailViewModel = hiltViewModel(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigationIconClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    WattsDetailScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        animatedVisibilityScope = animatedVisibilityScope,
        onNavigationIconClick = onNavigationIconClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
internal fun WattsDetailScreen(
    uiState: WattsDetailViewModel.WattsDetailUiState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    snackbarHostState: SnackbarHostState,
    onNavigationIconClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val sharedTransitionScope = LocalSharedTransitionScope.current

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            AnimatedTextTopAppBar(
                title = stringResource(id = R.string.power_monitor),
                navigationIcon = {
                    IconButton(
                        onClick = { onNavigationIconClick() },
                        colors = IconButtonDefaults.iconButtonColors().copy(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = "Back",
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val sharedModifier = if (sharedTransitionScope != null) {
                with(sharedTransitionScope) {
                    Modifier
                        .padding(16.dp)
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = WattsDetailDestination.STATE_KEY),
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                }
            } else {
                Modifier.padding(16.dp)
            }

            Box(
                modifier = sharedModifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                WattsMonitor(
                    modifier = Modifier
                        .padding(24.dp)
                        .size(240.dp),
                    power = uiState.details.power,
                    powerPercentage = uiState.details.powerPercentage
                )
            }
        }
    }
}
