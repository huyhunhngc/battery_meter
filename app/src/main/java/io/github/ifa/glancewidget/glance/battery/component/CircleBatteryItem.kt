package io.github.ifa.glancewidget.glance.battery.component

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
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
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.github.ifa.glancewidget.model.DeviceType

@SuppressLint("RestrictedApi")
@Composable
fun CircleBatteryItem(
    deviceType: DeviceType,
    percent: Int,
    isCharging: Boolean,
    scaleTextSize: Float = 1.0f,
    transparency: Float = 1f,
    modifier: GlanceModifier = GlanceModifier,
) {
    val context = LocalContext.current
    val backgroundColor = GlanceTheme.colors.secondaryContainer
    val widgetBackgroundColor = ColorProvider(
        GlanceTheme.colors.widgetBackground.getColor(context).copy(alpha = transparency)
    )
    val foregroundColor = GlanceTheme.colors.inversePrimary
    val onBackgroundColor = GlanceTheme.colors.primary
    val iconColor = onBackgroundColor.getColor(context)
        .copy(alpha = if (deviceType == DeviceType.OTHER) 0.2f else 1.0f)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter,
    ) {
        GlanceCanvas(modifier = GlanceModifier.fillMaxSize()) {
            val rectF = RectF(x - radius, y - radius + 2f, x + radius, y + radius)
            val spaceStartAngle = if (isCharging) 40f else 20f
            val spaceSweepAngle = if (isCharging) 40f else 0f
            drawCircle(x, y, radius + 32f, Paint().apply {
                color = widgetBackgroundColor.getColor(context).toArgb()
            })
            drawArc(rectF, 250f + spaceStartAngle, 360f - spaceSweepAngle, false, Paint().apply {
                color = backgroundColor.getColor(context).toArgb()
                style = Paint.Style.STROKE
                strokeWidth = 72f
                strokeCap = Paint.Cap.ROUND
            })
            drawArc(
                rectF,
                250f + spaceStartAngle,
                minOf(percent.toFloat() / 100 * 360f, 360f - spaceSweepAngle),
                false,
                Paint().apply {
                    color = foregroundColor.getColor(context).toArgb()
                    style = Paint.Style.STROKE
                    strokeWidth = 72f
                    strokeCap = Paint.Cap.ROUND
                }
            )
            if (isCharging) {
                val path = Path()
                val xAxis = 8f
                val yAxis = 3 * xAxis
                path.moveTo(x + 2 * xAxis, y - radius - 2f * yAxis - 8f)
                path.lineTo(x - 4 * xAxis, y - radius + yAxis - 8f)
                path.lineTo(x - xAxis, y - radius + yAxis - 8f)
                path.lineTo(x - 2 * xAxis, y - radius + 3f * yAxis - 8f)
                path.lineTo(x + 4 * xAxis, y - radius - 8f)
                path.lineTo(x + xAxis, y - radius - 8f)
                path.close()
                drawPath(
                    path,
                    Paint().apply {
                        color = onBackgroundColor.getColor(context).toArgb()
                    }
                )
            }
        }

        Column(
            modifier = GlanceModifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val scale = if (percent > 0) scaleTextSize else scaleTextSize * 1.4f
            if (percent > 0) {
                Text(
                    text = percent.toString(),
                    style = TextStyle(
                        color = if (transparency == 0f) GlanceTheme.colors.inversePrimary else GlanceTheme.colors.primary,
                        fontSize = (9 * scaleTextSize).sp
                    ),
                    fontWeight = FontWeight.Bold,
                    modifier = GlanceModifier.padding(bottom = 2.dp)
                )
            }
            Image(
                modifier = GlanceModifier.size((16 * scale).dp),
                provider = ImageProvider(deviceType.icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(ColorProvider(iconColor))
            )
        }

    }
}

@Composable
private fun GlanceCanvas(
    modifier: GlanceModifier = GlanceModifier,
    nativeCanvas: CanvasInGlance.() -> Unit
) {
    val bitmap = createBitmap(600, 600)
    val canvas = CanvasInGlance(bitmap, 300f, 300f, 240f)
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
