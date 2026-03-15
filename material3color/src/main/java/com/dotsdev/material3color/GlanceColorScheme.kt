package com.dotsdev.material3color

import android.annotation.SuppressLint
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.glance.color.ColorProviders
import androidx.glance.color.colorProviders
import androidx.glance.unit.ColorProvider

fun Color.toDarkGlanceColorProviders(isAmoled: Boolean = false): ColorProviders {
    return getDarkScheme(this.toArgb(), isAmoled).toGlanceColorProviders()
}

fun Color.toLightGlanceColorProviders(): ColorProviders {
    return getLightScheme(this.toArgb()).toGlanceColorProviders()
}

fun Color.toDayNightGlanceColorProviders(isAmoled: Boolean = false): ColorProviders {
    val lightScheme = getLightScheme(this.toArgb())
    val darkScheme = getDarkScheme(this.toArgb(), isAmoled)
    return colorProviders(
        primary = androidx.glance.color.ColorProvider(lightScheme.primary, darkScheme.primary),
        onPrimary = androidx.glance.color.ColorProvider(
            lightScheme.onPrimary,
            darkScheme.onPrimary
        ),
        primaryContainer = androidx.glance.color.ColorProvider(
            lightScheme.primaryContainer,
            darkScheme.primaryContainer
        ),
        onPrimaryContainer = androidx.glance.color.ColorProvider(
            lightScheme.onPrimaryContainer,
            darkScheme.onPrimaryContainer
        ),
        secondary = androidx.glance.color.ColorProvider(
            lightScheme.secondary,
            darkScheme.secondary
        ),
        onSecondary = androidx.glance.color.ColorProvider(
            lightScheme.onSecondary,
            darkScheme.onSecondary
        ),
        secondaryContainer = androidx.glance.color.ColorProvider(
            lightScheme.secondaryContainer,
            darkScheme.secondaryContainer
        ),
        onSecondaryContainer = androidx.glance.color.ColorProvider(
            lightScheme.onSecondaryContainer,
            darkScheme.onSecondaryContainer
        ),
        tertiary = androidx.glance.color.ColorProvider(lightScheme.tertiary, darkScheme.tertiary),
        onTertiary = androidx.glance.color.ColorProvider(
            lightScheme.onTertiary,
            darkScheme.onTertiary
        ),
        tertiaryContainer = androidx.glance.color.ColorProvider(
            lightScheme.tertiaryContainer,
            darkScheme.tertiaryContainer
        ),
        onTertiaryContainer = androidx.glance.color.ColorProvider(
            lightScheme.onTertiaryContainer,
            darkScheme.onTertiaryContainer
        ),
        error = androidx.glance.color.ColorProvider(lightScheme.error, darkScheme.error),
        onError = androidx.glance.color.ColorProvider(lightScheme.onError, darkScheme.onError),
        errorContainer = androidx.glance.color.ColorProvider(
            lightScheme.errorContainer,
            darkScheme.errorContainer
        ),
        onErrorContainer = androidx.glance.color.ColorProvider(
            lightScheme.onErrorContainer,
            darkScheme.onErrorContainer
        ),
        background = androidx.glance.color.ColorProvider(
            lightScheme.background,
            darkScheme.background
        ),
        onBackground = androidx.glance.color.ColorProvider(
            lightScheme.onBackground,
            darkScheme.onBackground
        ),
        surface = androidx.glance.color.ColorProvider(lightScheme.surface, darkScheme.surface),
        onSurface = androidx.glance.color.ColorProvider(
            lightScheme.onSurface,
            darkScheme.onSurface
        ),
        surfaceVariant = androidx.glance.color.ColorProvider(
            lightScheme.surfaceVariant,
            darkScheme.surfaceVariant
        ),
        onSurfaceVariant = androidx.glance.color.ColorProvider(
            lightScheme.onSurfaceVariant,
            darkScheme.onSurfaceVariant
        ),
        outline = androidx.glance.color.ColorProvider(lightScheme.outline, darkScheme.outline),
        inverseOnSurface = androidx.glance.color.ColorProvider(
            lightScheme.inverseOnSurface,
            darkScheme.inverseOnSurface
        ),
        inversePrimary = androidx.glance.color.ColorProvider(
            lightScheme.inversePrimary,
            darkScheme.inversePrimary
        ),
        inverseSurface = androidx.glance.color.ColorProvider(
            lightScheme.inverseSurface,
            darkScheme.inverseSurface
        ),
        widgetBackground = androidx.glance.color.ColorProvider(
            lightScheme.surfaceContainerLow,
            darkScheme.surfaceContainerLow
        )
    )
}

@SuppressLint("RestrictedApi")
fun ColorScheme.toGlanceColorProviders() = colorProviders(
    primary = ColorProvider(primary),
    onPrimary = ColorProvider(onPrimary),
    primaryContainer = ColorProvider(primaryContainer),
    onPrimaryContainer = ColorProvider(onPrimaryContainer),
    secondary = ColorProvider(secondary),
    onSecondary = ColorProvider(onSecondary),
    secondaryContainer = ColorProvider(secondaryContainer),
    onSecondaryContainer = ColorProvider(onSecondaryContainer),
    tertiary = ColorProvider(tertiary),
    onTertiary = ColorProvider(onTertiary),
    tertiaryContainer = ColorProvider(tertiaryContainer),
    onTertiaryContainer = ColorProvider(onTertiaryContainer),
    error = ColorProvider(error),
    onError = ColorProvider(onError),
    errorContainer = ColorProvider(errorContainer),
    onErrorContainer = ColorProvider(onErrorContainer),
    background = ColorProvider(background),
    onBackground = ColorProvider(onBackground),
    surface = ColorProvider(surface),
    onSurface = ColorProvider(onSurface),
    surfaceVariant = ColorProvider(surfaceVariant),
    onSurfaceVariant = ColorProvider(onSurfaceVariant),
    outline = ColorProvider(outline),
    inverseOnSurface = ColorProvider(inverseOnSurface),
    inversePrimary = ColorProvider(inversePrimary),
    inverseSurface = ColorProvider(inverseSurface),
    widgetBackground = ColorProvider(surfaceContainer)
)
