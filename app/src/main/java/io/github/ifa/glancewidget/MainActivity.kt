package io.github.ifa.glancewidget

import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import dagger.hilt.android.AndroidEntryPoint
import io.github.ifa.glancewidget.di.RepositoryProvider
import io.github.ifa.glancewidget.glance.MonitorReceiver
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.BATTERY_ACTIONS
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.BLUETOOTH_STATE_ACTIONS
import io.github.ifa.glancewidget.presentation.main.mainScreenRoute
import io.github.ifa.glancewidget.ui.theme.GlanceWidgetTheme
import io.github.ifa.glancewidget.utils.BluetoothPermissions
import io.github.ifa.glancewidget.utils.checkPermissions
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var repositoryProvider: RepositoryProvider

    private val monitorReceiver by lazy { MonitorReceiver() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestBluetooth()
        enableEdgeToEdge()
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
            registerReceiver()
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
            registerReceiver()
        }

    private fun registerReceiver() {
        val filter = IntentFilter().apply {
            (BATTERY_ACTIONS + BLUETOOTH_STATE_ACTIONS).forEach { addAction(it) }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(monitorReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(monitorReceiver, filter)
        }
    }

    override fun onDestroy() {
        unregisterReceiver(monitorReceiver)
        super.onDestroy()
    }
}

