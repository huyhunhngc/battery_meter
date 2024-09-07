package io.github.ifa.glancewidget.model

import kotlinx.serialization.Serializable

@Serializable
data class BonedDevice(
    val name: String = "",
    val address: String = "",
    val batteryInPercentage: Int = 0,
    val batteryInMinutes: Int = 0,
    val deviceType: DeviceType = DeviceType.OTHER
)