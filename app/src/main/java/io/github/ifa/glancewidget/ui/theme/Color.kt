package io.github.ifa.glancewidget.ui.theme

import android.annotation.SuppressLint
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.google.android.material.color.utilities.CorePalette
import com.google.android.material.color.utilities.Scheme
import io.github.ifa.glancewidget.model.ThemeTypeColor

val routine_secondary50 = Color(0xFF00894B)
val routine_secondary80 = Color(0xFF43E188)

val pinkLight = Color(0xFFD81B60)
val pinkDark = Color(0xFFF48FB1)

val redLight = Color(0xFFD32F2F)
val redDark = Color(0xFFE57373)

val blueLight = Color(0xFF4285F4)
val blueDark = Color(0xFF6DB6FF)

val tealLight = Color(0xFF009688)
val tealDark = Color(0xFF80CBC4)

val indigoLight = Color(0xFF3F51B5)
val indigoDark = Color(0xFF7986CB)

val greenLight = Color(0xFF7AFFB4)
val greenDark = Color(0xFF00A956)

val limeLight = Color(0xFFCDDC39)
val limeDark = Color(0xFFdce775)

val yellowLight = Color(0xFFffeb3b)
val yellowDark = Color(0xFFfff59d)

val amberLight = Color(0xFFffc107)
val amberDark = Color(0xFFffe082)

val orangeLight = Color(0xFFF86734)
val orangeDark = Color(0xFFFFAC8D)

val brownLight = Color(0xFF795548)
val brownDark = Color(0xFFbcaaa4)

val grayLight = Color(0xFF616161)
val grayDark = Color(0xFFEEEEEE)


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
        background = Color(scheme.background),
        onBackground = Color(scheme.onBackground),
        surface = Color(scheme.surface),
        onSurface = Color(scheme.onSurface),
        surfaceContainer = Color(core.n2.tone(94)),
        surfaceContainerLow = Color(core.n2.tone(96)),
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
        background = Color(scheme.background),
        onBackground = Color(scheme.onBackground),
        surface = Color(scheme.surface),
        onSurface = Color(scheme.onSurface),
        surfaceContainer = Color(core.n2.tone(12)),
        surfaceContainerLow = Color(core.n2.tone(8)),
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