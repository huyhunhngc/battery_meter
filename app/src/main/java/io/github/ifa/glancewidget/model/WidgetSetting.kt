package io.github.ifa.glancewidget.model

import kotlinx.serialization.Serializable

@Serializable
data class WidgetSetting(
    val appWidgetId: Int = -1,
    val isTransparent: Boolean = false,
    val transparency: Float = 1.0f,
    val width: Int = 1,
    val height: Int = 1,
    val style: Style = Style.Horizontal,
) {
    enum class Style {
        Circle, Vertical, Horizontal
    }
    enum class Type {
        Small, Tall, Wide, Large, Default, Square, FullWidex1;

        fun itemOnSizeForHorizontal(): Int {
            return when (this) {
                Small -> 1
                FullWidex1, Square -> 2
                Wide -> 3
                Tall, Large -> 4
                Default -> 1
            }
        }

        fun itemOnSizeForCircle(): Int {
            return when (this) {
                Small -> 2
                FullWidex1, Square -> 3
                else -> 2
            }
        }
    }

    fun getType(): Type {
        val ratio = height.toFloat() / width.toFloat()
        return when {
            height < 110 && width > 300 -> Type.FullWidex1 // 4x1
            height < 110 -> Type.Small // 3x1 or 4x1
            height > 300 && width > 300 -> Type.Large // 4x4
            width < height -> Type.Tall
            height in 110..<width -> Type.Wide
            ratio < 1.2 && ratio > 0.8 -> Type.Square
            else -> Type.Default
        }
    }

}

@Serializable
data class WidgetSettings(
    val settings: Map<Int, WidgetSetting> = emptyMap(),
    val theme: ThemeType = ThemeType.FOLLOW_SYSTEM,
    val themeColor: ThemeTypeColor = ThemeTypeColor.System,
)
