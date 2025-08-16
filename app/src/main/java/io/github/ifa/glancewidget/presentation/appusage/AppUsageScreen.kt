package io.github.ifa.glancewidget.presentation.appusage

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.ColorFilter
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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        AppUsageScreenContent(
            snackbarHostState = snackbarHostState,
            uiState = uiState,
            onUsageRangeChange = viewModel::setUsageRange
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
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
                title = stringResource(id = MainScreenTab.AppUsage.label),
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
                ) { packageName ->
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = android.net.Uri.fromParts("package", packageName, null)
                        }
                    )
                }
            } else {
                AppUsagePermission(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun AppUsagePermission(
    modifier: Modifier = Modifier, onRequestPermission: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_program_time),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(id = R.string.should_allow_app_usage),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(id = R.string.should_allow_app_usage_desc),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium
        )
        Image(
            painter = painterResource(id = R.drawable.ic_no_permission),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            alignment = Alignment.Center,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = onRequestPermission) {
                Text(
                    text = stringResource(id = R.string.request_app_usage_permission),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AppUsageScreenContentPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState = AppUsageScreenUiState()
    AppUsageScreenContent(
        snackbarHostState = snackbarHostState, uiState = uiState, onUsageRangeChange = {})
}