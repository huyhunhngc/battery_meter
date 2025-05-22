package io.github.ifa.glancewidget.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.github.ifa.glancewidget.R

internal val interLightFont = Font(R.font.interlight, FontWeight.Light)
internal val interRegularFont = Font(R.font.interregular, FontWeight.Normal)
internal val interMediumFont = Font(R.font.intermedium, FontWeight.Medium)
internal val interSemiBoldFont = Font(R.font.intersemibold, FontWeight.SemiBold)
internal val interBoldFont = Font(R.font.interbold, FontWeight.Bold)

val interFont: FontFamily = FontFamily(
    interLightFont,
    interRegularFont,
    interMediumFont,
    interSemiBoldFont,
    interBoldFont
)

object Type {
    val typography = Typography(
        headlineLarge = TextStyle(
            fontFamily = interFont, fontWeight = FontWeight.Medium, fontSize = 32.sp
        ), headlineMedium = TextStyle(
            fontFamily = interFont, fontWeight = FontWeight.Medium, fontSize = 28.sp
        ), headlineSmall = TextStyle(
            fontFamily = interFont,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            letterSpacing = 0.15.sp
        ), titleLarge = TextStyle(
            fontFamily = interFont, fontWeight = FontWeight.Medium, fontSize = 20.sp
        ), titleMedium = TextStyle(
            fontFamily = interFont, fontWeight = FontWeight.Medium, fontSize = 16.sp
        ), titleSmall = TextStyle(
            fontFamily = interFont, fontWeight = FontWeight.Medium, fontSize = 14.sp
        ), bodyLarge = TextStyle(
            fontFamily = interFont, fontWeight = FontWeight.Medium, fontSize = 16.sp
        ), bodyMedium = TextStyle(
            fontFamily = interFont, fontWeight = FontWeight.Medium, fontSize = 14.sp
        ), bodySmall = TextStyle(
            fontFamily = interFont, fontWeight = FontWeight.Medium, fontSize = 12.sp
        ), labelLarge = TextStyle(
            fontFamily = interFont, fontWeight = FontWeight.Medium, fontSize = 12.sp
        ), labelMedium = TextStyle(
            fontFamily = interFont, fontWeight = FontWeight.Medium, fontSize = 12.sp
        ), labelSmall = TextStyle(
            fontFamily = interFont,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            letterSpacing = 1.5.sp
        )
    )
}