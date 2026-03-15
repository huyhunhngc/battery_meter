package io.github.ifa.glancewidget.glance.battery.utils

import androidx.compose.ui.graphics.Color
import androidx.glance.color.ColorProviders
import io.github.ifa.glancewidget.model.ThemeTypeColor
import io.github.ifa.glancewidget.ui.theme.toDarkGlanceColorProviders
import io.github.ifa.glancewidget.ui.theme.toDayNightGlanceColorProviders
import io.github.ifa.glancewidget.ui.theme.toLightGlanceColorProviders

fun appLightColors(): Map<ThemeTypeColor, ColorProviders> {
    return ThemeTypeColor.entries.filter { it != ThemeTypeColor.System }.associateWith {
        Color(it.code).toLightGlanceColorProviders()
    }
}

fun appDarkColors(isAmoled: Boolean = false): Map<ThemeTypeColor, ColorProviders> {
    return ThemeTypeColor.entries.filter { it != ThemeTypeColor.System }.associateWith {
        Color(it.code).toDarkGlanceColorProviders(isAmoled)
    }
}

fun dayNightColors(isAmoled: Boolean = false): Map<ThemeTypeColor, ColorProviders> {
    return ThemeTypeColor.entries.filter { it != ThemeTypeColor.System }.associateWith {
        Color(it.code).toDayNightGlanceColorProviders(isAmoled)
    }
}