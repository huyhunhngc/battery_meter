package io.github.ifa.glancewidget.presentation.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import io.github.ifa.glancewidget.domain.BatteryUseCase
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.PINNED_WIDGET_DEFAULT_ID
import io.github.ifa.glancewidget.model.wrapper.BatteryDataWrapper
import io.github.ifa.glancewidget.utils.buildUiState
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
    batteryUseCase: BatteryUseCase,
) : ViewModel() {
    data class WidgetScreenUiState(
        val setupWidgetId: Int = INVALID_APPWIDGET_ID,
        val batteryOverall: BatteryDataWrapper
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                batteryStateRepository.saveExtraBatteryInformation()
                delay(1000)
            }
        }
    }

    private val _setupWidgetId = MutableStateFlow(INVALID_APPWIDGET_ID)
    private val _batteryDataWrapper = batteryUseCase.getBatteryWrapper().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BatteryDataWrapper()
    )

    val uiState: StateFlow<WidgetScreenUiState> = buildUiState(
        _setupWidgetId, _batteryDataWrapper
    ) { setupWidgetId, batteryDataWrapper ->
        WidgetScreenUiState(setupWidgetId, batteryDataWrapper)
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