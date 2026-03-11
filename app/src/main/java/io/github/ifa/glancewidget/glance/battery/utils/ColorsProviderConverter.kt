package io.github.ifa.glancewidget.glance.battery.utils

import android.annotation.SuppressLint
import androidx.glance.color.ColorProvider
import androidx.glance.color.ColorProviders
import androidx.glance.color.colorProviders
import androidx.glance.unit.ColorProvider
import io.github.ifa.glancewidget.model.ThemeTypeColor
import io.github.ifa.glancewidget.ui.theme.getDarkScheme
import io.github.ifa.glancewidget.ui.theme.getLightScheme

@SuppressLint("RestrictedApi")
fun appLightColors(): Map<ThemeTypeColor, ColorProviders> {
    return ThemeTypeColor.entries.filter { it != ThemeTypeColor.System }.associateWith {
        val colorScheme = getLightScheme(it.code)
        colorProviders(
            primary = ColorProvider(colorScheme.primary),
            onPrimary = ColorProvider(colorScheme.onPrimary),
            primaryContainer = ColorProvider(colorScheme.primaryContainer),
            onPrimaryContainer = ColorProvider(colorScheme.onPrimaryContainer),
            secondary = ColorProvider(colorScheme.secondary),
            onSecondary = ColorProvider(colorScheme.onSecondary),
            secondaryContainer = ColorProvider(colorScheme.secondaryContainer),
            onSecondaryContainer = ColorProvider(colorScheme.onSecondaryContainer),
            tertiary = ColorProvider(colorScheme.tertiary),
            onTertiary = ColorProvider(colorScheme.onTertiary),
            tertiaryContainer = ColorProvider(colorScheme.tertiaryContainer),
            onTertiaryContainer = ColorProvider(colorScheme.onTertiaryContainer),
            error = ColorProvider(colorScheme.error),
            onError = ColorProvider(colorScheme.onError),
            errorContainer = ColorProvider(colorScheme.errorContainer),
            onErrorContainer = ColorProvider(colorScheme.onErrorContainer),
            background = ColorProvider(colorScheme.background),
            onBackground = ColorProvider(colorScheme.onBackground),
            surface = ColorProvider(colorScheme.surface),
            onSurface = ColorProvider(colorScheme.onSurface),
            surfaceVariant = ColorProvider(colorScheme.surfaceVariant),
            onSurfaceVariant = ColorProvider(colorScheme.onSurfaceVariant),
            outline = ColorProvider(colorScheme.outline),
            inverseOnSurface = ColorProvider(colorScheme.inverseOnSurface),
            inversePrimary = ColorProvider(colorScheme.inversePrimary),
            inverseSurface = ColorProvider(colorScheme.inverseSurface),
            widgetBackground = ColorProvider(colorScheme.surfaceContainerLow)
        )
    }
}

@SuppressLint("RestrictedApi")
fun appDarkColors(isAmoled: Boolean = false): Map<ThemeTypeColor, ColorProviders> {
    return ThemeTypeColor.entries.filter { it != ThemeTypeColor.System }.associateWith {
        val colorScheme = getDarkScheme(it.code, isAmoled)
        colorProviders(
            primary = ColorProvider(colorScheme.primary),
            onPrimary = ColorProvider(colorScheme.onPrimary),
            primaryContainer = ColorProvider(colorScheme.primaryContainer),
            onPrimaryContainer = ColorProvider(colorScheme.onPrimaryContainer),
            secondary = ColorProvider(colorScheme.secondary),
            onSecondary = ColorProvider(colorScheme.onSecondary),
            secondaryContainer = ColorProvider(colorScheme.secondaryContainer),
            onSecondaryContainer = ColorProvider(colorScheme.onSecondaryContainer),
            tertiary = ColorProvider(colorScheme.tertiary),
            onTertiary = ColorProvider(colorScheme.onTertiary),
            tertiaryContainer = ColorProvider(colorScheme.tertiaryContainer),
            onTertiaryContainer = ColorProvider(colorScheme.onTertiaryContainer),
            error = ColorProvider(colorScheme.error),
            onError = ColorProvider(colorScheme.onError),
            errorContainer = ColorProvider(colorScheme.errorContainer),
            onErrorContainer = ColorProvider(colorScheme.onErrorContainer),
            background = ColorProvider(colorScheme.background),
            onBackground = ColorProvider(colorScheme.onBackground),
            surface = ColorProvider(colorScheme.surface),
            onSurface = ColorProvider(colorScheme.onSurface),
            surfaceVariant = ColorProvider(colorScheme.surfaceVariant),
            onSurfaceVariant = ColorProvider(colorScheme.onSurfaceVariant),
            outline = ColorProvider(colorScheme.outline),
            inverseOnSurface = ColorProvider(colorScheme.inverseOnSurface),
            inversePrimary = ColorProvider(colorScheme.inversePrimary),
            inverseSurface = ColorProvider(colorScheme.inverseSurface),
            widgetBackground = ColorProvider(colorScheme.surfaceContainer)
        )
    }
}

