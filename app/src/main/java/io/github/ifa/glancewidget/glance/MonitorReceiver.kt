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
import io.github.ifa.glancewidget.utils.getPairedDevices
import io.github.ifa.glancewidget.utils.setObject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MonitorReceiver : BroadcastReceiver() {
    @Inject
    lateinit var batteryStateRepository: BatteryStateRepository

    private val lock = Object()
    private var batteryData: BatteryData? = null
        set(value) {
            synchronized(lock) {
                field = value
            }
        }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BATTERY_CHANGED -> {
                val devices = context.getPairedDevices()
                batteryData = BatteryData(
                    myDevice = MyDevice.fromIntent(intent), batteryConnectedDevices = devices
                )
            }

            Intent.ACTION_POWER_CONNECTED -> {
                batteryData = (batteryData ?: BatteryData.initial()).run {
                    copy(myDevice = myDevice.copy(isCharging = true))
                }
                MainScope().launch {
                    batteryStateRepository.saveExtraBatteryInformation()
                }
            }

            Intent.ACTION_POWER_DISCONNECTED -> {
                batteryData = (batteryData ?: BatteryData.initial()).run {
                    copy(myDevice = myDevice.copy(isCharging = true))
                }
            }

            in BLUETOOTH_STATE_ACTIONS -> {
                val devices = context.getPairedDevices()
                batteryData = (batteryData ?: BatteryData.initial()).copy(
                    batteryConnectedDevices = devices
                )
            }
        }
        observeData(context)
    }

    private fun observeData(context: Context) {
        MainScope().launch {
            context.batteryWidgetStore.setObject(BATTERY_PREFERENCES, batteryData)
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
}