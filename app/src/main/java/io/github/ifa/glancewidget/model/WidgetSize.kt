package io.github.ifa.glancewidget.model

import kotlinx.serialization.Serializable

@Serializable
data class WidgetSize(
    val width: Int,
    val height: Int
) {
    enum class Type {
        Size2x1, Size2x2, Size2x3, Size3x2, Size3x3, Size3x4, Size4x3, Size4x4
    }
}
