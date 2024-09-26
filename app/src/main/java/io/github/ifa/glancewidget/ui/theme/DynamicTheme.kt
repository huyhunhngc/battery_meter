package io.github.ifa.glancewidget.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import io.github.ifa.glancewidget.model.ThemeTypeColor
import io.github.ifa.glancewidget.ui.theme.DynamicThemeConstants.ThemeTimeout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object DynamicThemeConstants {
    const val AnimatedThemeDuration = 4500
    const val ThemeTimeout = 10000L
}

@Suppress("CompositionLocalAllowlist")
val LocalDynamicAnimatedTheme: ProvidableCompositionLocal<ColorScheme> =
    compositionLocalOf {
        getLightScheme()
    }

@Composable
fun DynamicAnimatedTheme(
    content: @Composable () -> Unit
) {
    var themeTypeColor by remember { mutableStateOf(ThemeTypeColor.System) }
    val scope = rememberCoroutineScope()
    LifecycleResumeEffect(Unit) {
        scope.launch {
            while (true) {
                delay(ThemeTimeout / 2)
                themeTypeColor = themeTypeColor.next
                delay(ThemeTimeout / 2)
            }
        }
        onPauseOrDispose { }
    }
    val appColorSchemes = LocalAppColorSchemes.current
    val materialColorScheme = MaterialTheme.colorScheme
    val colorScheme by remember(themeTypeColor) {
        derivedStateOf {
            appColorSchemes.find { it.themeTypeColor == themeTypeColor }?.colorScheme
                ?: materialColorScheme
        }
    }

    CompositionLocalProvider(
        LocalDynamicAnimatedTheme provides colorScheme
    ) {
        content()
    }
}