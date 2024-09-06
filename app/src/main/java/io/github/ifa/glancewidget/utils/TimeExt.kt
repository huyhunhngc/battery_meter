package io.github.ifa.glancewidget.utils

fun Long.toHHMMSS(): String {
    val timeInSeconds = this / 1000
    val hours =  when(val hours = timeInSeconds / 3600) {
        in 0..9 -> "0$hours"
        else -> "$hours"
    }
    val minutes = when(val minutes = (timeInSeconds % 3600) / 60) {
        in 0..9 -> "0$minutes"
        else -> "$minutes"
    }
    val seconds = when(val resultSeconds = timeInSeconds % 60) {
        in 0..9 -> "0$resultSeconds"
        else -> "$resultSeconds"
    }
    return listOf(hours, minutes, seconds).joinToString(":")
}