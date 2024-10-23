package io.github.ifa.glancewidget.glance.battery.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.unit.ColorProvider
import io.github.ifa.glancewidget.R


@SuppressLint("RestrictedApi")
fun GlanceModifier.cornerRadiusCompat(
    cornerRadius: Int,
    color: ColorProvider,
): GlanceModifier {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this.background(color).cornerRadius(cornerRadius.dp)
    } else {
        if (color == ColorProvider(Color.Transparent)) {
            this.background(
                imageProvider = ImageProvider(R.drawable.bg_widget_transparent)
            )
        } else {
            this.background(
                imageProvider = ImageProvider(R.drawable.bg_widget_16),
                colorFilter = ColorFilter.tint(color)
            )
        }
    }
}
