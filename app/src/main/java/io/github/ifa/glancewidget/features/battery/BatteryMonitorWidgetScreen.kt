package io.github.ifa.glancewidget.features.battery

import android.Manifest
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Intent
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.ScreenLockPortrait
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.MainActivity
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.features.about.TonalButton
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.PINNED_WIDGET_DEFAULT_ID
import io.github.ifa.glancewidget.model.AddWidgetParams
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.BonnedDeviceSettings
import io.github.ifa.glancewidget.model.ChartRecord
import io.github.ifa.glancewidget.model.wrapper.BatteryDataWrapper
import io.github.ifa.glancewidget.features.battery.component.AddWidgetBottomSheet
import io.github.ifa.glancewidget.features.battery.component.BatteryOverall
import io.github.ifa.glancewidget.features.battery.component.BonedDeviceItem
import io.github.ifa.glancewidget.features.battery.component.ConnectedDevice
import io.github.ifa.glancewidget.features.battery.component.DropdownMenu
import io.github.ifa.glancewidget.features.battery.component.MeasurementWarning
import io.github.ifa.glancewidget.features.battery.component.WidgetSelectionType
import io.github.ifa.glancewidget.features.battery.wattsmonitor.WattsDetailDestination
import io.github.ifa.glancewidget.model.WidgetSetting
import io.github.ifa.glancewidget.ui.component.appPadding
import io.github.ifa.glancewidget.ui.theme.topBarColors
import io.github.ifa.glancewidget.utils.addWidget
import io.github.ifa.glancewidget.utils.findActivity
import io.github.ifa.glancewidget.utils.requestToPinWidget
import kotlinx.coroutines.launch

const val batteryMonitorScreenRoute = "battery_monitor_screen_route"

fun NavGraphBuilder.batteryMonitorScreen(
    contentPadding: PaddingValues,
    onOpenWattsDetailScreen: (WattsDetailDestination) -> Unit
) {
    composable(batteryMonitorScreenRoute) {
        BatteryMonitorScreen(
            onOpenWattsDetailScreen = onOpenWattsDetailScreen,
            contentPadding = contentPadding,
        )
    }
}

@Composable
internal fun BatteryMonitorScreen(
    viewModel: BatteryMonitorViewModel = hiltViewModel(),
    contentPadding: PaddingValues,
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
    BatteryMonitorScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        contentPadding = contentPadding,
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
                viewModel.setWidgetSetting(
                    appWidgetId = uiState.setupWidgetId,
                    params = params
                )
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BatteryMonitorScreen(
    uiState: BatteryMonitorViewModel.BatteryMonitorScreenUiState,
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    isShowAddWidgetBottomSheet: Boolean = false,
    onOpenWattsDetailScreen: (WattsDetailDestination) -> Unit,
    onDisMissBottomSheet: () -> Unit = {},
    onClickAddWidget: (AddWidgetParams) -> Unit,
    onRequestPiningWidget: () -> Unit = {},
    onShowInWidgetChanged: (String, Boolean) -> Unit,
    onForceReloadClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val haptic = LocalHapticFeedback.current
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Appbar(
                scrollBehavior = scrollBehavior,
                onForceReloadClick = onForceReloadClick
            )
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
            batteryMeasurementWarning(
                showMeasurementWarning = uiState.measurementProgress < 100f,
                progress = uiState.measurementProgress
            )
            batteryOverall(
                batteryDataWrapper = uiState.batteryOverall,
                chartTrackingData = uiState.chartTrackingData,
                onOpenWattsDetailScreen = onOpenWattsDetailScreen,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = onRequestPiningWidget,
                        modifier = Modifier.weight(1f).height(80.dp),
                        shape = MaterialTheme.shapes.largeIncreased
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Widgets,
                            contentDescription = stringResource(id = R.string.add_widget)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(id = R.string.add_widget),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    FilledIconToggleButton(
                        onCheckedChange = { checked ->
                            if (checked) haptic.performHapticFeedback(
                                HapticFeedbackType.ToggleOn
                            )
                            else haptic.performHapticFeedback(
                                HapticFeedbackType.ToggleOff
                            )

                        },
                        checked = false,
                        colors = IconButtonDefaults.filledIconToggleButtonColors(
                            containerColor = MaterialTheme.colorScheme.background
                        ),
                        shapes = IconButtonDefaults.toggleableShapes(),
                        modifier = Modifier.weight(1f).height(80.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ScreenLockPortrait,
                                contentDescription = "AOD"
                            )
                            Text(
                                text = "AOD",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }

                    }
                }
            }
            connectedDevices(
                modifier = Modifier.padding(bottom = 16.dp),
                batteryConnectedDevices = uiState.batteryOverall.batteryData.batteryConnectedDevices,
                batteryDeviceSettings = uiState.bonedDeviceSettings,
                onShowInWidgetChanged = onShowInWidgetChanged
            )
        }

        if (isShowAddWidgetBottomSheet) {
            AddWidgetBottomSheet(
                uiState = uiState,
                onDisMiss = onDisMissBottomSheet,
                onClickAddWidget = onClickAddWidget
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Appbar(
    scrollBehavior: TopAppBarScrollBehavior,
    onForceReloadClick: () -> Unit,
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.battery),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        subtitle = {},
        titleHorizontalAlignment = Alignment.CenterHorizontally,
        scrollBehavior = scrollBehavior,
        colors = topBarColors,
        actions = {
            IconButton(onClick = { dropdownExpanded = true }) {
                Icon(
                    Icons.Filled.MoreVert, contentDescription = null
                )
            }
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
                onForceReloadClick = {
                    onForceReloadClick()
                    dropdownExpanded = false
                }
            )
        }
    )
}

private fun LazyListScope.batteryMeasurementWarning(
    showMeasurementWarning: Boolean,
    progress: Float = 0f,
) {
    item {
        AnimatedVisibility(
            visible = showMeasurementWarning,
            enter = slideInVertically(),
        ) {
            MeasurementWarning(
                progress = progress,
            )
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
                onShowInWidgetChanged = onShowInWidgetChanged
            )
        }
    }
}