package io.github.ifa.glancewidget.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import io.github.ifa.glancewidget.domain.localAppSettingsRepository
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.model.ThemeTypeColor
import io.github.ifa.glancewidget.utils.isSupportedDynamicColor
import kotlin.text.get

data class AppColorScheme(
    val themeTypeColor: ThemeTypeColor,
    val colorScheme: ColorScheme
)

internal val LocalAppColorSchemes = compositionLocalOf<List<AppColorScheme>> { listOf() }

@Composable
fun AppTheme(
    shapes: Shapes = MaterialTheme.shapes,
    typography: Typography = Type.typography,
    appSettingsRepository: AppSettingsRepository = localAppSettingsRepository(),
    content: @Composable () -> Unit
) {
    val settings by appSettingsRepository.get().collectAsStateWithLifecycle(AppSettings())
    val isDarkTheme = when (settings.theme) {
        ThemeType.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        ThemeType.DARK_THEME -> true
        ThemeType.LIGHT_THEME -> false
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        }
    }

    val colorScheme = rememberColorScheme(
        isDarkTheme = isDarkTheme,
        themeColor = settings.themeColor,
        isEnabledBlackDark = settings.isBlackDarkEnabled
    )
    val appColorScheme = rememberAppColorScheme(isDarkTheme, colorScheme)
    CompositionLocalProvider(
        LocalAppColorSchemes provides appColorScheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = shapes,
            typography = typography,
            content = content
        )
    }
}

@Composable
fun rememberColorScheme(
    isDarkTheme: Boolean,
    isEnabledBlackDark: Boolean,
    themeColor: ThemeTypeColor,
): ColorScheme {
    val context = LocalContext.current
    val colorScheme = if (themeColor == ThemeTypeColor.System && isSupportedDynamicColor()) {
        if (isDarkTheme) {
            val dynamicDark = dynamicDarkColorScheme(context)
            dynamicDark.copy(
                onBackground = if (isEnabledBlackDark) Color.White else dynamicDark.onBackground,
                surface = if (isEnabledBlackDark) Color.Black else dynamicDark.surface,
                onSurface = if (isEnabledBlackDark) Color.White else dynamicDark.onSurface,
                surfaceDim = if (isEnabledBlackDark) Color.Black else dynamicDark.surfaceDim,
                surfaceContainer = if (isEnabledBlackDark) Color.Black else dynamicDark.surfaceContainer,
                surfaceContainerLow = if (isEnabledBlackDark) Color.Black else dynamicDark.surfaceContainerLow,
                surfaceContainerLowest = if (isEnabledBlackDark) Color.Black else dynamicDark.surfaceContainerLowest
            )
        } else {
            dynamicLightColorScheme(context)
        }
    } else {
        if (isDarkTheme) {
            getDarkScheme(themeColor.code, isEnabledBlackDark)
        } else {
            getLightScheme(themeColor.code)
        }
    }

    return remember(isDarkTheme, themeColor, isEnabledBlackDark) { colorScheme }
}

@Composable
fun rememberAppColorScheme(isDarkTheme: Boolean, colorScheme: ColorScheme): List<AppColorScheme> {
    val appColorSchemeMap = rememberAppColorSchemeMap(isDarkTheme)
    return remember(colorScheme) {
        ThemeTypeColor.entries.map {
            if (it == ThemeTypeColor.System) {
                AppColorScheme(it, colorScheme)
            } else {
                appColorSchemeMap[it] ?: AppColorScheme(it, colorScheme)
            }
        }
    }
}

@Composable
fun rememberAppColorSchemeMap(
    isDarkTheme: Boolean
): Map<ThemeTypeColor, AppColorScheme> {
    return remember(isDarkTheme) {
        ThemeTypeColor.entries.associateWith { themeTypeColor ->
            AppColorScheme(
                themeTypeColor = themeTypeColor,
                colorScheme = if (isDarkTheme) {
                    getDarkScheme(themeTypeColor.code, true)
                } else {
                    getLightScheme(themeTypeColor.code)
                }
            )
        }
    }
}