fun dayNightColors(isAmoled: Boolean = false): Map<ThemeTypeColor, ColorProviders> {
    return ThemeTypeColor.entries.filter { it != ThemeTypeColor.System }.associateWith {
        val lightScheme = getLightScheme(it.code)
        val darkScheme = getDarkScheme(it.code, isAmoled)
        colorProviders(
            primary = ColorProvider(lightScheme.primary, darkScheme.primary),
            onPrimary = ColorProvider(lightScheme.onPrimary, darkScheme.onPrimary),
            primaryContainer = ColorProvider(
                lightScheme.primaryContainer,
                darkScheme.primaryContainer
            ),
            onPrimaryContainer = ColorProvider(
                lightScheme.onPrimaryContainer,
                darkScheme.onPrimaryContainer
            ),
            secondary = ColorProvider(lightScheme.secondary, darkScheme.secondary),
            onSecondary = ColorProvider(lightScheme.onSecondary, darkScheme.onSecondary),
            secondaryContainer = ColorProvider(
                lightScheme.secondaryContainer,
                darkScheme.secondaryContainer
            ),
            onSecondaryContainer = ColorProvider(
                lightScheme.onSecondaryContainer,
                darkScheme.onSecondaryContainer
            ),
            tertiary = ColorProvider(lightScheme.tertiary, darkScheme.tertiary),
            onTertiary = ColorProvider(lightScheme.onTertiary, darkScheme.onTertiary),
            tertiaryContainer = ColorProvider(
                lightScheme.tertiaryContainer,
                darkScheme.tertiaryContainer
            ),
            onTertiaryContainer = ColorProvider(
                lightScheme.onTertiaryContainer,
                darkScheme.onTertiaryContainer
            ),
            error = ColorProvider(lightScheme.error, darkScheme.error),
            onError = ColorProvider(lightScheme.onError, darkScheme.onError),
            errorContainer = ColorProvider(lightScheme.errorContainer, darkScheme.errorContainer),
            onErrorContainer = ColorProvider(
                lightScheme.onErrorContainer,
                darkScheme.onErrorContainer
            ),
            background = ColorProvider(lightScheme.background, darkScheme.background),
            onBackground = ColorProvider(lightScheme.onBackground, darkScheme.onBackground),
            surface = ColorProvider(lightScheme.surface, darkScheme.surface),
            onSurface = ColorProvider(lightScheme.onSurface, darkScheme.onSurface),
            surfaceVariant = ColorProvider(lightScheme.surfaceVariant, darkScheme.surfaceVariant),
            onSurfaceVariant = ColorProvider(
                lightScheme.onSurfaceVariant,
                darkScheme.onSurfaceVariant
            ),
            outline = ColorProvider(lightScheme.outline, darkScheme.outline),
            inverseOnSurface = ColorProvider(
                lightScheme.inverseOnSurface,
                darkScheme.inverseOnSurface
            ),
            inversePrimary = ColorProvider(lightScheme.inversePrimary, darkScheme.inversePrimary),
            inverseSurface = ColorProvider(lightScheme.inverseSurface, darkScheme.inverseSurface),
            widgetBackground = ColorProvider(
                lightScheme.surfaceContainerLow,
                darkScheme.surfaceContainerLow
            )
        )
    }
}