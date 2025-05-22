package io.github.ifa.glancewidget.presentation.appusage

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.presentation.appusage.component.AppUsageList
import io.github.ifa.glancewidget.presentation.main.MainScreenTab
import io.github.ifa.glancewidget.ui.component.AnimatedTextTopAppBar

const val appUsageScreenRoute = "app_usage_screen_route"

fun NavGraphBuilder.appUsageScreen() {
    composable(appUsageScreenRoute) {
        AppUsageScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppUsageScreen(
    viewModel: AppUsageViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AppUsageScreenContent(
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        onUsageRangeChange = viewModel::setUsageRange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppUsageScreenContent(
    snackbarHostState: SnackbarHostState,
    uiState: AppUsageScreenUiState,
    onUsageRangeChange: (AppUsageScreenUiState.UsageRange) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
) {
    val context = LocalContext.current
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            AnimatedTextTopAppBar(
                title = stringResource(id = MainScreenTab.Settings.label),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.hasAppUsagePermission) {
                AppUsageList(
                    appUsageStats = uiState.appUsageStats,
                    usageRange = uiState.usageRange,
                    onUsageRangeChange = onUsageRangeChange,
                    modifier = Modifier
                )
            } else {
                OutlinedButton(onClick = {
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    context.startActivity(intent)
                }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_monitoring),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = stringResource(id = R.string.request_app_usage_permission),
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AppUsageScreenContentPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState = AppUsageScreenUiState()
    AppUsageScreenContent(
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        onUsageRangeChange = {}
    )
}