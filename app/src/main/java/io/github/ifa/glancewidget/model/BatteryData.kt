package io.github.ifa.glancewidget.model

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import androidx.annotation.StringRes
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.utils.getPairedDevices
import kotlinx.serialization.Serializable
import kotlin.math.round

@Serializable
data class BatteryData(
    val myDevice: MyDevice, val batteryConnectedDevices: List<BonedDevice>
) {
    fun setChargingStatus(isCharging: Boolean): BatteryData {
        return copy(myDevice = myDevice.copy(isCharging = isCharging))
    }

    fun setPairedDevices(context: Context): BatteryData {
        return copy(
            batteryConnectedDevices = context.getPairedDevices()
                .ifEmpty { batteryConnectedDevices })
    }

    companion object {
        fun initial(): BatteryData {
            return BatteryData(
                myDevice = MyDevice.fromIntent(Intent()), batteryConnectedDevices = emptyList()
            )
        }
    }
}

@Serializable
data class MyDevice(
    val name: String = "",
    val iconSmall: Int = -1,
    val action: String = "",
    val health: Int = -1,
    val status: Int = -1,
    val voltage: Float = -1f,
    val temperature: Temperature = Temperature(),
    val technology: String = "",
    val level: Int = 0,
    val scale: Int = -1,
    val present: Boolean = false,
    val batteryLow: Boolean = false,
    val plugged: Int = -1,
    val isCharging: Boolean = false,
    val deviceType: DeviceType = DeviceType.PHONE,
    val cycleCount: Int = 0,
    val chargeType: ChargeType = ChargeType.NONE,
) {
    enum class ChargeType(val type: String) {
        AC("AC"), USB("USB"), WIRELESS("Wireless"), NONE("");
    }

    @Serializable
    data class Temperature(
        val temperature: Float = -1f, val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS
    ) {
        enum class TemperatureUnit(val unit: String) {
            CELSIUS("°C"), FAHRENHEIT("°F")
        }

        fun formatTemperature(): String = "${round(temperature)} ${temperatureUnit.unit}"
    }

    fun getBatteryHealth(extraBatteryInfo: ExtraBatteryInfo): BatteryHealth {
        val lossCapacityInPercent =
            100.0 - (extraBatteryInfo.fullChargeCapacity.toFloat() / extraBatteryInfo.capacity.toFloat()) * 100.0
        return when {
            health == BatteryManager.BATTERY_HEALTH_OVERHEAT -> BatteryHealth.OVERHEAT
            health == BatteryManager.BATTERY_HEALTH_DEAD -> BatteryHealth.REQUIRE_REPLACEMENT
            health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> BatteryHealth.OVER_VOLTAGE
            health == BatteryManager.BATTERY_HEALTH_COLD -> BatteryHealth.COLD
            lossCapacityInPercent in 0.0..9.0 -> BatteryHealth.VERY_GOOD
            lossCapacityInPercent in 10.0..25.0 -> BatteryHealth.GOOD
            lossCapacityInPercent in 26.0..59.0 -> BatteryHealth.BAD
            else -> BatteryHealth.UNKNOWN
        }
    }

    enum class BatteryHealth(@StringRes val type: Int) {
        UNKNOWN(R.string.unknown_status), VERY_GOOD(R.string.very_good_status), GOOD(R.string.good_status), BAD(
            R.string.bad_status
        ),
        REQUIRE_REPLACEMENT(R.string.require_replacement_status), OVERHEAT(R.string.overheat_status), OVER_VOLTAGE(
            R.string.over_voltage_status
        ),
        COLD(R.string.cold_status)
    }

    companion object {
        fun fromIntent(intent: Intent): MyDevice {
            val batteryLow = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                intent.getBooleanExtra(BatteryManager.EXTRA_BATTERY_LOW, false)
            } else {
                false
            }
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

            val cycleCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                intent.getIntExtra(BatteryManager.EXTRA_CYCLE_COUNT, -1)
            } else {
                0
            }
            val isCharging =
                status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            val chargePlug: Int = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val chargeType = when (chargePlug) {
                BatteryManager.BATTERY_PLUGGED_AC -> ChargeType.AC
                BatteryManager.BATTERY_PLUGGED_USB -> ChargeType.USB
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> ChargeType.WIRELESS
                else -> ChargeType.NONE
            }
            return MyDevice(
                name = "${Build.MANUFACTURER} ${Build.MODEL}",
                iconSmall = intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, -1),
                action = intent.action.toString(),
                health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1),
                status = status,
                voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) / 1000.0f,
                temperature = Temperature(getTemperatureInCelsius(intent)),
                level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0),
                scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1),
                present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false),
                technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY).orEmpty(),
                plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1),
                batteryLow = batteryLow,
                isCharging = isCharging,
                cycleCount = cycleCount,
                chargeType = chargeType
            )
        }

        private fun getTemperatureInCelsius(intent: Intent): Float {
            val temperatureInCelsius = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
            return (temperatureInCelsius / 10.0f)
        }

        fun getTemperatureInFahrenheit(temperatureInCelsius: Double) =
            (temperatureInCelsius * 1.8) + 32.0
    }
}
