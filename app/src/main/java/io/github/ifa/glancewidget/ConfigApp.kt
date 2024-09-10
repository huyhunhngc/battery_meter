package io.github.ifa.glancewidget

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import io.github.ifa.glancewidget.domain.localAppSettingsRepository
import io.github.ifa.glancewidget.glance.ui.theme.AppTheme
import io.github.ifa.glancewidget.glance.ui.theme.DarkColorScheme
import io.github.ifa.glancewidget.glance.ui.theme.LightColorScheme
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.ThemeType

@Composable
fun ConfigApp(
    modifier: Modifier = Modifier,
    startDestination: String,
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme(),
    appSettingsRepository: AppSettingsRepository = localAppSettingsRepository()
) {
    val settings by appSettingsRepository.get().collectAsStateWithLifecycle(AppSettings())
    val darkTheme by remember(settings) {
        derivedStateOf { isDarkTheme(settings.theme, isSystemInDarkTheme) }
    }
    val colorScheme = rememberColorScheme(darkTheme)
    val navController: NavHostController = rememberNavController()
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(darkTheme) {
        systemUiController.statusBarDarkContentEnabled = !darkTheme
    }
    AppTheme(colorScheme = colorScheme) {
        AppNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier,
            onBackClick = {
                navController.popBackStack()
            },
            onBackClickBlockNavController = {
                popBackStack()
            }
        )
    }
}

@Composable
fun rememberColorScheme(
    isDarkTheme: Boolean,
): ColorScheme {
    val context = LocalContext.current
    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (isDarkTheme) DarkColorScheme else LightColorScheme
    }
    return remember(isDarkTheme) { colorScheme }
}

fun isDarkTheme(themeType: ThemeType, isSystemInDarkTheme: Boolean): Boolean {
    return when (themeType) {
        ThemeType.FOLLOW_SYSTEM -> isSystemInDarkTheme
        ThemeType.DARK_THEME -> true
        ThemeType.LIGHT_THEME -> false
    }
}