package io.github.ifa.glancewidget.presentation.widget

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.MainActivity
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.PINNED_WIDGET_DEFAULT_ID
import io.github.ifa.glancewidget.model.AddWidgetParams
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.BonnedDeviceSettings
import io.github.ifa.glancewidget.model.ChartRecord
import io.github.ifa.glancewidget.model.wrapper.BatteryDataWrapper
import io.github.ifa.glancewidget.presentation.main.MainScreenTab
import io.github.ifa.glancewidget.presentation.widget.component.AddWidgetBottomSheet
import io.github.ifa.glancewidget.presentation.widget.component.BatteryExtraInformation
import io.github.ifa.glancewidget.presentation.widget.component.BatteryOverall
import io.github.ifa.glancewidget.presentation.widget.component.BonedDeviceItem
import io.github.ifa.glancewidget.presentation.widget.component.ConnectedDevice
import io.github.ifa.glancewidget.presentation.widget.component.DropdownMenu
import io.github.ifa.glancewidget.presentation.widget.component.MeasurementWarning
import io.github.ifa.glancewidget.presentation.widget.wattsmonitor.WattsDetailDestination
import io.github.ifa.glancewidget.ui.component.AnimatedTextTopAppBar
import io.github.ifa.glancewidget.ui.component.appPadding
import io.github.ifa.glancewidget.utils.findActivity
import io.github.ifa.glancewidget.utils.requestToPinWidget
import kotlinx.coroutines.launch

const val widgetScreenRoute = "widget_screen_route"

fun NavGraphBuilder.widgetScreen(
    onOpenWattsDetailScreen: (WattsDetailDestination) -> Unit
) {
    composable(widgetScreenRoute) {
        WidgetScreen(onOpenWattsDetailScreen = onOpenWattsDetailScreen)
    }
}

@Composable
internal fun WidgetScreen(
    viewModel: WidgetViewModel = hiltViewModel(),
    onOpenWattsDetailScreen: (WattsDetailDestination) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showBottomSheet by rememberUpdatedState(uiState.setupWidgetId != INVALID_APPWIDGET_ID)
    val context = LocalContext.current
    val activity = context.findActivity()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val addPinnedWidgetMessage = stringResource(id = R.string.pinned_widget_added)
    LaunchedEffect(Unit) {
        val intent = activity?.intent ?: return@LaunchedEffect
        viewModel.controlExtras(intent)
    }
    WidgetScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        isShowAddWidgetBottomSheet = showBottomSheet,
        onOpenWattsDetailScreen = onOpenWattsDetailScreen,
        onDisMissBottomSheet = viewModel::hideBottomSheet,
        onClickAddWidget = { params ->
            if (uiState.setupWidgetId == PINNED_WIDGET_DEFAULT_ID) {
                val isSupported = activity?.requestToPinWidget(params)
                if (isSupported == true) {
                    scope.launch {
                        snackbarHostState.showSnackbar(addPinnedWidgetMessage)
                    }
                    viewModel.hideBottomSheet()
                }
            } else {
                viewModel.saveTransparentSettings(params.isTransparent, uiState.setupWidgetId)
                activity?.addWidget(uiState.setupWidgetId)
            }
        },
        onRequestPiningWidget = viewModel::createPinnedWidget,
        onShowInWidgetChanged = viewModel::updateDeviceShowInWidget,
        onForceReloadClick = {
            activity?.apply {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                finish()
                startActivity(intent)
            }
        }
    )
}

fun Activity.addWidget(appWidgetId: Int) {
    val resultValue = Intent().apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    }
    setResult(RESULT_OK, resultValue)
    finish()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WidgetScreen(
    uiState: WidgetViewModel.WidgetScreenUiState,
    snackbarHostState: SnackbarHostState,
    isShowAddWidgetBottomSheet: Boolean = false,
    onOpenWattsDetailScreen: (WattsDetailDestination) -> Unit,
    onDisMissBottomSheet: () -> Unit = {},
    onClickAddWidget: (AddWidgetParams) -> Unit,
    onRequestPiningWidget: () -> Unit = {},
    onShowInWidgetChanged: (String, Boolean) -> Unit,
    onForceReloadClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Appbar(
                scrollBehavior = scrollBehavior,
                onClickAddWidget = onRequestPiningWidget,
                onForceReloadClick = onForceReloadClick
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .appPadding()
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            batteryMeasurementWarning(uiState.showMeasurementWarning)
            batteryOverall(
                batteryDataWrapper = uiState.batteryOverall,
                chartTrackingData = uiState.chartTrackingData,
                onOpenWattsDetailScreen = onOpenWattsDetailScreen,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            batteryExtraInformation(
                batteryDataWrapper = uiState.batteryOverall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            connectedDevices(
                modifier = Modifier.padding(bottom = 16.dp),
                batteryConnectedDevices = uiState.batteryOverall.batteryData.batteryConnectedDevices,
                batteryDeviceSettings = uiState.bonnedDeviceSettings,
                onShowInWidgetChanged = onShowInWidgetChanged
            )
        }

        if (isShowAddWidgetBottomSheet) {
            AddWidgetBottomSheet(
                uiState = uiState,
                padding = padding,
                onDisMiss = onDisMissBottomSheet,
                onClickAddWidget = onClickAddWidget
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Appbar(
    scrollBehavior: TopAppBarScrollBehavior,
    onClickAddWidget: () -> Unit,
    onForceReloadClick: () -> Unit,
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    AnimatedTextTopAppBar(
        title = stringResource(id = MainScreenTab.Widget.label),
        scrollBehavior = scrollBehavior,
        actions = {
            IconButton(onClick = { dropdownExpanded = true }) {
                Icon(
                    Icons.Filled.MoreVert, contentDescription = null
                )
            }
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
                onAddWidgetClick = {
                    onClickAddWidget()
                    dropdownExpanded = false
                },
                onForceReloadClick = {
                    onForceReloadClick()
                    dropdownExpanded = false
                }
            )
        }
    )
}

private fun LazyListScope.batteryMeasurementWarning(
    showMeasurementWarning: Boolean
) {
    item {
        AnimatedVisibility(
            visible = showMeasurementWarning,
            enter = slideInVertically(),
        ) {
            MeasurementWarning()
        }
    }
}


private fun LazyListScope.batteryOverall(
    batteryDataWrapper: BatteryDataWrapper,
    chartTrackingData: ChartRecord,
    onOpenWattsDetailScreen: (WattsDetailDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    item {
        BatteryOverall(
            modifier = modifier,
            batteryDataWrapper = batteryDataWrapper,
            chartTrackingData = chartTrackingData,
            onOpenWattsDetailScreen = onOpenWattsDetailScreen
        )
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

private fun LazyListScope.connectedDevices(
    modifier: Modifier = Modifier,
    batteryConnectedDevices: List<BonedDevice>,
    batteryDeviceSettings: BonnedDeviceSettings,
    onShowInWidgetChanged: (String, Boolean) -> Unit,
) {
    if (batteryConnectedDevices.isNotEmpty()) {
        item {
            ConnectedDevice(
                modifier = modifier,
            )
        }
        items(
            items = batteryConnectedDevices,
            key = { device -> device.address }
        ) { device ->
            val showInWidget = batteryDeviceSettings.settings[device.address]?.showInWidget ?: true
            BonedDeviceItem(
                device = device,
                showInWidget = showInWidget,
                onShowInWidgetChanged = onShowInWidgetChanged,
                onItemClick = {}
            )
        }
    }
}