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
        Circle, Vertical, Horizontal;
        companion object {
            fun fromOrdinal(ordinal: Int): Style {
                return Style.entries.getOrNull(ordinal) ?: Horizontal
            }
        }
    }
    enum class Type {
        Small, Tall, Wide, Large, Default, Square, FullWidex1, Smallest, SmallForCircle;

        fun itemOnSizeForHorizontal(): Int {
            return when (this) {
                Smallest -> 0
                Small -> 1
                FullWidex1, Square -> 2
                Wide -> 3
                Tall, Large -> 4
                Default -> 1
                else -> 1
            }
        }

        fun itemOnSizeForCircle(): Int {
            return when (this) {
                Smallest -> 0
                SmallForCircle -> 1
                Small -> 2
                FullWidex1, Square -> 3
                else -> 2
            }
        }
    }

    fun getType(): Type {
        val ratio = height.toFloat() / width.toFloat()
        return when {
            height < 110 && width < 90 -> Type.Smallest // 1x1
            height < 110 && width > 300 -> Type.FullWidex1 // 4x1
            height < 110 -> Type.Small // 3x1 or 4x1
            height > 300 && width > 300 -> Type.Large // 4x4
            width < height -> Type.Tall
            height in 110..<width -> Type.Wide
            ratio in 0.75..1.25 -> Type.Square
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
