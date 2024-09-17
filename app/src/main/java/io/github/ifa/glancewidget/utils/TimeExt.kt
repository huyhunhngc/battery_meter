package io.github.ifa.glancewidget.utils

fun Long.toHHMM(): String {
    val timeInSeconds = this / 1000
    val hours =  when(val hours = timeInSeconds / 3600) {
        in 0..9 -> "0$hours hrs"
        else -> "$hours hrs"
    }
    val minutes = when(val minutes = (timeInSeconds % 3600) / 60) {
        in 0..9 -> "0$minutes mins"
        else -> "$minutes mins"
    }
    return listOf(hours, minutes).joinToString(" ")
}