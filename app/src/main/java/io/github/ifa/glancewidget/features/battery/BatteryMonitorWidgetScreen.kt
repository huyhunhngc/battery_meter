package io.github.ifa.glancewidget.features.battery

import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Intent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.ScreenLockPortrait
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.MainActivity
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.di.LocalRepositories
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.PINNED_WIDGET_DEFAULT_ID
import io.github.ifa.glancewidget.model.AddWidgetParams
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.BonnedDeviceSettings
import io.github.ifa.glancewidget.model.ChargeDisChargeCurrent
import io.github.ifa.glancewidget.model.ChartRecord
import io.github.ifa.glancewidget.model.DeviceType
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.model.ThemeTypeColor
import io.github.ifa.glancewidget.model.wrapper.BatteryDataWrapper
import io.github.ifa.glancewidget.model.wrapper.PowerDetails
import io.github.ifa.glancewidget.features.battery.component.AddWidgetBottomSheet
import io.github.ifa.glancewidget.features.battery.component.BatteryOverall
import io.github.ifa.glancewidget.features.battery.component.DropdownMenu
import io.github.ifa.glancewidget.features.battery.component.MeasurementWarning
import io.github.ifa.glancewidget.features.battery.wattsmonitor.WattsDetailDestination
import io.github.ifa.glancewidget.ui.component.appPadding
import io.github.ifa.glancewidget.ui.theme.AppTheme
import io.github.ifa.glancewidget.ui.theme.topBarColors
import io.github.ifa.glancewidget.utils.addWidget
import io.github.ifa.glancewidget.utils.findActivity
import io.github.ifa.glancewidget.utils.combinePadding
import io.github.ifa.glancewidget.utils.requestToPinWidget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

const val batteryMonitorScreenRoute = "battery_monitor_screen_route"

fun NavGraphBuilder.batteryMonitorScreen(
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onOpenWattsDetailScreen: (WattsDetailDestination) -> Unit,
    onOpenInquiryScreen: () -> Unit,
) {
    composable(batteryMonitorScreenRoute) {
        BatteryMonitorScreen(
            onOpenWattsDetailScreen = onOpenWattsDetailScreen,
            onOpenInquiryScreen = onOpenInquiryScreen,
            snackbarHostState = snackbarHostState,
            contentPadding = contentPadding,
        )
    }
}

