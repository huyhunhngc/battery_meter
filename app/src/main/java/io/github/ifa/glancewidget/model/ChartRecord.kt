package io.github.ifa.glancewidget.model

import kotlinx.serialization.Serializable

@Serializable
data class ChartRecord(
    val temperatures: List<Float> = listOf(),
    val voltages: List<Float> = listOf(),
)
