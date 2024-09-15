package io.github.ifa.glancewidget

import android.content.Context
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
import io.github.ifa.glancewidget.di.RepositoryProvider
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import io.github.ifa.glancewidget.broadcast.MonitorReceiver
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.BATTERY_ACTIONS
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.BLUETOOTH_STATE_ACTIONS
import io.github.ifa.glancewidget.presentation.main.mainScreenRoute
import io.github.ifa.glancewidget.ui.theme.GlanceWidgetTheme
import io.github.ifa.glancewidget.utils.BluetoothPermissions
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
        requestBluetooth()
        enableEdgeToEdge()
        if (VERSION.SDK_INT >= VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            repositoryProvider.Provide {
                GlanceWidgetTheme {
                    ConfigApp(startDestination = mainScreenRoute)
                }
            }
        }
    }

    private fun requestBluetooth() {
        if (!applicationContext.checkPermissions(BluetoothPermissions)) {
            requestMultiplePermissions.launch(BluetoothPermissions.toTypedArray())
        } else {
            registerReceiver(BATTERY_ACTIONS + BLUETOOTH_STATE_ACTIONS)
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGranted = permissions.entries.all { it.value }
            if (isGranted) {
                registerReceiver(BATTERY_ACTIONS + BLUETOOTH_STATE_ACTIONS)
                saveShowPairedDevicesSetting(showPairedDevices = true)
            } else {
                registerReceiver(BATTERY_ACTIONS)
                saveShowPairedDevicesSetting(showPairedDevices = false)
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

    override fun onDestroy() {
        unregisterReceiver(monitorReceiver)
        super.onDestroy()
    }
}

