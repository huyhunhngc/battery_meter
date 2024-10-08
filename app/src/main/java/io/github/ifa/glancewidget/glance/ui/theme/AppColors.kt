package io.github.ifa.glancewidget.glance.ui.theme

import androidx.compose.ui.graphics.Color

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
