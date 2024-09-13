package io.github.ifa.glancewidget.model

import io.github.ifa.glancewidget.utils.Constants.DEFAULT_MAX_WATTS_CHARGE
import kotlinx.serialization.Serializable
import kotlin.math.abs

@Serializable
data class ExtraBatteryInfo(
    val capacity: Int = -1,
    val chargeCounter: Int = -1,
    val fullChargeCapacity: Int = -1,
    val chargingTimeRemaining: Long = -1,
    val chargeCurrent: Int = -1,
    val maxWattsChargeInput: Float = DEFAULT_MAX_WATTS_CHARGE
) {
    fun getBatteryTimeRemaining(isCharging: Boolean): Long {
        val chargeDischargeCurrent = getChargeDisChargeCurrent(isCharging)
        return ((chargeCounter.toFloat() / (chargeDischargeCurrent.toFloat()/-1)) * 3600000f).toLong()
            .coerceAtLeast(0)
    }

    fun powerInWatt(voltage: Float, isCharging: Boolean = false): Double {
        return if (isCharging) abs(chargeCurrent.toDouble() / 1000.0 * voltage.toDouble()) else 0.0
    }

    fun getChargeDisChargeCurrent(isCharging: Boolean) =
        abs(chargeCurrent) * (if (isCharging) 1 else -1)
}
