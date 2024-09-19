package io.github.ifa.glancewidget.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.utils.buildUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository
): ViewModel() {
    data class MainScreenUiState(
        val shouldStartNotification: Boolean
    )

    private val _appSetting = appSettingsRepository.get().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )
    val uiState: StateFlow<MainScreenUiState> = buildUiState(_appSetting) { appSettings ->
        MainScreenUiState(
            shouldStartNotification = appSettings.notificationSetting.batteryAlert
        )
    }
}