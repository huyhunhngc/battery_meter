package io.github.ifa.glancewidget.presentation.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.utils.buildUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WidgetViewModel @Inject constructor(
    private val batteryStateRepository: BatteryStateRepository
) : ViewModel() {
    data class WidgetScreenUiState(
        val setupWidgetId: Int = INVALID_APPWIDGET_ID,
        val batteryData: BatteryData,
        val extraBatteryInfo: ExtraBatteryInfo,
    ) {
        val batteryHealth = batteryData.myDevice.getBatteryHealth(extraBatteryInfo)
    }

    private val _setupWidgetId = MutableStateFlow(INVALID_APPWIDGET_ID)
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
        _setupWidgetId, _batteryData, _extraBatteryInfo
    ) { setupWidgetId, batteryData, extraBatteryInfo ->
        WidgetScreenUiState(setupWidgetId, batteryData, extraBatteryInfo)
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

    fun saveTransparentSettings(isTransparent: Boolean, appWidgetId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            batteryStateRepository.saveWidgetTransparentSetting(isTransparent, appWidgetId)
        }
    }
}