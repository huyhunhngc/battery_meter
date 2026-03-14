package io.github.ifa.glancewidget.ui.theme

import android.annotation.SuppressLint
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.google.android.material.color.utilities.Hct
import com.google.android.material.color.utilities.MaterialDynamicColors
import com.google.android.material.color.utilities.SchemeTonalSpot
import io.github.ifa.glancewidget.model.ThemeTypeColor

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