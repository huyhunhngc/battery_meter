package io.github.ifa.glancewidget.glance.battery

import android.appwidget.AppWidgetManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import dagger.hilt.android.AndroidEntryPoint
import io.github.ifa.glancewidget.broadcast.BatteryWidgetMonitor
import io.github.ifa.glancewidget.data.batteryWidgetStore
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import io.github.ifa.glancewidget.model.AppIntent
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.model.WidgetSetting
import io.github.ifa.glancewidget.utils.getInt
import io.github.ifa.glancewidget.utils.setInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BatteryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget get() = BatteryWidget()

    @Inject
    lateinit var appSettingsRepository: AppSettingsRepository

    @Inject
    lateinit var batteryStateRepository: BatteryStateRepository

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        syncLatestBatteryState(context)
        for (appWidgetId in appWidgetIds) {
            MainScope().launch(Dispatchers.IO) {
                context.batteryWidgetStore.setInt(
                    key = PINNED_WIDGET_PREFERENCES,
                    value = appWidgetId
                )
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "ACTION_PINNED_SUCCESS") {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val deviceName = intent.getStringExtra("EXTRA_DEVICE_ADDRESS")
            MainScope().launch {
                val appWidgetId = context.batteryWidgetStore.getInt(PINNED_WIDGET_PREFERENCES)

            }

        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        val minW = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val minH = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        MainScope().launch {
            (glanceAppWidget as? BatteryWidget)?.updateOnSizeChanged(
                context = context,
                glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId),
                widgetSetting = WidgetSetting(
                    appWidgetId = appWidgetId,
                    width = minW,
                    height = minH
                )
            )
        }
    }

    private fun syncLatestBatteryState(context: Context) {
        val filter = IntentFilter().apply {
            (BATTERY_ACTIONS + BLUETOOTH_STATE_ACTIONS).forEach { addAction(it) }
        }
        val status = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                null, filter, Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            context.registerReceiver(null, filter)
        } ?: Intent()
        val myDevice = MyDevice.fromIntent(status)
        MainScope().launch(Dispatchers.IO) {
            batteryStateRepository.setMyDevice(myDevice)
        }
    }

    companion object {
        val BATTERY_ACTIONS = listOf(
            Intent.ACTION_BATTERY_CHANGED,
            Intent.ACTION_BATTERY_LOW,
            Intent.ACTION_POWER_DISCONNECTED,
            Intent.ACTION_POWER_CONNECTED,
            Intent.ACTION_BATTERY_OKAY,
            AppIntent.ACTION_SHOW_PAIRED_DEVICES_CHANGED,
            AppIntent.ACTION_SYNC_THEME,
            AppIntent.ACTION_SYNC_THEME_COLOR
        )
        val BLUETOOTH_STATE_ACTIONS = listOf(
            BluetoothAdapter.ACTION_STATE_CHANGED,
            BluetoothDevice.ACTION_ACL_DISCONNECTED,
            BluetoothDevice.ACTION_ACL_CONNECTED
        )
        const val PINNED_WIDGET_DEFAULT_ID = -11
        val PINNED_WIDGET_PREFERENCES = intPreferencesKey("request_pinned_widget_id")
    }
}
