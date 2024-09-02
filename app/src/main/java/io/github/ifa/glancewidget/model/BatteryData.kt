package io.github.ifa.glancewidget.model

import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import kotlinx.serialization.Serializable

@Serializable
data class BatteryData(
    val myDevice: MyDevice, val batteryConnectedDevices: List<BonedDevice>
)

@Serializable
data class MyDevice(
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
    val deviceType: DeviceType = DeviceType.PHONE
) {
    companion object {
        fun fromIntent(intent: Intent): MyDevice {
            val batteryLow = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                intent.getBooleanExtra(BatteryManager.EXTRA_BATTERY_LOW, false)
            } else {
                null
            }
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging =
                status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            return MyDevice(
                name = "${Build.MANUFACTURER} ${Build.MODEL}",
                iconSmall = intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, -1),
                action = intent.action.toString(),
                health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1),
                status = status,
                voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1),
                temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1),
                level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0),
                scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1),
                present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false),
                technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY),
                plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1),
                batteryLow = batteryLow,
                isCharging = isCharging
            )
        }
    }

}
