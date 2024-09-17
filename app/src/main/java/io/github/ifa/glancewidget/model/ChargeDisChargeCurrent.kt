package io.github.ifa.glancewidget.model

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
