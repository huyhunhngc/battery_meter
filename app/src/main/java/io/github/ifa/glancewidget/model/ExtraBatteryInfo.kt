package io.github.ifa.glancewidget.model

import kotlinx.serialization.Serializable

@Serializable
data class ExtraBatteryInfo(
    val capacity: Long = -1,
    val chargeCounter: Long = -1,
    val fullChargeCapacity: Long = -1,
)
