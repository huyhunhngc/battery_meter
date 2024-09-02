package io.github.ifa.glancewidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.github.ifa.glancewidget.glance.MonitorReceiver
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.BATTERY_ACTIONS
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.BLUETOOTH_STATE_ACTIONS
import io.github.ifa.glancewidget.ui.theme.GlanceWidgetTheme
import io.github.ifa.glancewidget.utils.BluetoothPermissions
import io.github.ifa.glancewidget.utils.checkPermissions

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var mAppWidgetId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestBluetooth()
        registerReceiver()
        controlExtras(intent)
        enableEdgeToEdge()
        setContent {
            GlanceWidgetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Button(
                        onClick = { addNewWidget() }, modifier = Modifier.padding(innerPadding)
                    ) {
                        Text(text = "Add")
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        controlExtras(intent)
    }

    private fun addNewWidget() {
        if (mAppWidgetId == -1) return
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }

    private fun controlExtras(intent: Intent) {
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }
    }

    private fun requestBluetooth() {
        if (!applicationContext.checkPermissions(BluetoothPermissions)) {
            requestMultiplePermissions.launch(BluetoothPermissions.toTypedArray())
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ -> }

    private fun registerReceiver() {
        val filter = IntentFilter().apply {
            (BATTERY_ACTIONS + BLUETOOTH_STATE_ACTIONS).forEach { addAction(it) }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            applicationContext.registerReceiver(
                MonitorReceiver(), filter, Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            applicationContext.registerReceiver(MonitorReceiver(), filter)
        }
    }
}

