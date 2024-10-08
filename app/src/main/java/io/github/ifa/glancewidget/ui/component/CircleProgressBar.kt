package io.github.ifa.glancewidget.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ifa.glancewidget.R
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircleProgressBar(
    progressPercentage: Float = 1.0f,
    value: String,
    label: String = "",
    modifier: Modifier
) {
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer
    val foregroundColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.tertiary
    val animateFloat = remember { Animatable(0.0f) }
    var parentSize by remember { mutableStateOf(IntSize.Zero) }
    LaunchedEffect(progressPercentage) {
        animateFloat.animateTo(
            targetValue = progressPercentage,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.onSizeChanged {
            parentSize = it
        }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                color = textColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge.scale(parentSize.width),
            )
            Image(
                painter = painterResource(id = R.drawable.ic_vital_signs),
                contentDescription = "",
                modifier = Modifier.size(parentSize.width.dp * 0.05f)
            )
        }

        Text(
            text = "LO",
            color = textColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall.scale(parentSize.width),
            modifier = Modifier
                .fillMaxHeight(0.2f)
                .align(Alignment.BottomStart)
        )
        Text(
            text = "HI",
            color = textColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall.scale(parentSize.width),
            modifier = Modifier
                .fillMaxHeight(0.2f)
                .align(Alignment.BottomEnd)
        )
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelMedium.scale(parentSize.width),
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        Canvas(modifier = Modifier.fillMaxSize(0.75f)) {
            val strokeWidth = size.height * 0.075f
            drawArc(
                color = backgroundColor,
                140f,
                260f,
                false,
                style = Stroke(strokeWidth.dp.toPx(), cap = StrokeCap.Round),
                size = Size(size.width, size.height)
            )
            drawArc(
                color = foregroundColor,
                140f,
                animateFloat.value * 260f,
                false,
                style = Stroke(strokeWidth.dp.toPx(), cap = StrokeCap.Round),
                size = Size(size.width, size.height)
            )
            val angleInDegrees = (animateFloat.value * 260.0) + 50.0
            val radius = (size.height / 2)
            val x = -(radius * sin(Math.toRadians(angleInDegrees))).toFloat() + (size.width / 2)
            val y = (radius * cos(Math.toRadians(angleInDegrees))).toFloat() + (size.height / 2)

            drawCircle(
                color = backgroundColor,
                radius = strokeWidth * 0.5f,
                center = Offset(x, y)
            )
        }
    }
}

fun TextStyle.scale(size: Int): TextStyle {
    val fontBase = 14
    val perStyle = fontSize.value / fontBase.toFloat()
    val scale = size.toFloat() / 25 * perStyle
    return copy(fontSize = scale.sp)
}

@Preview
@Composable
fun CircleProgressBarPreview() {
    CircleProgressBar(
        progressPercentage = 0.7f,
        value = "-448",
        label = "W",
        modifier = Modifier.size(120.dp)
    )
}
