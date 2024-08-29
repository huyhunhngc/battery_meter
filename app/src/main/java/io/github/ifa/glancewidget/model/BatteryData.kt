package io.github.ifa.glancewidget.model

import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import java.util.Date

data class BatteryData(
    val timestamp: Date = Date(),
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
//    val propertyCapacity: Int,
//    val propertyChargeCounter: Int,
//    val propertyCurrentAverage: Int,
//    val propertyCurrentNow: Int,
//    val propertyEnergyCounter: Long,
//    val propertyStatus: Int?,
    val isCharging: Boolean?,
    // Min. API Level 29
//    val chargeTimeRemaining: Long?,
) {
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
            return BatteryData(
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
        }
    }
}