@Composable
internal fun BatteryMonitorScreen(
    viewModel: BatteryMonitorViewModel = hiltViewModel(),
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onOpenWattsDetailScreen: (WattsDetailDestination) -> Unit,
    onOpenInquiryScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showBottomSheet by rememberUpdatedState(uiState.setupWidgetId != INVALID_APPWIDGET_ID)
    val context = LocalContext.current
    val activity = context.findActivity()

    val scope = rememberCoroutineScope()
    val addPinnedWidgetMessage = stringResource(id = R.string.pinned_widget_added)
    LaunchedEffect(Unit) {
        val intent = activity?.intent ?: return@LaunchedEffect
        viewModel.controlExtras(intent)
    }
    BatteryMonitorScreen(
        uiState = uiState,
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
        onForceReloadClick = {
            activity?.apply {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                finish()
                startActivity(intent)
            }
        },
        onOpenInquiryScreen = onOpenInquiryScreen,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BatteryMonitorScreen(
    uiState: BatteryMonitorViewModel.BatteryMonitorScreenUiState,
    contentPadding: PaddingValues,
    isShowAddWidgetBottomSheet: Boolean = false,
    onOpenWattsDetailScreen: (WattsDetailDestination) -> Unit,
    onDisMissBottomSheet: () -> Unit = {},
    onClickAddWidget: (AddWidgetParams) -> Unit,
    onRequestPiningWidget: () -> Unit = {},
    onForceReloadClick: () -> Unit,
    onOpenInquiryScreen: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val haptic = LocalHapticFeedback.current
    Scaffold(
        topBar = {
            Appbar(
                scrollBehavior = scrollBehavior,
                onForceReloadClick = onForceReloadClick,
                onOpenInquiryScreen = onOpenInquiryScreen
            )
        },
        containerColor = topBarColors.containerColor,
    ) { padding ->
        val insets = combinePadding(padding, contentPadding)
        LazyColumn(
            contentPadding = insets,
            modifier = Modifier
                .appPadding()
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            batteryMeasurementWarning(
                showMeasurementWarning = uiState.measurementProgress < 100f,
                progress = uiState.measurementProgress
            )
            batteryOverall(
                batteryDataWrapper = uiState.batteryOverall,
                chartTrackingData = uiState.chartTrackingData,
                temperatureUnit = uiState.temperatureUnit,
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
    onOpenInquiryScreen: () -> Unit,
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
                },
                onOpenInquiryScreen = onOpenInquiryScreen
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
    temperatureUnit: MyDevice.Temperature.TemperatureUnit?,
    onOpenWattsDetailScreen: (WattsDetailDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    item {
        BatteryOverall(
            modifier = modifier,
            batteryDataWrapper = batteryDataWrapper,
            chartTrackingData = chartTrackingData,
            temperatureUnit = temperatureUnit,
            onOpenWattsDetailScreen = onOpenWattsDetailScreen
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BatteryMonitorScreenPreview() {
    val mockAppSettingsRepository = object : AppSettingsRepository {
        override fun get(): Flow<AppSettings> = flowOf(AppSettings())
        override suspend fun getAppSettings(): AppSettings = AppSettings()
        override suspend fun saveLocaleLanguage(language: AppSettings.Language) {}
        override suspend fun saveTheme(themeType: ThemeType) {}
        override suspend fun saveThemeColor(themeTypeColor: ThemeTypeColor) {}
        override suspend fun saveNotificationSetting(notificationSetting: AppSettings.NotificationSetting) {}
        override suspend fun saveShowPairedDevicesSetting(showPairedDevices: Boolean) {}
        override suspend fun saveBondedDeviceSetting(macAddress: String, showInWidget: Boolean) {}
        override suspend fun saveEnableBlackDark(enabled: Boolean) {}
        override suspend fun saveTemperatureUnit(temperatureUnit: MyDevice.Temperature.TemperatureUnit) {}
        override fun getBondedDeviceSettings(): Flow<BonnedDeviceSettings> = flowOf(BonnedDeviceSettings())
    }

    val sampleMyDevice = MyDevice(
        name = "Google Pixel 8",
        level = 85,
        temperature = MyDevice.Temperature(32f),
        voltage = 4.1f,
        isCharging = true,
        chargeType = MyDevice.ChargeType.AC,
        deviceType = DeviceType.PHONE
    )
    val sampleExtraBatteryInfo = ExtraBatteryInfo(
        chargeCurrent = 2500,
        capacity = 5000,
        fullChargeCapacity = 4900,
        chargeCounter = 4200
    )
    val sampleBatteryDataWrapper = BatteryDataWrapper(
        batteryData = BatteryData(myDevice = sampleMyDevice, batteryConnectedDevices = emptyList()),
        extraBatteryInfo = sampleExtraBatteryInfo,
        chargeDisChargeCurrent = ChargeDisChargeCurrent(
            chargeCurrents = List(500) { 2500 },
            dischargeCurrents = List(500) { -500 }
        ),
        powerDetails = PowerDetails(
            power = 10.25f,
            powerPercentage = 0.15f
        )
    )
    val sampleChartRecord = ChartRecord(
        temperatures = listOf(30f, 30.5f, 31f, 31.5f, 32f),
        voltages = listOf(3.9f, 3.95f, 4.0f, 4.05f, 4.1f)
    )

    CompositionLocalProvider(
        LocalRepositories provides mapOf(AppSettingsRepository::class to mockAppSettingsRepository)
    ) {
        AppTheme(appSettingsRepository = mockAppSettingsRepository) {
            BatteryMonitorScreen(
                uiState = BatteryMonitorViewModel.BatteryMonitorScreenUiState(
                    batteryOverall = sampleBatteryDataWrapper,
                    chartTrackingData = sampleChartRecord,
                    temperatureUnit = MyDevice.Temperature.TemperatureUnit.CELSIUS
                ),
                contentPadding = PaddingValues(0.dp),
                onOpenWattsDetailScreen = {},
                onClickAddWidget = {},
                onForceReloadClick = {},
                onOpenInquiryScreen = {}
            )
        }
    }
}
