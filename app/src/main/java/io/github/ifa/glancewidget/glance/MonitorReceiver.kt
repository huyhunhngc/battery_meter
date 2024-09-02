package io.github.ifa.glancewidget.glance

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import io.github.ifa.glancewidget.glance.battery.BatteryWidget
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.BLUETOOTH_STATE_ACTIONS
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.utils.getPairedDevices
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MonitorReceiver: BroadcastReceiver() {
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
                    myDevice = MyDevice.fromIntent(intent),
                    batteryConnectedDevices = devices
                )
            }
            Intent.ACTION_POWER_CONNECTED -> {
                batteryData = batteryData?.run {
                    copy(myDevice = myDevice.copy(isCharging = true))
                }
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                batteryData = batteryData?.run {
                    copy(myDevice = myDevice.copy(isCharging = true))
                }
            }
            in BLUETOOTH_STATE_ACTIONS -> {
                val devices = context.getPairedDevices()
                batteryData = batteryData?.copy(
                    batteryConnectedDevices = devices
                )
            }
        }
        observeData(context)
    }

    private fun observeData(context: Context) {
        MainScope().launch {
            val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(BatteryWidget::class.java)
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(
                    context = context,
                    glanceId = glanceId,
                ) { _ ->
                    BatteryWidget().updateIfBatteryChanged(
                        context, glanceId, batteryData
                    )
                }
            }
        }
    }
}