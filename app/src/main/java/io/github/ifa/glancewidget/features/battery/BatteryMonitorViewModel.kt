package io.github.ifa.glancewidget.features.battery

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import io.github.ifa.glancewidget.domain.BatteryUseCase
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.PINNED_WIDGET_DEFAULT_ID
import io.github.ifa.glancewidget.model.AddWidgetParams
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.BonnedDeviceSettings
import io.github.ifa.glancewidget.model.ChartRecord
import io.github.ifa.glancewidget.model.wrapper.BatteryDataWrapper
import io.github.ifa.glancewidget.utils.buildUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BatteryMonitorViewModel @Inject constructor(
    private val batteryStateRepository: BatteryStateRepository,
    private val appSettingsRepository: AppSettingsRepository,
    batteryUseCase: BatteryUseCase,
) : ViewModel() {
    init {
        autoTemperature()
    }

    data class BatteryMonitorScreenUiState(
        val setupWidgetId: Int = INVALID_APPWIDGET_ID,
        val batteryOverall: BatteryDataWrapper,
        val chartTrackingData: ChartRecord,
        val temperatureUnit: MyDevice.Temperature.TemperatureUnit? = null,
    ) {
        val measurementProgress = batteryOverall.chargeDisChargeCurrent.getMeasurementProgress()
    }

    private val _setupWidgetId = MutableStateFlow(INVALID_APPWIDGET_ID)
    private val _chartTrackingData = batteryStateRepository.chartRecordFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChartRecord()
    )
    private val _batteryDataWrapper = batteryUseCase.getBatteryWrapper().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BatteryDataWrapper()
    )

    private val _temperatureUnit = appSettingsRepository.get().map { it.temperatureUnit }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        null
    )

    val uiState: StateFlow<BatteryMonitorScreenUiState> = buildUiState(
        _setupWidgetId,
        _batteryDataWrapper,
        _chartTrackingData,
        _temperatureUnit
    ) { setupWidgetId, batteryDataWrapper, chartTrackingData, temperatureUnit ->
        BatteryMonitorScreenUiState(
            setupWidgetId = setupWidgetId,
            batteryOverall = batteryDataWrapper,
            chartTrackingData = chartTrackingData,
            temperatureUnit = temperatureUnit
        )
    }

    fun hideBottomSheet() {
        _setupWidgetId.value = INVALID_APPWIDGET_ID
    }

    fun controlExtras(intent: Intent) {
        val extras = intent.extras
        if (extras != null) {
            val appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID
            )
            _setupWidgetId.value = appWidgetId
        }
    }

    fun createPinnedWidget() {
        _setupWidgetId.value = PINNED_WIDGET_DEFAULT_ID
    }

    fun setWidgetSetting(params: AddWidgetParams, appWidgetId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            batteryStateRepository.saveWidgetInitialSetting(
                appWidgetId = appWidgetId,
                isTransparent = params.isTransparent,
                widgetStyle = params.widgetStyle
            )
        }
    }


    private fun autoTemperature() {
        viewModelScope.launch(Dispatchers.IO) {
            val appSettings = runCatching {
                appSettingsRepository.getAppSettings()
            }.getOrDefault(AppSettings())
            if (appSettings.temperatureUnit == null) {
                val countryCode = Locale.getDefault().country
                val fahrenheitCountries = listOf("US", "BS", "BZ", "KY", "PW", "LR", "FM", "MH")
                val isFahrenheit = fahrenheitCountries.contains(countryCode.uppercase())
                val defaultUnit = if (isFahrenheit) {
                    MyDevice.Temperature.TemperatureUnit.FAHRENHEIT
                } else {
                    MyDevice.Temperature.TemperatureUnit.CELSIUS
                }
                appSettingsRepository.saveTemperatureUnit(defaultUnit)
            }
        }
    }
}