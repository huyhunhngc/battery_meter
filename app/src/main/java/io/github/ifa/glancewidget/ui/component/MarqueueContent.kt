package io.github.ifa.glancewidget.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun MarqueeText(text: String) {
    val textWidth = remember { Animatable(0f) }
    val screenWidth = 300f

    LaunchedEffect(Unit) {
        while (true) {
            textWidth.animateTo(
                targetValue = -screenWidth,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 5000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            textWidth.snapTo(screenWidth)
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        BasicText(
            text = text,
            style = TextStyle(color = Color.Black, fontSize = 24.sp),
            modifier = Modifier.offset {
                IntOffset(textWidth.value.toInt(), 0)
            }
        )
    }
}

@Composable
fun MarqueeImage(painter: Painter,  imageWidth: Float, contentDescription: String?, modifier: Modifier) {
    val totalWidth = imageWidth
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            offsetX.animateTo(
                targetValue = -totalWidth,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 500, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            offsetX.snapTo(0f) // Đưa về vị trí ban đầu
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = offsetX.value.dp)
    ) {
        // Hiển thị hình ảnh đầu tiên
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(imageWidth.dp),
        )

        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(imageWidth.dp),
        )
    }
}
