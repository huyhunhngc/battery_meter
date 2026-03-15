package io.github.ifa.glancewidget.model

import com.dotsdev.material3color.isSupportedDynamicColor

enum class ThemeType {
    FOLLOW_SYSTEM,
    DARK_THEME,
    LIGHT_THEME
}

enum class ThemeTypeColor(val code: Int) {
    System(0xFF386239.toInt()),
    Spotify(0xFF20665D.toInt()),
    FlowerBlue(0xFF1E6470.toInt()),
    MidnightPurple(0xFF764F77.toInt()),
    FireRed(0xFF854B56.toInt()),
    Brown(0xFF965141.toInt()),
    AutumnYellow(0xFF71581B.toInt()),
    OliverGreen(0xFF3F653C.toInt()),
    MintyOrange(0xFFFF9800.toInt());

    val next: ThemeTypeColor
        get() {
            val index = ThemeTypeColor.entries.indexOf(this)
            val nextIndex = (index + 1) % ThemeTypeColor.entries.size
            return ThemeTypeColor.entries[nextIndex]
        }

    companion object {
        fun entries(): List<ThemeTypeColor> {
            return if (isSupportedDynamicColor()) entries else entries.filter { it != System }
        }
    }
}