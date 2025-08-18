package io.github.ifa.glancewidget.presentation.widget.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.DeviceType

@Composable
fun CirCleBatteryItem(
    batteryLevel: Int,
    isCharging: Boolean,
    deviceType: DeviceType,
) {
    CircleProgressBar(
        progressPercentage = batteryLevel / 100f,
        isCharging = isCharging,
        value = batteryLevel.toString(),
        icon = deviceType.icon,
        modifier = Modifier.size(120.dp)
    )
}

@Composable
fun CircleProgressBar(
    progressPercentage: Float = 1.0f,
    isCharging: Boolean,
    value: String,
    icon: Int,
    modifier: Modifier
) {
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer
    val foregroundColor = MaterialTheme.colorScheme.inversePrimary
    val animateFloat = remember { Animatable(0.0f) }
    LaunchedEffect(progressPercentage) {
        animateFloat.animateTo(
            targetValue = progressPercentage,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
    }
    Box(
        contentAlignment = Alignment.Center, modifier = modifier
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.size(8.dp))
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
        if (isCharging) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bolt),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp)
                    .size(16.dp)
            )
        }

        Canvas(modifier = Modifier.fillMaxSize(0.8f)) {
            val strokeWidth = size.height * 0.06f
            val (startAngle, sweepAngle) = if (isCharging) {
                290f to 320f
            } else {
                270f to 360f
            }

            drawArc(
                color = backgroundColor,
                startAngle,
                sweepAngle,
                false,
                style = Stroke(strokeWidth.dp.toPx(), cap = StrokeCap.Round),
                size = Size(size.width, size.height)
            )
            drawArc(
                color = foregroundColor,
                startAngle,
                animateFloat.value * sweepAngle,
                false,
                style = Stroke(strokeWidth.dp.toPx(), cap = StrokeCap.Round),
                size = Size(size.width, size.height)
            )
        }
    }
}

@Preview
@Composable
fun CircleProgressBarPreview() {
    CircleProgressBar(
        progressPercentage = 0.5f,
        isCharging = false,
        value = "80",
        icon = R.drawable.ic_watch,
        modifier = Modifier.size(120.dp)
    )
}