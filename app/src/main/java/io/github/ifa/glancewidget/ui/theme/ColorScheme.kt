package io.github.ifa.glancewidget.ui.theme

import android.annotation.SuppressLint
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.glance.color.ColorProviders
import androidx.glance.color.colorProviders
import androidx.glance.unit.ColorProvider
import com.google.android.material.color.utilities.Hct
import com.google.android.material.color.utilities.MaterialDynamicColors
import com.google.android.material.color.utilities.SchemeTonalSpot
import io.github.ifa.glancewidget.utils.isSupportedDynamicColor

@Composable
fun rememberColorScheme(
    isDarkTheme: Boolean,
    isEnabledBlackDark: Boolean,
    isDynamicColor: Boolean,
    seedColor: Int = 0xFFFFFF,
): ColorScheme {
    val context = LocalContext.current
    val supportsDynamic = isSupportedDynamicColor()

    return remember(isDarkTheme, isEnabledBlackDark, isDynamicColor, seedColor) {
        if (isDynamicColor && supportsDynamic) {
            if (isDarkTheme) {
                val dynamicDark = dynamicDarkColorScheme(context)
                if (isEnabledBlackDark) {
                    dynamicDark.copy(
                        onBackground = Color.White,
                        surface = Color.Black,
                        onSurface = Color.White,
                        surfaceDim = Color.Black,
                        surfaceContainer = Color.Black,
                        surfaceContainerLow = Color.Black,
                        surfaceContainerLowest = Color.Black
                    )
                } else {
                    dynamicDark
                }
            } else {
                dynamicLightColorScheme(context)
            }
        } else {
            if (isDarkTheme) {
                getDarkScheme(seedColor, isEnabledBlackDark)
            } else {
                getLightScheme(seedColor)
            }
        }
    }
}

@SuppressLint("RestrictedApi")
fun getLightScheme(argb: Int): ColorScheme {
    val hct = Hct.fromInt(argb)
    val scheme = SchemeTonalSpot(hct, false, 0.0)
    val dynamicColors = MaterialDynamicColors()
    return lightColorScheme(
        primary = Color(dynamicColors.primary().getArgb(scheme)),
        onPrimary = Color(dynamicColors.onPrimary().getArgb(scheme)),
        primaryContainer = Color(dynamicColors.primaryContainer().getArgb(scheme)),
        onPrimaryContainer = Color(dynamicColors.onPrimaryContainer().getArgb(scheme)),

        secondary = Color(dynamicColors.secondary().getArgb(scheme)),
        onSecondary = Color(dynamicColors.onSecondary().getArgb(scheme)),
        secondaryContainer = Color(dynamicColors.secondaryContainer().getArgb(scheme)),
        onSecondaryContainer = Color(dynamicColors.onSecondaryContainer().getArgb(scheme)),

        tertiary = Color(dynamicColors.tertiary().getArgb(scheme)),
        onTertiary = Color(dynamicColors.onTertiary().getArgb(scheme)),
        tertiaryContainer = Color(dynamicColors.tertiaryContainer().getArgb(scheme)),
        onTertiaryContainer = Color(dynamicColors.onTertiaryContainer().getArgb(scheme)),

        error = Color(dynamicColors.error().getArgb(scheme)),
        onError = Color(dynamicColors.onError().getArgb(scheme)),
        errorContainer = Color(dynamicColors.errorContainer().getArgb(scheme)),
        onErrorContainer = Color(dynamicColors.onErrorContainer().getArgb(scheme)),

        background = Color(dynamicColors.background().getArgb(scheme)),
        onBackground = Color(dynamicColors.onBackground().getArgb(scheme)),
        surface = Color(dynamicColors.surface().getArgb(scheme)),
        onSurface = Color(dynamicColors.onSurface().getArgb(scheme)),
        surfaceVariant = Color(dynamicColors.surfaceVariant().getArgb(scheme)),
        onSurfaceVariant = Color(dynamicColors.onSurfaceVariant().getArgb(scheme)),

        outline = Color(dynamicColors.outline().getArgb(scheme)),
        outlineVariant = Color(dynamicColors.outlineVariant().getArgb(scheme)),
        scrim = Color(dynamicColors.scrim().getArgb(scheme)),
        inversePrimary = Color(dynamicColors.inversePrimary().getArgb(scheme)),
        inverseSurface = Color(dynamicColors.inverseSurface().getArgb(scheme)),
        inverseOnSurface = Color(dynamicColors.inverseOnSurface().getArgb(scheme)),

        surfaceBright = Color(dynamicColors.surfaceBright().getArgb(scheme)),
        surfaceDim = Color(dynamicColors.surfaceDim().getArgb(scheme)),
        surfaceContainer = Color(dynamicColors.surfaceContainer().getArgb(scheme)),
        surfaceContainerLow = Color(dynamicColors.surfaceContainerLow().getArgb(scheme)),
        surfaceContainerLowest = Color(dynamicColors.surfaceContainerLowest().getArgb(scheme)),
        surfaceContainerHigh = Color(dynamicColors.surfaceContainerHigh().getArgb(scheme)),
        surfaceContainerHighest = Color(dynamicColors.surfaceContainerHighest().getArgb(scheme)),
    )
}

