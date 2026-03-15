package io.github.ifa.glancewidget.glance.battery.component

import android.annotation.SuppressLint
import android.content.ComponentName
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.RectF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.github.ifa.glancewidget.MainActivity
import io.github.ifa.glancewidget.glance.battery.utils.GlanceCanvas
import io.github.ifa.glancewidget.model.DeviceType
import io.github.ifa.glancewidget.ui.theme.googleFlex400
import io.github.ifa.glancewidget.utils.Constants.ANDROID_SETTING_PACKAGE
import io.github.ifa.glancewidget.utils.Constants.BLUETOOTH_SETTING_CLASS

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

    val action = remember(deviceType) {
        if (deviceType == DeviceType.PHONE) {
            actionStartActivity<MainActivity>()
        } else {
            actionStartActivity(ComponentName(ANDROID_SETTING_PACKAGE, BLUETOOTH_SETTING_CLASS))
        }
    }

    Box(
        modifier = modifier.clickable(action),
        contentAlignment = Alignment.TopCenter,
    ) {
        GlanceCanvas(modifier = GlanceModifier.fillMaxSize()) {
            val rectF = RectF(x - radius, y - radius + 2f, x + radius, y + radius)
            val spaceStartAngle = if (isCharging) 48f else 24f
            val spaceSweepAngle = if (isCharging) 48f else 0f
            val totalSweep = 360f - spaceSweepAngle

            drawArc(
                rectF,
                246f + spaceStartAngle,
                totalSweep,
                false,
                Paint().apply {
                    color = backgroundColor.getColor(context).toArgb()
                    style = Paint.Style.STROKE
                    isAntiAlias = true
                    strokeWidth = 60f
                    strokeCap = Paint.Cap.ROUND
                }
            )

            drawArc(
                rectF,
                246f + spaceStartAngle,
                minOf(percent / 100f * totalSweep, totalSweep),
                false,
                Paint().apply {
                    color = foregroundColor.getColor(context).toArgb()
                    style = Paint.Style.STROKE
                    isAntiAlias = true
                    strokeWidth = 60f
                    strokeCap = Paint.Cap.ROUND
                }
            )

            if (isCharging) {
                val path = Path()
                val scale = 14f
                val cx = x
                val cy = y - radius + 8

                path.moveTo(cx + 1.5f * scale, cy - 3f * scale)
                path.lineTo(cx - 2.5f * scale, cy + 0.5f * scale)
                path.lineTo(cx - 0.5f * scale, cy + 0.5f * scale)
                path.lineTo(cx - 1.5f * scale, cy + 3f * scale)
                path.lineTo(cx + 2.5f * scale, cy - 0.5f * scale)
                path.lineTo(cx + 0.5f * scale, cy - 0.5f * scale)
                path.close()

                val paint = Paint().apply {
                    color = onBackgroundColor.getColor(context).toArgb()
                    isAntiAlias = true
                    style = Paint.Style.FILL
                    pathEffect = android.graphics.CornerPathEffect(6f)
                }

                drawPath(path, paint)
            }
        }

        Column(
            modifier = GlanceModifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val scale = if (percent > 0) scaleTextSize else scaleTextSize * 1.4f
            val textSize = if (deviceType == DeviceType.PHONE) 16 else 10
            if (percent > 0) {
                Text(
                    text = percent.toString(),
                    style = TextStyle(
                        color = if (transparency == 0f) {
                            GlanceTheme.colors.inversePrimary
                        } else {
                            GlanceTheme.colors.primary
                        },
                        fontSize = (textSize * scaleTextSize).sp,
                        fontFamily = FontFamily("monospace")
                    ),
                    fontWeight = FontWeight.Bold,
                    modifier = GlanceModifier.padding(bottom = 2.dp)
                )
            }
            if (deviceType != DeviceType.PHONE) Image(
                modifier = GlanceModifier.size((16 * scale).dp),
                provider = ImageProvider(deviceType.icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(ColorProvider(iconColor))
            )
        }

    }
}
