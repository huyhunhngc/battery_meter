package io.github.ifa.glancewidget.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import io.github.ifa.glancewidget.model.ThemeTypeColor

data class AppColorScheme(
    val themeTypeColor: ThemeTypeColor,
    val colorScheme: ColorScheme
)

internal val LocalAppColorSchemes = compositionLocalOf<List<AppColorScheme>> { listOf() }

@Composable
fun AppTheme(
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    appColorScheme: List<AppColorScheme> = listOf(),
    shapes: Shapes = MaterialTheme.shapes,
    typography: Typography = MaterialTheme.typography,
    content: @Composable () -> Unit
) {
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
