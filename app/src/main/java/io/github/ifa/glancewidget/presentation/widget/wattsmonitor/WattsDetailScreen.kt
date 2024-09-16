package io.github.ifa.glancewidget.presentation.widget.wattsmonitor

import androidx.annotation.Keep
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.presentation.widget.component.WattsMonitor
import io.github.ifa.glancewidget.ui.localcomposition.LocalAnimatedVisibilityScope
import io.github.ifa.glancewidget.ui.localcomposition.LocalSharedTransitionScope
import kotlinx.serialization.Serializable

fun NavGraphBuilder.wattsDetailScreen(
    onNavigationIconClick: () -> Unit,
) {
    composable<WattsDetailDestination> {
        CompositionLocalProvider(
            LocalAnimatedVisibilityScope provides this@composable,
        ) {
            WattsDetailScreen(onNavigationIconClick = onNavigationIconClick)
        }
    }
}

fun NavController.navigateToWattsDetailScreen(destination: WattsDetailDestination) {
    navigate(destination)
}

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

@Composable
fun WattsDetailScreen(
    viewModel: WattsDetailViewModel = hiltViewModel(),
    onNavigationIconClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    WattsDetailScreen(
        uiState = uiState,
        onNavigationIconClick = onNavigationIconClick,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
internal fun WattsDetailScreen(
    uiState: WattsDetailViewModel.WattsDetailUiState,
    onNavigationIconClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedScope = LocalAnimatedVisibilityScope.current
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.power_monitor),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigationIconClick() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            tint = MaterialTheme.colorScheme.tertiary,
                            contentDescription = "Back",
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        val boxModifier = if (sharedTransitionScope != null && animatedScope != null) {
            with(sharedTransitionScope) {
                Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .sharedElement(
                        state = rememberSharedContentState(key = WattsDetailDestination.STATE_KEY),
                        animatedVisibilityScope = animatedScope,
                    )
            }
        } else {
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .padding(16.dp)
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = boxModifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                WattsMonitor(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(240.dp),
                    power = uiState.details.power,
                    powerPercentage = uiState.details.powerPercentage
                )
            }
        }
    }
}
