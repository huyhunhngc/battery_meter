package io.github.ifa.glancewidget.ui.theme

import androidx.compose.ui.graphics.Color

object AppColor {
    val LightColors = lightColors(
        check = routine_secondary50,
        amber = amberLight,
        blue = blueLight,
        brown = brownLight,
        gray = grayLight,
        green = greenLight,
        indigo = indigoLight,
        lime = limeLight,
        orange = orangeLight,
        red = redLight,
        pink = pinkLight,
        teal = tealLight,
        yellow = yellowLight
    )

    val DarkColors = darkColors(
        check = routine_secondary80,
        amber = amberDark,
        blue = blueDark,
        brown = brownDark,
        gray = grayDark,
        green = greenDark,
        indigo = indigoDark,
        lime = limeDark,
        orange = orangeDark,
        red = redDark,
        pink = pinkDark,
        teal = tealDark,
        yellow = yellowDark
    )
}

class AppColors(
    val check: Color,
    val amber: Color,
    val blue: Color,
    val brown: Color,
    val gray: Color,
    val green: Color,
    val indigo: Color,
    val lime: Color,
    val orange: Color,
    val red: Color,
    val pink: Color,
    val teal: Color,
    val yellow: Color
)

fun lightColors(
    check: Color = Color.Unspecified,
    amber: Color = Color.Unspecified,
    blue: Color = Color.Unspecified,
    brown: Color = Color.Unspecified,
    gray: Color = Color.Unspecified,
    green: Color = Color.Unspecified,
    indigo: Color = Color.Unspecified,
    lime: Color = Color.Unspecified,
    orange: Color = Color.Unspecified,
    red: Color = Color.Unspecified,
    pink: Color = Color.Unspecified,
    teal: Color = Color.Unspecified,
    yellow: Color = Color.Unspecified
): AppColors =
    AppColors(
        check = check,
        amber = amber,
        blue = blue,
        brown = brown,
        gray = gray,
        green = green,
        indigo = indigo,
        lime = lime,
        orange = orange,
        red = red,
        pink = pink,
        teal = teal,
        yellow = yellow
    )

fun darkColors(
    check: Color = Color.Unspecified,
    amber: Color = Color.Unspecified,
    blue: Color = Color.Unspecified,
    brown: Color = Color.Unspecified,
    gray: Color = Color.Unspecified,
    green: Color = Color.Unspecified,
    indigo: Color = Color.Unspecified,
    lime: Color = Color.Unspecified,
    orange: Color = Color.Unspecified,
    red: Color = Color.Unspecified,
    pink: Color = Color.Unspecified,
    teal: Color = Color.Unspecified,
    yellow: Color = Color.Unspecified
): AppColors =
    AppColors(
        check = check,
        amber = amber,
        blue = blue,
        brown = brown,
        gray = gray,
        green = green,
        indigo = indigo,
        lime = lime,
        orange = orange,
        red = red,
        pink = pink,
        teal = teal,
        yellow = yellow
    )

val routine_secondary50 = Color(0xFF00894B)
val routine_secondary80 = Color(0xFF43E188)

val pinkLight = Color(0xFFD81B60)
val pinkDark = Color(0xFFF48FB1)

val redLight = Color(0xFFD32F2F)
val redDark = Color(0xFFE57373)

val blueLight = Color(0xFF4285F4)
val blueDark = Color(0xFF6DB6FF)

val tealLight = Color(0xFF009688)
val tealDark = Color(0xFF80CBC4)

val indigoLight = Color(0xFF3F51B5)
val indigoDark = Color(0xFF7986CB)

val greenLight = Color(0xFF7AFFB4)
val greenDark = Color(0xFF00A956)

val limeLight = Color(0xFFCDDC39)
val limeDark = Color(0xFFdce775)

val yellowLight = Color(0xFFffeb3b)
val yellowDark = Color(0xFFfff59d)

val amberLight = Color(0xFFffc107)
val amberDark = Color(0xFFffe082)

val orangeLight = Color(0xFFF86734)
val orangeDark = Color(0xFFFFAC8D)

val brownLight = Color(0xFF795548)
val brownDark = Color(0xFFbcaaa4)

val grayLight = Color(0xFF616161)
val grayDark = Color(0xFFEEEEEE)
