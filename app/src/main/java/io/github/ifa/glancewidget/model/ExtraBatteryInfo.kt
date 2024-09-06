package io.github.ifa.glancewidget.model

import kotlinx.serialization.Serializable

@Serializable
data class ExtraBatteryInfo(
    val capacity: Int = -1,
    val chargeCounter: Int = -1,
    val fullChargeCapacity: Int = -1,
)
