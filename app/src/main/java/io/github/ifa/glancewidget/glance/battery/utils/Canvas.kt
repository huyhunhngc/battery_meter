package io.github.ifa.glancewidget.glance.battery.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.graphics.createBitmap
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.ContentScale

@Composable
fun GlanceCanvas(
    modifier: GlanceModifier = GlanceModifier,
    nativeCanvas: CanvasInGlance.() -> Unit
) {
    val sizePx = 480
    val bitmap = remember { createBitmap(sizePx, sizePx) }
    val canvas = CanvasInGlance(
        bitmap,
        sizePx / 2f,
        sizePx / 2f,
        sizePx * 0.4f
    )
    canvas.drawColor(android.graphics.Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    nativeCanvas(canvas)
    Image(
        modifier = modifier,
        provider = ImageProvider(bitmap),
        contentScale = ContentScale.Fit,
        contentDescription = null,
    )
}

class CanvasInGlance(bitmap: Bitmap, val x: Float, val y: Float, val radius: Float) : Canvas(bitmap)