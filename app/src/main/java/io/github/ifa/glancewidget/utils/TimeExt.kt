package io.github.ifa.glancewidget.utils

import android.content.Context
import android.icu.text.MeasureFormat
import android.icu.util.Measure
import android.icu.util.MeasureUnit
import io.github.ifa.glancewidget.R
import java.util.Locale

fun Long.toLocaleDuration(context: Context): String {
    if (this < 0) return ""

    val timeInSeconds = this / 1000
    val hours = (timeInSeconds / 3600).toInt()
    val minutes = ((timeInSeconds % 3600) / 60).toInt()

    val hoursString = if (hours > 0) {
        context.resources.getQuantityString(R.plurals.hours_format, hours, hours)
    } else {
        null
    }

    val minutesString = if (minutes > 0) {
        context.resources.getQuantityString(R.plurals.minutes_format, minutes, minutes)
    } else {
        null
    }

    return listOfNotNull(hoursString, minutesString).joinToString(" ")
}
