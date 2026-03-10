package io.github.ifa.glancewidget.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.provider.Settings
import dagger.hilt.android.AndroidEntryPoint
import io.github.ifa.glancewidget.BuildConfig
import io.github.ifa.glancewidget.broadcast.BatteryAppMonitor
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.BATTERY_ACTIONS
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.BLUETOOTH_STATE_ACTIONS
import io.github.ifa.glancewidget.model.MyDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BatteryStatusService : Service() {
    @Inject
    lateinit var notificationHandler: NotificationHandler
    @Inject
    lateinit var batteryStateRepository: BatteryStateRepository
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val batteryMonitor by lazy { BatteryAppMonitor() }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val myDevice = registerReceiver(BATTERY_ACTIONS + BLUETOOTH_STATE_ACTIONS + BATTERY_STATUS_ACTION)?.let {
            MyDevice.fromIntent(it)
        }
        if (myDevice != null) {
            scope.launch {
                batteryStateRepository.setMyDevice(myDevice)
            }
        }
        val notification = notificationHandler.createStartMonitorNotification(myDevice)
        if (VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(SERVICE_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(SERVICE_ID, notification)
        }

        return START_STICKY
    }

    private fun registerReceiver(actions: List<String>): Intent? {
        val filter = IntentFilter().apply {
            actions.forEach { addAction(it) }
        }
        return if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            registerReceiver(batteryMonitor, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(batteryMonitor, filter)
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // Optionally restart service immediately if killed by some OEMs
        // ContextExt.kt startBatteryStatus uses startForegroundService safely.
    }

    override fun onDestroy() {
        unregisterReceiver(batteryMonitor)
        scope.cancel()
        stopForeground(STOP_FOREGROUND_DETACH)
        super.onDestroy()
    }

    companion object {
        const val SERVICE_ID = 1000
        const val BATTERY_STATUS_ACTION = "battery_status_action"
        fun createOpenBatteryStatusSettingsIntent(context: Context, channelId: String): PendingIntent {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID)
                putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
            }
            return PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}