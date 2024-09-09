package io.github.ifa.glancewidget

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.ifa.glancewidget.glance.ui.theme.AppTheme
import io.github.ifa.glancewidget.glance.ui.theme.DarkColorScheme
import io.github.ifa.glancewidget.glance.ui.theme.LightColorScheme
import io.github.ifa.glancewidget.model.ThemeType

@Composable
fun ConfigApp(
    modifier: Modifier = Modifier,
    startDestination: String,
    themeType: ThemeType
) {
    val darkTheme: Boolean = when (themeType) {
        ThemeType.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        ThemeType.DARK_THEME -> true
        ThemeType.LIGHT_THEME -> false
    }

    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (darkTheme) DarkColorScheme else LightColorScheme
    }
    val navController: NavHostController = rememberNavController()
    AppTheme(colorScheme = colorScheme) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = colorScheme.background,
        ) {
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
}