package io.github.ifa.glancewidget.model

import io.github.ifa.glancewidget.utils.isSupportedDynamicColor

enum class ThemeType {
    FOLLOW_SYSTEM,
    DARK_THEME,
    LIGHT_THEME
}

enum class ThemeTypeColor(val code: Int) {
    System(0xFF386239.toInt()),
    Spotify(0xFF386239.toInt()),
    FlowerBlue(0xFF769CDF.toInt()),
    MidnightPurple(0xFF220050.toInt()),
    FireRed(0xFFB33B15.toInt()),
    Brown(0xFF965141.toInt()),
    AutumnYellow(0xFFFFDE3F.toInt()),
    OliverGreen(0xFF63A002.toInt()),
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