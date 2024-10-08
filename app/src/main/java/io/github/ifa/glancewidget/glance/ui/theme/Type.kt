package io.github.ifa.glancewidget.glance.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.github.ifa.glancewidget.ui.theme.googlesansBoldFont
import io.github.ifa.glancewidget.ui.theme.googlesansLightFont
import io.github.ifa.glancewidget.ui.theme.googlesansMediumFont
import io.github.ifa.glancewidget.ui.theme.googlesansRegularFont
import io.github.ifa.glancewidget.ui.theme.googlesansSemiBoldFont

val googleFont: FontFamily = FontFamily(
    googlesansLightFont,
    googlesansRegularFont,
    googlesansMediumFont,
    googlesansSemiBoldFont,
    googlesansBoldFont
)

object Type {
    val typography = Typography(
        headlineLarge = TextStyle(
            fontFamily = googleFont, fontWeight = FontWeight.Medium, fontSize = 32.sp
        ), headlineMedium = TextStyle(
            fontFamily = googleFont, fontWeight = FontWeight.Medium, fontSize = 28.sp
        ), headlineSmall = TextStyle(
            fontFamily = googleFont,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            letterSpacing = 0.15.sp
        ), titleLarge = TextStyle(
            fontFamily = googleFont, fontWeight = FontWeight.Medium, fontSize = 20.sp
        ), titleMedium = TextStyle(
            fontFamily = googleFont, fontWeight = FontWeight.Medium, fontSize = 16.sp
        ), titleSmall = TextStyle(
            fontFamily = googleFont, fontWeight = FontWeight.Medium, fontSize = 14.sp
        ), bodyLarge = TextStyle(
            fontFamily = googleFont, fontWeight = FontWeight.Medium, fontSize = 16.sp
        ), bodyMedium = TextStyle(
            fontFamily = googleFont, fontWeight = FontWeight.Medium, fontSize = 14.sp
        ), bodySmall = TextStyle(
            fontFamily = googleFont, fontWeight = FontWeight.Medium, fontSize = 12.sp
        ), labelLarge = TextStyle(
            fontFamily = googleFont, fontWeight = FontWeight.SemiBold, fontSize = 12.sp
        ), labelMedium = TextStyle(
            fontFamily = googleFont, fontWeight = FontWeight.Medium, fontSize = 12.sp
        ), labelSmall = TextStyle(
            fontFamily = googleFont,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            letterSpacing = 1.5.sp
        )
    )
}