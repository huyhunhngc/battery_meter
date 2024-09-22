package io.github.ifa.glancewidget.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.model.ThemeTypeColor
import io.github.ifa.glancewidget.utils.buildUiState
import io.github.ifa.glancewidget.utils.isSupportedDynamicColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: AppSettingsRepository
) : ViewModel() {
    data class SettingsScreenUiState(
        val theme: ThemeType = ThemeType.FOLLOW_SYSTEM,
        val themeColor: ThemeTypeColor = ThemeTypeColor.System,
        val language: AppSettings.Language? = null,
        val notificationSetting: AppSettings.NotificationSetting = AppSettings.NotificationSetting(),
    ) {
        val colorScheme = if (!isSupportedDynamicColor() && themeColor == ThemeTypeColor.System) {
            ThemeTypeColor.entries.first()
        } else {
            themeColor
        }
    }

    private val _settings = settingsRepository.get().stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        AppSettings()
    )
    val uiState: StateFlow<SettingsScreenUiState> =
        buildUiState(_settings) { settings ->
            SettingsScreenUiState(
                theme = settings.theme,
                themeColor = settings.themeColor,
                language = settings.language,
                notificationSetting = settings.notificationSetting,
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
}