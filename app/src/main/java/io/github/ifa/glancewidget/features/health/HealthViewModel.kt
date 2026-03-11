package io.github.ifa.glancewidget.features.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ifa.glancewidget.domain.BatteryUseCase
import io.github.ifa.glancewidget.model.wrapper.BatteryDataWrapper
import io.github.ifa.glancewidget.utils.buildUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject constructor(
    batteryUseCase: BatteryUseCase,
) : ViewModel() {
    data class HealthScreenUiState(
        val batteryOverall: BatteryDataWrapper,
    )

    private val _batteryDataWrapper = batteryUseCase.getBatteryWrapper().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BatteryDataWrapper()
    )

    val uiState: StateFlow<HealthScreenUiState> = buildUiState(
        _batteryDataWrapper,
    ) { batteryDataWrapper ->
        HealthScreenUiState(
            batteryOverall = batteryDataWrapper,
        )
    }
}