@SuppressLint("RestrictedApi")
fun getDarkScheme(
    argb: Int,
    isAmoled: Boolean = false,
): ColorScheme {
    val hct = Hct.fromInt(argb)
    val scheme = SchemeTonalSpot(hct, true, 0.0)
    val dynamicColors = MaterialDynamicColors()

    return darkColorScheme(
        primary = Color(dynamicColors.primary().getArgb(scheme)),
        onPrimary = Color(dynamicColors.onPrimary().getArgb(scheme)),
        primaryContainer = Color(dynamicColors.primaryContainer().getArgb(scheme)),
        onPrimaryContainer = Color(dynamicColors.onPrimaryContainer().getArgb(scheme)),

        secondary = Color(dynamicColors.secondary().getArgb(scheme)),
        onSecondary = Color(dynamicColors.onSecondary().getArgb(scheme)),
        secondaryContainer = Color(dynamicColors.secondaryContainer().getArgb(scheme)),
        onSecondaryContainer = Color(dynamicColors.onSecondaryContainer().getArgb(scheme)),

        tertiary = Color(dynamicColors.tertiary().getArgb(scheme)),
        onTertiary = Color(dynamicColors.onTertiary().getArgb(scheme)),
        tertiaryContainer = Color(dynamicColors.tertiaryContainer().getArgb(scheme)),
        onTertiaryContainer = Color(dynamicColors.onTertiaryContainer().getArgb(scheme)),

        error = Color(dynamicColors.error().getArgb(scheme)),
        onError = Color(dynamicColors.onError().getArgb(scheme)),
        errorContainer = Color(dynamicColors.errorContainer().getArgb(scheme)),
        onErrorContainer = Color(dynamicColors.onErrorContainer().getArgb(scheme)),

        background = Color(dynamicColors.background().getArgb(scheme)),
        onBackground = if (isAmoled) Color.White else Color(dynamicColors.onBackground().getArgb(scheme)),
        surface = if (isAmoled) Color.Black else Color(dynamicColors.surface().getArgb(scheme)),
        onSurface = if (isAmoled) Color.White else Color(dynamicColors.onSurface().getArgb(scheme)),

        surfaceBright = Color(dynamicColors.surfaceBright().getArgb(scheme)),
        surfaceDim = if (isAmoled) Color.Black else Color(dynamicColors.surfaceDim().getArgb(scheme)),
        surfaceContainer = if (isAmoled) Color.Black else Color(dynamicColors.surfaceContainer().getArgb(scheme)),
        surfaceContainerLow = if (isAmoled) Color.Black else Color(dynamicColors.surfaceContainerLow().getArgb(scheme)),
        surfaceContainerLowest = if (isAmoled) Color.Black else Color(dynamicColors.surfaceContainerLowest().getArgb(scheme)),
        surfaceContainerHigh = Color(dynamicColors.surfaceContainerHigh().getArgb(scheme)),
        surfaceContainerHighest = Color(dynamicColors.surfaceContainerHighest().getArgb(scheme)),

        surfaceVariant = Color(dynamicColors.surfaceVariant().getArgb(scheme)),
        onSurfaceVariant = Color(dynamicColors.onSurfaceVariant().getArgb(scheme)),
        outline = Color(dynamicColors.outline().getArgb(scheme)),
        outlineVariant = Color(dynamicColors.outlineVariant().getArgb(scheme)),
        scrim = Color(dynamicColors.scrim().getArgb(scheme)),
        inversePrimary = Color(dynamicColors.inversePrimary().getArgb(scheme)),
        inverseSurface = Color(dynamicColors.inverseSurface().getArgb(scheme)),
        inverseOnSurface = Color(dynamicColors.inverseOnSurface().getArgb(scheme)),
        surfaceTint = Color(dynamicColors.primary().getArgb(scheme))
    )
}

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