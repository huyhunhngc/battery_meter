package io.github.ifa.glancewidget.glance.battery

import android.appwidget.AppWidgetManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.SizeF
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import dagger.hilt.android.AndroidEntryPoint
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.BatteryDevice
import io.github.ifa.glancewidget.utils.getPairedDevices
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BatteryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget get() = BatteryWidget()


    private val lock = Object()
    private var batteryData: BatteryData? = null
        set(value) {
            synchronized(lock) {
                field = value
            }
        }

    private val monitorBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                in BATTERY_ACTIONS -> {
                    val devices = context.getPairedDevices()
                    batteryData = BatteryData(
                        batteryDevice = BatteryDevice.fromIntent(intent),
                        batteryConnectedDevices = devices
                    )
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
    }

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val filter = IntentFilter().apply {
            (BATTERY_ACTIONS + BLUETOOTH_STATE_ACTIONS).forEach {
                addAction(it)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.applicationContext.registerReceiver(
                monitorBroadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            context.applicationContext.registerReceiver(monitorBroadcastReceiver, filter)
        }
    }

    private fun observeData(context: Context) {
        MainScope().launch {
            val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(BatteryWidget::class.java)
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(
                    context = context,
                    glanceId = glanceId,
                ) { _ ->
                    (glanceAppWidget as? BatteryWidget)?.updateIfBatteryChanged(
                        context, glanceId, batteryData
                    ) ?: run {
                        glanceAppWidget.update(context, glanceId)
                    }
                }
            }
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        val sizes = newOptions.getParcelableArrayList<SizeF>(
            AppWidgetManager.OPTION_APPWIDGET_SIZES
        )
        // Check that the list of sizes is provided by the launcher.
        if (sizes.isNullOrEmpty()) {
            return
        }

    }

    companion object {
        val BATTERY_ACTIONS = listOf(
            Intent.ACTION_BATTERY_CHANGED,
            Intent.ACTION_BATTERY_LOW,
            Intent.ACTION_POWER_DISCONNECTED,
            Intent.ACTION_POWER_CONNECTED,
            Intent.ACTION_BATTERY_OKAY
        )
        val BLUETOOTH_STATE_ACTIONS = listOf(
            BluetoothAdapter.ACTION_STATE_CHANGED,
            BluetoothDevice.ACTION_ACL_DISCONNECTED,
            BluetoothDevice.ACTION_ACL_CONNECTED
        )
    }
}
