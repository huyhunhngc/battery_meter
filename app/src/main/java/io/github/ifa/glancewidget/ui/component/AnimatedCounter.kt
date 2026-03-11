package io.github.ifa.glancewidget.ui.component

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = MaterialTheme.colorScheme.tertiary,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    var oldInternalCount by remember { mutableIntStateOf(count) }

    SideEffect {
        oldInternalCount = count
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val countString = count.toString()
        val oldString = oldInternalCount.toString()
        val maxLength = maxOf(countString.length, oldString.length)

        val charList = remember(countString, oldString) {
            (0 until maxLength).map { i ->
                val newChar = countString.getOrNull(countString.length - 1 - i)
                val oldChar = oldString.getOrNull(oldString.length - 1 - i)

                if (newChar == oldChar) {
                    newChar to i
                } else {
                    newChar to i
                }
            }.reversed()
        }

        charList.forEach { (char, index) ->
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    if (count > oldInternalCount) {
                        (slideInVertically { it } + fadeIn()).togetherWith(slideOutVertically { -it } + fadeOut())
                    } else {
                        (slideInVertically { -it } + fadeIn()).togetherWith(slideOutVertically { it } + fadeOut())
                    }.using(
                        SizeTransform(clip = false)
                    )
                },
                label = "CounterAnimation"
            ) { targetChar ->
                Text(
                    text = targetChar?.toString() ?: "",
                    style = style,
                    color = color,
                    fontWeight = fontWeight,
                    softWrap = false
                )
            }
        }
    }
}