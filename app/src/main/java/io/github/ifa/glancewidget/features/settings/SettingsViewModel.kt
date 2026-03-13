package io.github.ifa.glancewidget.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.BonnedDeviceSettings
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.model.ThemeTypeColor
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.utils.buildUiState
import io.github.ifa.glancewidget.utils.isSupportedDynamicColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: AppSettingsRepository,
    batteryStateRepository: BatteryStateRepository,
) : ViewModel() {
    data class SettingsScreenUiState(
        val theme: ThemeType = ThemeType.FOLLOW_SYSTEM,
        val themeColor: ThemeTypeColor = ThemeTypeColor.System,
        val language: AppSettings.Language? = null,
        val temperatureUnit: MyDevice.Temperature.TemperatureUnit? = null,
        val syncColorEnabled: Boolean = true,
        val isBlackDarkEnabled: Boolean = false,
        val notificationSetting: AppSettings.NotificationSetting = AppSettings.NotificationSetting(),
        val bonedDeviceSettings: BonnedDeviceSettings = BonnedDeviceSettings(),
        val bonedDevices: List<BonedDevice> = emptyList()
    ) {
        val colorScheme = if (!isSupportedDynamicColor() && themeColor == ThemeTypeColor.System) {
            ThemeTypeColor.entries.first()
        } else {
            themeColor
        }
        val batteryConnectedDevices get() = if (notificationSetting.showPairedDevices) {
            bonedDevices
        } else {
            emptyList()
        }
    }

    private val _bonedDeviceSettings = settingsRepository.getBondedDeviceSettings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BonnedDeviceSettings()
    )

    private val _bonedDevices =
        batteryStateRepository.batteryFlow().map { it.batteryConnectedDevices }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _settings = settingsRepository.get().stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        AppSettings()
    )
    val uiState: StateFlow<SettingsScreenUiState> = buildUiState(
        _settings,
        _bonedDeviceSettings,
        _bonedDevices
    ) { settings, bonedDeviceSettings, bonedDevices ->
        SettingsScreenUiState(
            theme = settings.theme,
            themeColor = settings.themeColor,
            language = settings.language,
            temperatureUnit = settings.temperatureUnit,
            syncColorEnabled = settings.syncColorEnabled,
            isBlackDarkEnabled = settings.isBlackDarkEnabled,
            notificationSetting = settings.notificationSetting,
            bonedDeviceSettings = bonedDeviceSettings,
            bonedDevices = bonedDevices
        )
    }

    fun setThemeType(themeType: ThemeType) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.saveTheme(themeType)
        }
    }

    fun setThemeTypeColor(themeTypeColor: ThemeTypeColor) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.saveThemeColor(themeTypeColor)
        }
    }

    fun setLanguage(language: AppSettings.Language) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.saveLocaleLanguage(language)
        }
    }

    fun setTemperatureUnit(temperatureUnit: MyDevice.Temperature.TemperatureUnit) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.saveTemperatureUnit(temperatureUnit)
        }
    }

    fun setEnableBlackDark(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.saveEnableBlackDark(enabled)
        }
    }

    fun onBatteryAlertChanged(checked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.saveNotificationSetting(
                _settings.value.notificationSetting.copy(
                    batteryAlert = checked
                )
            )
        }
    }

    fun onShowPairedDeviceChanged(checked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.saveNotificationSetting(
                _settings.value.notificationSetting.copy(
                    showPairedDevices = checked
                )
            )
        }
    }

    fun updateDeviceShowInWidget(
        address: String, showInWidget: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.saveBondedDeviceSetting(address, showInWidget)
        }
    }
}