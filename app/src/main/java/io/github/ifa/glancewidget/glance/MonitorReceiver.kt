package io.github.ifa.glancewidget.glance

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import dagger.hilt.android.AndroidEntryPoint
import io.github.ifa.glancewidget.data.batteryWidgetStore
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import io.github.ifa.glancewidget.glance.battery.BatteryWidget
import io.github.ifa.glancewidget.glance.battery.BatteryWidget.Companion.BATTERY_PREFERENCES
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.BLUETOOTH_STATE_ACTIONS
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.utils.setObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MonitorReceiver : BroadcastReceiver() {
    @Inject
    lateinit var batteryStateRepository: BatteryStateRepository

    private val lock = Object()
    private var batteryData: BatteryData = BatteryData.initial()
        set(value) {
            synchronized(lock) {
                field = value
            }
        }

    override fun onReceive(context: Context, intent: Intent) {
        val updatedBatteryData = when (intent.action) {
            Intent.ACTION_BATTERY_CHANGED -> {
                batteryData.copy(myDevice = MyDevice.fromIntent(intent)).setPairedDevices(context)
            }

            Intent.ACTION_POWER_CONNECTED -> batteryData.setChargingStatus(true)

            Intent.ACTION_POWER_DISCONNECTED -> batteryData.setChargingStatus(false)

            in BLUETOOTH_STATE_ACTIONS -> batteryData.setPairedDevices(context)

            else -> batteryData
        }
        goAsync(MainScope()) {
            if (updatedBatteryData != batteryData) {
                batteryData = updatedBatteryData
                updateBatteryWidget(context)

            }
        }
    }

    private suspend fun updateBatteryWidget(context: Context) = coroutineScope {
        withContext(Dispatchers.IO) {
            batteryStateRepository.saveExtraBatteryInformation()
            context.batteryWidgetStore.setObject(BATTERY_PREFERENCES, batteryData)
        }
        val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(BatteryWidget::class.java)
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                glanceId = glanceId,
            ) { _ ->
                BatteryWidget().updateIfBatteryChanged(context, glanceId)
            }
        }
    }
}

fun BroadcastReceiver.goAsync(
    coroutineScope: CoroutineScope,
    block: suspend () -> Unit
) {
    val pendingResult = goAsync()
    coroutineScope.launch {
        block()
        pendingResult.finish()
    }
}