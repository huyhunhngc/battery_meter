package io.github.ifa.glancewidget

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import io.github.ifa.glancewidget.domain.localAppSettingsRepository
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.model.ThemeTypeColor
import io.github.ifa.glancewidget.navigation.AppNavHost
import io.github.ifa.glancewidget.ui.theme.AppColorScheme
import io.github.ifa.glancewidget.ui.theme.AppTheme
import io.github.ifa.glancewidget.ui.theme.Type
import io.github.ifa.glancewidget.ui.theme.getDarkScheme
import io.github.ifa.glancewidget.ui.theme.getLightScheme
import io.github.ifa.glancewidget.ui.theme.topBarColors
import io.github.ifa.glancewidget.utils.isSupportedDynamicColor

@Composable
fun BatteryApp(
    modifier: Modifier = Modifier,
    startDestination: String,
    appSettingsRepository: AppSettingsRepository = localAppSettingsRepository()
) {
    val settings by appSettingsRepository.get().collectAsStateWithLifecycle(AppSettings())
    val isDarkTheme = when (settings.theme) {
        ThemeType.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        ThemeType.DARK_THEME -> true
        ThemeType.LIGHT_THEME -> false
    }

    val colorScheme = rememberColorScheme(isDarkTheme, settings.themeColor)
    val navController: NavHostController = rememberNavController()
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        }
    }
    val appColorScheme = rememberAppColorScheme(isDarkTheme, colorScheme)
    AppTheme(
        colorScheme = colorScheme,
        typography = Type.typography,
        appColorScheme = appColorScheme
    ) {
        Surface(
            color = topBarColors.containerColor
        ) {
            AppNavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = modifier
            )
        }
    }
}

@Composable
fun rememberColorScheme(
    isDarkTheme: Boolean,
    themeColor: ThemeTypeColor,
): ColorScheme {
    val context = LocalContext.current
    val colorScheme = if (themeColor == ThemeTypeColor.System && isSupportedDynamicColor()) {
        if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (isDarkTheme) getDarkScheme(themeColor.code) else getLightScheme(themeColor.code)
    }

    return remember(isDarkTheme, themeColor) { colorScheme }
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
                    getDarkScheme(themeTypeColor.code)
                } else {
                    getLightScheme(themeTypeColor.code)
                }
            )
        }
    }
}
