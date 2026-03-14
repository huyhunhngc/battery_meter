package io.github.ifa.glancewidget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val batteryStateRepository: BatteryStateRepository
) : ViewModel() {

    private var monitoringJob: Job? = null

    @OptIn(FlowPreview::class)
    fun startBatteryMonitoring() {
        if (monitoringJob?.isActive == true) return

        monitoringJob = viewModelScope.launch {
            launch {
                combine(
                    batteryStateRepository.batteryFlow().map { it.myDevice }.distinctUntilChanged(),
                    batteryStateRepository.extraBatteryFlow().distinctUntilChanged { old, new ->
                        old.chargeCurrent == new.chargeCurrent
                    },
                ) { myDevice, extraBatteryInfo ->
                    Pair(myDevice, extraBatteryInfo)
                }.sample(5000).collect { (myDevice, extraBatteryInfo) ->
                    batteryStateRepository.saveHistoryForChart(
                        myDevice.temperature.temperature, myDevice.voltage
                    )
                    batteryStateRepository.saveChargeCurrent(
                        extraBatteryInfo.getChargeDisChargeCurrent(myDevice.isCharging)
                    )
                }
            }

            launch {
                while (currentCoroutineContext().isActive) {
                    batteryStateRepository.saveExtraBatteryInformation()
                    delay(2000)
                }
            }
        }
    }

    fun stopBatteryMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = null
    }
}