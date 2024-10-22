package io.github.ifa.glancewidget.glance.battery.component

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle
import io.github.ifa.glancewidget.model.DeviceType


@Composable
fun CircleBatteryItem(
    deviceType: DeviceType,
    percent: Int,
    isCharging: Boolean,
    deviceName: String,
    scaleTextSize: Float = 1.0f,
    modifier: GlanceModifier = GlanceModifier,
) {
    val context = LocalContext.current
    val backgroundColor = GlanceTheme.colors.secondaryContainer
    val foregroundColor = GlanceTheme.colors.inversePrimary
    val onBackgroundColor = GlanceTheme.colors.primary

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter,
    ) {
        GlanceCanvas(modifier = GlanceModifier.fillMaxSize()) {
            val rectF = RectF(x - radius, y - radius + 2f, x + radius, y + radius)
            drawArc(rectF, 0f, 360f, false, Paint().apply {
                color = backgroundColor.getColor(context).toArgb()
                style = Paint.Style.STROKE
                strokeWidth = 24f
            })
            drawArc(rectF, 270f, percent.toFloat() / 100 * 360f, false, Paint().apply {
                color = foregroundColor.getColor(context).toArgb()
                style = Paint.Style.STROKE
                strokeWidth = 24f
                strokeCap = Paint.Cap.ROUND
            })
            if (isCharging) {
                val path = Path()
                path.moveTo(x + 4f, y - radius - 16f)
                path.lineTo(x - 12f, y - radius + 2f)
                path.lineTo(x - 2f, y - radius + 2f)
                path.lineTo(x - 5f, y - radius + 14f)
                path.lineTo(x + 12f, y - radius - 5f)
                path.lineTo(x + 2f, y - radius - 5f)
                path.close()
                drawPath(path, Paint().apply { color = onBackgroundColor.getColor(context).toArgb() })
            }
        }

        Column(
            modifier = GlanceModifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (percent > 0) {
                Text(
                    text = "$percent",
                    style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontSize = (9 * scaleTextSize).sp
                    ),
                    fontWeight = FontWeight.Bold,
                    modifier = GlanceModifier.padding(bottom = 2.dp)
                )
            }
            Image(
                modifier = GlanceModifier.size((16 * scaleTextSize).dp),
                provider = ImageProvider(deviceType.icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(GlanceTheme.colors.primary)
            )
        }

    }
}

@Composable
private fun GlanceCanvas(
    modifier: GlanceModifier = GlanceModifier,
    nativeCanvas: CanvasInGlance.() -> Unit
) {
    val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
    val canvas = CanvasInGlance(bitmap, 100f, 100f, 85f)
    nativeCanvas(canvas)
    Image(
        modifier = modifier,
        provider = ImageProvider(bitmap),
        contentScale = ContentScale.Fit,
        contentDescription = null,
    )
}

class CanvasInGlance(bitmap: Bitmap, val x: Float, val y: Float, val radius: Float) :
    Canvas(bitmap)

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
fun CircleBatteryItemPreview() {
    CircleBatteryItem(
        deviceType = DeviceType.PHONE,
        percent = 100,
        isCharging = true,
        deviceName = "",
    )
}