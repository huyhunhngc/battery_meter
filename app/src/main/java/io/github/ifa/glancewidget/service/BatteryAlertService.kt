package io.github.ifa.glancewidget.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import io.github.ifa.glancewidget.broadcast.MonitorReceiver
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import io.github.ifa.glancewidget.domain.BatteryUseCase
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.BATTERY_ACTIONS
import io.github.ifa.glancewidget.model.MyDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BatteryAlertService : Service() {
    @Inject
    lateinit var batteryUseCase: BatteryUseCase

    private val notificationHandler: NotificationHandler by lazy {
        NotificationHandler(this)
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val monitorReceiver by lazy { MonitorReceiver() }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            SERVICE_ID,
            notificationHandler.createStartMonitorNotification()
        )
        registerReceiver(BATTERY_ACTIONS)
        scope.launch {
            batteryUseCase.getBatteryWrapper().collect {
                notificationHandler.createBatteryMonitorNotification(it)
            }
        }

        return START_STICKY
    }

    private fun registerReceiver(actions: List<String>) {
        val filter = IntentFilter().apply {
            actions.forEach { addAction(it) }
        }
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            registerReceiver(monitorReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(monitorReceiver, filter)
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.d("!@#", "onDestroy: onDestroy")
        //scope.cancel()
        super.onDestroy()
    }

    companion object {
        const val SERVICE_ID = 1234
    }
}