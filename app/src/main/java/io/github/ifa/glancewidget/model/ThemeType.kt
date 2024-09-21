package io.github.ifa.glancewidget.model

import io.github.ifa.glancewidget.utils.isSupportedDynamicColor

enum class ThemeType {
    FOLLOW_SYSTEM,
    DARK_THEME,
    LIGHT_THEME
}

enum class ThemeTypeColor(val code: Int) {
    System(0xFF004418.toInt()),
    Spotify(0xFF004418.toInt()),
    FlowerBlue(0xFF769CDF.toInt()),
    MidnightPurple(0xFF220050.toInt()),
    FireRed(0xFFB33B15.toInt()),
    OliverGreen(0xFF63A002.toInt()),
    MintyOrange(0xFFF8B23E.toInt());

    companion object {
        fun entries(): List<ThemeTypeColor> {
            return if (isSupportedDynamicColor()) entries else entries.filter { it != System }
        }
    }
}