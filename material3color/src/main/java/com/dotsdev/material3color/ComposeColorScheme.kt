package com.dotsdev.material3color

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

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
