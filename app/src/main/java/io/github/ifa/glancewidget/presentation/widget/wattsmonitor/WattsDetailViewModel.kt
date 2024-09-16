package io.github.ifa.glancewidget.presentation.widget.wattsmonitor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import io.github.ifa.glancewidget.domain.BatteryUseCase
import io.github.ifa.glancewidget.model.wrapper.PowerDetails
import io.github.ifa.glancewidget.utils.buildUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WattsDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    batteryUseCase: BatteryUseCase
) : ViewModel() {
    data class WattsDetailUiState(
        val details: PowerDetails
    )

    private val route = savedStateHandle.toRoute<WattsDetailDestination>()

    private val _powerDetails = batteryUseCase.getPower().onEach { delay(1000) }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PowerDetails(
            power = route.power,
            powerPercentage = route.powerPercentage
        )
    )

    val uiState: StateFlow<WattsDetailUiState> =
        buildUiState(_powerDetails) { details ->
            WattsDetailUiState(
                details = details
            )
        }
}
