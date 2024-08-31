package io.github.ifa.glancewidget.model

import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import com.jaredrummler.android.device.DeviceName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class BatteryData(
    val batteryDevice: BatteryDevice, val batteryConnectedDevice: BluetoothDevice?
) {
    fun updateValue(intent: Intent): BatteryData {
        val batteryData = fromIntent(intent)
        val shouldUpdate = when (intent.action) {
            Intent.ACTION_BATTERY_CHANGED -> batteryDevice.level != batteryData.batteryDevice.level
            else -> true
        }
        return if (shouldUpdate) batteryData else this
    }

    companion object {
        fun fromIntent(intent: Intent): BatteryData {
            val batteryLow = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                intent.getBooleanExtra(BatteryManager.EXTRA_BATTERY_LOW, false)
            } else {
                null
            }
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -99)
            val isCharging =
                status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            val batteryDevice = BatteryDevice(
                name = DeviceName.getDeviceName(),
                iconSmall = intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, -99),
                action = intent.action.toString(),
                health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -99),
                status = status,
                voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -99),
                temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -99),
                level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -99),
                scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -99),
                present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false),
                technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY),
                plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -99),
                batteryLow = batteryLow,
                isCharging = isCharging
            )
            return BatteryData(
                batteryDevice = batteryDevice, batteryConnectedDevice = null
            )
        }
    }
}

@Serializable
data class BatteryDevice(
    val name: String,
    val iconSmall: Int,
    val action: String,
    val health: Int,
    val status: Int,
    val voltage: Int,
    val temperature: Int,
    val technology: String?,
    val level: Int,
    val scale: Int,
    val present: Boolean,
    val batteryLow: Boolean?,
    val plugged: Int,
    val isCharging: Boolean?,
)
