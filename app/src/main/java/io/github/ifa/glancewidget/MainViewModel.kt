package io.github.ifa.glancewidget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
): ViewModel() {
    fun saveShowPairedDevicesSetting(showPairedDevices: Boolean) {
        viewModelScope.launch {
            appSettingsRepository.saveShowPairedDevicesSetting(showPairedDevices)
        }
    }
}