package io.github.ifa.glancewidget.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.github.ifa.glancewidget.R

val googleFlex400 = FontFamily(Font(R.font.google_sans_flex_400))
val googleFlex600 = FontFamily(Font(R.font.google_sans_flex_600))
val TYPOGRAPHY = Typography()

object Type {
    val typography = Typography(
        displayLarge = TYPOGRAPHY.displayLarge.copy(
            fontFamily = googleFlex600,
            fontFeatureSettings = "ss02, dlig"
        ),
        displayMedium = TYPOGRAPHY.displayMedium.copy(
            fontFamily = googleFlex600,
            fontFeatureSettings = "ss02, dlig"
        ),
        displaySmall = TYPOGRAPHY.displaySmall.copy(
            fontFamily = googleFlex600,
            fontFeatureSettings = "ss02, dlig"
        ),
        headlineLarge = TYPOGRAPHY.headlineLarge.copy(
            fontFamily = googleFlex600,
            fontFeatureSettings = "ss02, dlig"
        ),
        headlineMedium = TYPOGRAPHY.headlineMedium.copy(
            fontFamily = googleFlex600,
            fontFeatureSettings = "ss02, dlig"
        ),
        headlineSmall = TYPOGRAPHY.headlineSmall.copy(
            fontFamily = googleFlex600,
            fontFeatureSettings = "ss02, dlig"
        ),
        titleLarge = TYPOGRAPHY.titleLarge.copy(
            fontFamily = googleFlex400,
            fontFeatureSettings = "ss02, dlig"
        ),
        titleMedium = TYPOGRAPHY.titleMedium.copy(
            fontFamily = googleFlex600,
            fontFeatureSettings = "ss02, dlig"
        ),
        titleSmall = TYPOGRAPHY.titleSmall.copy(
            fontFamily = googleFlex600,
            fontFeatureSettings = "ss02, dlig"
        ),
        bodyLarge = TYPOGRAPHY.bodyLarge.copy(
            fontFamily = googleFlex600,
            fontFeatureSettings = "ss02, dlig"
        ),
        bodyMedium = TYPOGRAPHY.bodyMedium.copy(
            fontFamily = googleFlex400,
            fontFeatureSettings = "ss02, dlig"
        ),
        bodySmall = TYPOGRAPHY.bodySmall.copy(
            fontFamily = googleFlex400,
            fontFeatureSettings = "ss02, dlig"
        ),
        labelLarge = TYPOGRAPHY.labelLarge.copy(
            fontFamily = googleFlex600,
            fontFeatureSettings = "ss02, dlig"
        ),
        labelMedium = TYPOGRAPHY.labelMedium.copy(
            fontFamily = googleFlex600,
            fontFeatureSettings = "ss02, dlig"
        ),
        labelSmall = TYPOGRAPHY.labelSmall.copy(
            fontFamily = googleFlex600,
            fontFeatureSettings = "ss02, dlig"
        )
    )

}