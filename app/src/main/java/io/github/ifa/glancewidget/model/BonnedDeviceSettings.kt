package io.github.ifa.glancewidget.model

import kotlinx.serialization.Serializable

@Serializable
data class BonnedDeviceSettings(
    val settings: Map<String, BonnedDeviceSetting> = emptyMap(),
) {
    @Serializable
    data class BonnedDeviceSetting(
        val address: String = "",
        val showInWidget: Boolean = true,
    )
}
