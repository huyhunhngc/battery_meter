package io.github.ifa.glancewidget.data.battery


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import io.github.ifa.glancewidget.model.BatteryData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart

class DefaultBatteryStateRepository(
    private val batteryDataStore: BatteryDataStore
) : BatteryStateRepository {
    private val _battery = MutableStateFlow(BatteryData.initial())
    @Composable
    override fun battery(): BatteryData {
        val result by remember {
            batteryDataStore.get()
        }.collectAsState(BatteryData.initial())
        return result
    }

    override fun batteryFlow(): Flow<BatteryData> {
        return batteryDataStore.get()
    }
}