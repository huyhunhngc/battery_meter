package io.github.ifa.glancewidget.model

import kotlinx.serialization.Serializable

@Serializable
data class BonedDevice(
    val name: String?,
    val address: String?,
    val batteryInPercentage: Int,
    val batteryInMinutes: Int,
    val deviceType: DeviceType
)