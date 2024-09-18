package io.github.ifa.glancewidget

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.github.ifa.glancewidget.background.cancelBatteryMonitorRequest
import io.github.ifa.glancewidget.background.enqueueBatteryMonitorRequest
import io.github.ifa.glancewidget.broadcast.MonitorReceiver
import io.github.ifa.glancewidget.di.RepositoryProvider
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.BATTERY_ACTIONS
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.BLUETOOTH_STATE_ACTIONS
import io.github.ifa.glancewidget.presentation.main.mainScreenRoute
import io.github.ifa.glancewidget.service.BatteryAlertService
import io.github.ifa.glancewidget.utils.AppPermissions
import io.github.ifa.glancewidget.utils.BluetoothPermissions
import io.github.ifa.glancewidget.utils.NotificationPermissions
import io.github.ifa.glancewidget.utils.checkPermissions
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var repositoryProvider: RepositoryProvider

    @Inject
    lateinit var appSettingsRepository: AppSettingsRepository

    private val monitorReceiver by lazy { MonitorReceiver() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        requestPermissions()
        enableEdgeToEdge()
        if (VERSION.SDK_INT >= VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            repositoryProvider.Provide {
                ConfigApp(startDestination = mainScreenRoute)
            }
        }
    }

    private fun requestPermissions() {
        if (!applicationContext.checkPermissions(AppPermissions)) {
            requestMultiplePermissions.launch(AppPermissions.toTypedArray())
        } else {
            registerReceiver(BATTERY_ACTIONS + BLUETOOTH_STATE_ACTIONS)
            startForegroundService(Intent(this, BatteryAlertService::class.java))
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGrantedBluetooth =
                permissions.entries.filter { it.key in BluetoothPermissions }.all { it.value }
            if (isGrantedBluetooth) {
                registerReceiver(BATTERY_ACTIONS + BLUETOOTH_STATE_ACTIONS)
                saveShowPairedDevicesSetting(showPairedDevices = true)
            } else {
                registerReceiver(BATTERY_ACTIONS)
                saveShowPairedDevicesSetting(showPairedDevices = false)
            }
            val isGrantedNotification =
                permissions.entries.filter { it.key in NotificationPermissions }.all { it.value }
            if (isGrantedNotification) {
                startForegroundService(Intent(this, BatteryAlertService::class.java))
            }
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

    private fun saveShowPairedDevicesSetting(showPairedDevices: Boolean) {
        lifecycleScope.launch {
            appSettingsRepository.saveShowPairedDevicesSetting(showPairedDevices)
        }
    }

    override fun onStart() {
        super.onStart()
        enqueueBatteryMonitorRequest()
    }

    override fun onStop() {
        super.onStop()
        cancelBatteryMonitorRequest()
    }

    override fun onDestroy() {
        unregisterReceiver(monitorReceiver)
        super.onDestroy()
    }
}

