package io.github.ifa.glancewidget.presentation.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.PINNED_WIDGET_DEFAULT_ID
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.utils.buildUiState
import io.github.ifa.glancewidget.utils.toHHMMSS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WidgetViewModel @Inject constructor(
    private val batteryStateRepository: BatteryStateRepository,
    private val appSettingsRepository: AppSettingsRepository,
) : ViewModel() {
    data class WidgetScreenUiState(
        val setupWidgetId: Int = INVALID_APPWIDGET_ID,
        val batteryData: BatteryData,
        val extraBatteryInfo: ExtraBatteryInfo,
    ) {
        val batteryHealth = batteryData.myDevice.getBatteryHealth(extraBatteryInfo)
        val remainBatteryTime =
            extraBatteryInfo.getBatteryTimeRemaining(batteryData.myDevice.isCharging).toHHMMSS()
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                batteryStateRepository.saveExtraBatteryInformation()
                delay(1000)
            }
        }
    }

    private val _setupWidgetId = MutableStateFlow(INVALID_APPWIDGET_ID)
    private val _appSettings = appSettingsRepository.get().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )
    private val _extraBatteryInfo = batteryStateRepository.extraBatteryFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExtraBatteryInfo()
    )

    private val _batteryData = batteryStateRepository.batteryFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BatteryData.initial()
    )
    val uiState: StateFlow<WidgetScreenUiState> = buildUiState(
        _setupWidgetId, _batteryData, _extraBatteryInfo, _appSettings
    ) { setupWidgetId, batteryData, extraBatteryInfo, appSettings ->
        val battery = batteryData.copy(
            batteryConnectedDevices = if (appSettings.notificationSetting.showPairedDevices) {
                batteryData.batteryConnectedDevices
            } else {
                emptyList()
            }
        )
        WidgetScreenUiState(setupWidgetId, battery, extraBatteryInfo)
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

    fun saveTransparentSettings(isTransparent: Boolean, appWidgetId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            batteryStateRepository.saveWidgetTransparentSetting(isTransparent, appWidgetId)
        }
    }
}