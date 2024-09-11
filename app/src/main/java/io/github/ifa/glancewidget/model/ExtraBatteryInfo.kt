package io.github.ifa.glancewidget.model

import kotlinx.serialization.Serializable
import kotlin.math.abs

@Serializable
data class ExtraBatteryInfo(
    val capacity: Int = -1,
    val chargeCounter: Int = -1,
    val fullChargeCapacity: Int = -1,
    val chargingTimeRemaining: Long = -1,
    val chargeCurrent: Int = -1,
) {
    val batteryTimeRemaining: Long =  if (chargeCurrent < 0.0) {
        ((chargeCounter.toFloat() / (chargeCurrent.toFloat()/-1)) * 3600000f).toLong().coerceAtLeast(0)
    } else 0

    fun powerInWatt(voltage: Float, isCharging: Boolean = false): Double {
        return if (isCharging) abs(chargeCurrent.toDouble()/1000.0 * voltage.toDouble()) else 0.0
    }
}
