package io.github.ifa.glancewidget.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.utils.buildUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: AppSettingsRepository
) : ViewModel() {
    data class SettingsScreenUiState(
        val theme: ThemeType = ThemeType.FOLLOW_SYSTEM,
        val language: AppSettings.Language? = null,
        val notificationSetting: AppSettings.NotificationSetting = AppSettings.NotificationSetting(),
        val newNotificationSetting: AppSettings.NotificationSetting? = null
    ) {
        val showApplyButton =
            newNotificationSetting != null && newNotificationSetting != notificationSetting
    }

    private val _settings = settingsRepository.get().stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        AppSettings()
    )
    private val _newNotificationSettings = MutableStateFlow<AppSettings.NotificationSetting?>(null)
    val uiState: StateFlow<SettingsScreenUiState> =
        buildUiState(_settings, _newNotificationSettings) { settings, newNotificationSetting ->
            SettingsScreenUiState(
                theme = settings.theme,
                language = settings.language,
                notificationSetting = settings.notificationSetting,
                newNotificationSetting = newNotificationSetting,
            )
        }

    fun setThemeType(themeType: ThemeType) {
        viewModelScope.launch {
            settingsRepository.saveTheme(themeType)
        }
    }

    fun setLanguage(language: AppSettings.Language) {
        viewModelScope.launch {
            settingsRepository.saveLocaleLanguage(language)
        }
    }

    fun onBatteryAlertChanged(checked: Boolean) {
        _newNotificationSettings.update { settings ->
            (settings ?: _settings.value.notificationSetting).copy(batteryAlert = checked)
        }
    }

    fun onShowPairedDeviceChanged(checked: Boolean) {
        _newNotificationSettings.update { settings ->
            (settings ?: _settings.value.notificationSetting).copy(showPairedDevices = checked)
        }
    }

    fun onApplySettings(accept: Boolean) {
        viewModelScope.launch {
            if (accept) {
                _newNotificationSettings.value?.let {
                    settingsRepository.saveNotificationSetting(it)
                }
            }
            _newNotificationSettings.value = null
        }
    }
}