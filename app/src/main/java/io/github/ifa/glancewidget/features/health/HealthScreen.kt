package io.github.ifa.glancewidget.features.health

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.features.health.component.BatteryExtraInformation
import io.github.ifa.glancewidget.model.wrapper.BatteryDataWrapper
import io.github.ifa.glancewidget.ui.component.appPadding
import io.github.ifa.glancewidget.ui.theme.topBarColors

const val healthScreenRoute = "health_screen_route"

fun NavGraphBuilder.healthScreen(
    contentPadding: PaddingValues,
) {
    composable(healthScreenRoute) {
        HealthScreen(contentPadding = contentPadding)
    }
}

@Composable
internal fun HealthScreen(
    viewModel: HealthViewModel = hiltViewModel(),
    contentPadding: PaddingValues,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    HealthScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        contentPadding = contentPadding,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HealthScreen(
    uiState: HealthViewModel.HealthScreenUiState,
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Appbar(scrollBehavior = scrollBehavior)
        },
        containerColor = topBarColors.containerColor,
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding()),
            modifier = Modifier
                .appPadding()
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            batteryExtraInformation(
                batteryDataWrapper = uiState.batteryOverall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

private fun LazyListScope.batteryExtraInformation(
    modifier: Modifier = Modifier,
    batteryDataWrapper: BatteryDataWrapper,
) {
    item {
        BatteryExtraInformation(modifier, batteryDataWrapper)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Appbar(
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.health),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        subtitle = {},
        titleHorizontalAlignment = Alignment.CenterHorizontally,
        scrollBehavior = scrollBehavior,
        colors = topBarColors
    )
}