package io.github.ifa.glancewidget.ui.theme

import android.annotation.SuppressLint
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.google.android.material.color.utilities.CorePalette
import com.google.android.material.color.utilities.Scheme
import io.github.ifa.glancewidget.model.ThemeTypeColor

@SuppressLint("RestrictedApi")
fun getLightScheme(argb: Int = ThemeTypeColor.entries.first().code): ColorScheme {
    val scheme = Scheme.light(argb)
    val core = CorePalette.of(argb)
    return lightColorScheme(
        primary = Color(scheme.primary),
        onPrimary = Color(scheme.onPrimary),
        primaryContainer = Color(scheme.primaryContainer),
        onPrimaryContainer = Color(scheme.onPrimaryContainer),
        secondary = Color(scheme.secondary),
        onSecondary = Color(scheme.onSecondary),
        secondaryContainer = Color(scheme.secondaryContainer),
        tertiary = Color(scheme.tertiary),
        onTertiary = Color(scheme.onTertiary),
        tertiaryContainer = Color(scheme.tertiaryContainer),
        onTertiaryContainer = Color(scheme.onTertiaryContainer),
        error = Color(scheme.error),
        errorContainer = Color(scheme.errorContainer),
        onErrorContainer = Color(scheme.onErrorContainer),
        background = Color(core.n1.tone(98)),
        onBackground = Color(scheme.onBackground),
        surface = Color(core.n1.tone(98)),
        onSurface = Color(scheme.onSurface),
        surfaceContainerLowest = Color(core.n1.tone(100)),
        surfaceBright = Color(core.n1.tone(98)),
        surfaceContainerLow = Color(core.n1.tone(96)),
        surfaceContainer = Color(core.n1.tone(94)),
        surfaceContainerHigh = Color(core.n1.tone(92)),
        surfaceContainerHighest = Color(core.n1.tone(90)),
        surfaceDim = Color(core.n1.tone(87)),
        surfaceVariant = Color(scheme.surfaceVariant),
        onSurfaceVariant = Color(scheme.onSurfaceVariant),
        outline = Color(scheme.outline),
        outlineVariant = Color(scheme.outlineVariant),
        scrim = Color(scheme.scrim),
        inversePrimary = Color(scheme.inversePrimary),
        inverseSurface = Color(scheme.inverseSurface),
        inverseOnSurface = Color(scheme.inverseOnSurface),
        surfaceTint = Color(scheme.primary),
    )
}

@SuppressLint("RestrictedApi")
fun getDarkScheme(argb: Int = ThemeTypeColor.entries.first().code): ColorScheme {
    val scheme = Scheme.dark(argb)
    val core = CorePalette.of(argb)
    return darkColorScheme(
        primary = Color(scheme.primary),
        onPrimary = Color(scheme.onPrimary),
        primaryContainer = Color(scheme.primaryContainer),
        onPrimaryContainer = Color(scheme.onPrimaryContainer),
        secondary = Color(scheme.secondary),
        onSecondary = Color(scheme.onSecondary),
        secondaryContainer = Color(scheme.secondaryContainer),
        tertiary = Color(scheme.tertiary),
        onTertiary = Color(scheme.onTertiary),
        tertiaryContainer = Color(scheme.tertiaryContainer),
        onTertiaryContainer = Color(scheme.onTertiaryContainer),
        error = Color(scheme.error),
        errorContainer = Color(scheme.errorContainer),
        onErrorContainer = Color(scheme.onErrorContainer),
        background = Color(core.n1.tone(6)),
        onBackground = Color(scheme.onBackground),
        surface = Color(core.n1.tone(6)),
        onSurface = Color(scheme.onSurface),
        surfaceBright = Color(core.n2.tone(24)),
        surfaceContainerHighest = Color(core.n2.tone(24)),
        surfaceContainerHigh = Color(core.n2.tone(17)),
        surfaceContainer = Color(core.n2.tone(12)),
        surfaceContainerLow = Color(core.n2.tone(10)),
        surfaceDim = Color(core.n2.tone(6)),
        surfaceContainerLowest = Color(core.n2.tone(4)),
        surfaceVariant = Color(scheme.surfaceVariant),
        onSurfaceVariant = Color(scheme.onSurfaceVariant),
        outline = Color(scheme.outline),
        outlineVariant = Color(scheme.outlineVariant),
        scrim = Color(scheme.scrim),
        inversePrimary = Color(scheme.inversePrimary),
        inverseSurface = Color(scheme.inverseSurface),
        inverseOnSurface = Color(scheme.inverseOnSurface),
        surfaceTint = Color(scheme.primary),
    )
}
