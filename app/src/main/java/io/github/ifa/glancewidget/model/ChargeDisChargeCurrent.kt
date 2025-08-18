package io.github.ifa.glancewidget.model

import io.github.ifa.glancewidget.utils.Constants.DEFAULT_MAX_COLLECT_CURRENT
import kotlinx.serialization.Serializable

@Serializable
data class ChargeDisChargeCurrent(
    val chargeCurrents: List<Int> = listOf(-1),
    val dischargeCurrents: List<Int> = listOf(-1),
    val fastChargeCurrents: List<Int> = listOf(-1),
    val turboChargeCurrents: List<Int> = listOf(-1)
) {
    fun getChargeCurrent(current: Int): Int {
        val chargeCurrent = when (getChargeSpeed(current)) {
            ChargeSpeed.TURBO -> turboChargeCurrents.average().toInt()
            ChargeSpeed.FAST -> fastChargeCurrents.average().toInt()
            ChargeSpeed.NORMAL -> chargeCurrents.average().toInt()
            ChargeSpeed.DISCHARGING -> dischargeCurrents.average().toInt()
        }
        return if (chargeCurrent == -1) current else chargeCurrent
    }

    fun showMeasurementWarning(): Boolean {
        val collectionWarning = DEFAULT_MAX_COLLECT_CURRENT / 2
        if (chargeCurrents.size != 1 && chargeCurrents.size < collectionWarning) return true
        return dischargeCurrents.size != 1 && dischargeCurrents.size < collectionWarning
    }

    fun getMeasurementProgress(): Float {
        val totalMeasurements = chargeCurrents.size + dischargeCurrents.size
        if (totalMeasurements == 0) return 0f
        val currentMeasurements =
            chargeCurrents.count { it != -1 } + dischargeCurrents.count { it != -1 }
        val progress = (currentMeasurements.toFloat() / DEFAULT_MAX_COLLECT_CURRENT) * 100f
        return progress
    }

    enum class ChargeSpeed {
        NORMAL, FAST, TURBO, DISCHARGING
    }

    companion object {
        fun getChargeSpeed(current: Int): ChargeSpeed {
            return when {
                current > 4000 -> ChargeSpeed.TURBO
                current in 2000..4000 -> ChargeSpeed.FAST
                current in 0..2000 -> ChargeSpeed.NORMAL
                else -> ChargeSpeed.DISCHARGING
            }
        }
    }
}
