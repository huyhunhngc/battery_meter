package io.github.ifa.glancewidget

import android.content.IntentFilter
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import io.github.ifa.glancewidget.broadcast.BatteryAppMonitor
import io.github.ifa.glancewidget.di.RepositoryProvider
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.BATTERY_ACTIONS
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.BLUETOOTH_STATE_ACTIONS
import io.github.ifa.glancewidget.features.main.mainScreenRoute
import io.github.ifa.glancewidget.utils.AppPermissions
import io.github.ifa.glancewidget.utils.BluetoothPermissions
import io.github.ifa.glancewidget.utils.checkPermissions
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var repositoryProvider: RepositoryProvider

    private val batteryAppMonitor by lazy { BatteryAppMonitor() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        if (VERSION.SDK_INT >= VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            repositoryProvider.Provide {
                BatteryApp(startDestination = mainScreenRoute)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requestPermissions()
        viewModel.startBatteryMonitoring()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopBatteryMonitoring()
        unregisterReceiver(batteryAppMonitor)
    }

    private fun requestPermissions() {
        if (!applicationContext.checkPermissions(AppPermissions)) {
            requestMultiplePermissions.launch(AppPermissions.toTypedArray())
        } else {
            registerMonitorReceiver(BATTERY_ACTIONS + BLUETOOTH_STATE_ACTIONS)
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGrantedBluetooth =
                permissions.entries.filter { it.key in BluetoothPermissions }.all { it.value }
            if (isGrantedBluetooth) {
                registerMonitorReceiver(BATTERY_ACTIONS + BLUETOOTH_STATE_ACTIONS)
            } else {
                registerMonitorReceiver(BATTERY_ACTIONS)
            }
            viewModel.saveShowPairedDevicesSetting(isGrantedBluetooth)
        }

    private fun registerMonitorReceiver(actions: List<String>) {
        val filter = IntentFilter().apply {
            actions.forEach { addAction(it) }
        }
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            registerReceiver(batteryAppMonitor, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(batteryAppMonitor, filter)
        }
    }
}
