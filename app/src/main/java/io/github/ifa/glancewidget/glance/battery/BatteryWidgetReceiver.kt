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
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import dagger.hilt.android.AndroidEntryPoint
import io.github.ifa.glancewidget.glance.MonitorReceiver
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.model.WidgetSize
import io.github.ifa.glancewidget.utils.getPairedDevices
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BatteryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget get() = BatteryWidget()

    private lateinit var monitorBroadcastReceiver: MonitorReceiver

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        if (::monitorBroadcastReceiver.isInitialized)  {
            context.applicationContext.unregisterReceiver(monitorBroadcastReceiver)
        }
        monitorBroadcastReceiver = MonitorReceiver()
        val filter = IntentFilter().apply {
            (BATTERY_ACTIONS + BLUETOOTH_STATE_ACTIONS).forEach { addAction(it) }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.applicationContext.registerReceiver(
                monitorBroadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            context.applicationContext.registerReceiver(monitorBroadcastReceiver, filter)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        val size = newOptions.getParcelableArrayList<SizeF>(
            AppWidgetManager.OPTION_APPWIDGET_SIZES
        )?.firstOrNull()
        if (size == null) {
            return
        }
        MainScope().launch {
            (glanceAppWidget as? BatteryWidget)?.updateOnSizeChanged(
                context = context,
                glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId),
                widgetSize = WidgetSize(size.width.toInt(), size.height.toInt())
            )
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
