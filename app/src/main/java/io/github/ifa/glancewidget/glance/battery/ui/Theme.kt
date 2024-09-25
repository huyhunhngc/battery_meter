package io.github.ifa.glancewidget.glance.battery.ui

import androidx.compose.runtime.Composable
import androidx.glance.GlanceComposable
import androidx.glance.GlanceTheme
import androidx.glance.color.DynamicThemeColorProviders
import io.github.ifa.glancewidget.glance.battery.utils.appDarkColors
import io.github.ifa.glancewidget.glance.battery.utils.appLightColors
import io.github.ifa.glancewidget.glance.battery.utils.dayNightColors
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.model.ThemeTypeColor

@Composable
fun PixelBatteryTheme(
    themeTypeColor: ThemeTypeColor = ThemeTypeColor.System,
    themeType: ThemeType = ThemeType.FOLLOW_SYSTEM,
    content: @GlanceComposable @Composable () -> Unit
) {
    val appColors = when (themeType) {
        ThemeType.LIGHT_THEME -> appLightColors()
        ThemeType.DARK_THEME -> appDarkColors()
        ThemeType.FOLLOW_SYSTEM -> dayNightColors()
    }
    val colors = appColors[themeTypeColor] ?: DynamicThemeColorProviders
    GlanceTheme(colors = colors, content = content)
}
