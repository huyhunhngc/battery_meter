package io.github.ifa.glancewidget.model

import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import kotlinx.serialization.Serializable

@Serializable
data class BatteryData(
    val myDevice: MyDevice, val batteryConnectedDevices: List<BonedDevice>
) {
    companion object {
        fun initial(): BatteryData {
            return BatteryData(
                myDevice = MyDevice.fromIntent(Intent()),
                batteryConnectedDevices = emptyList()
            )
        }
    }
}

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
    val deviceType: DeviceType = DeviceType.PHONE,
    val chargeCounter: Int?,
    val cycleCount: Int?,
    val chargeType: ChargeType = ChargeType.NONE
) {
    enum class ChargeType(val type: String) {
        AC("AC"), USB("USB"), NONE("");
    }
    companion object {
        fun fromIntent(intent: Intent): MyDevice {
            val batteryLow = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                intent.getBooleanExtra(BatteryManager.EXTRA_BATTERY_LOW, false)
            } else {
                null
            }
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val chargeCounter = intent.getIntExtra("\"charge_counter\"", -1)
            val cycleCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                intent.getIntExtra(BatteryManager.EXTRA_CYCLE_COUNT, -1)
            } else {
                null
            }
            val isCharging =
                status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            val chargePlug: Int = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val chargeType = when(chargePlug) {
                BatteryManager.BATTERY_PLUGGED_AC -> ChargeType.AC
                BatteryManager.BATTERY_PLUGGED_USB -> ChargeType.USB
                else -> ChargeType.NONE
            }
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
                isCharging = isCharging,
                chargeCounter = chargeCounter,
                cycleCount = cycleCount,
                chargeType = chargeType
            )
        }
    }
}
