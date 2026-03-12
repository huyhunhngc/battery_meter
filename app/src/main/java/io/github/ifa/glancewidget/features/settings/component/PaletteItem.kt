package io.github.ifa.glancewidget.features.settings.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.FormatColorFill
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.ThemeTypeColor
import io.github.ifa.glancewidget.ui.theme.getLightScheme

@Composable
fun SelectablePaletteItem(
    modifier: Modifier,
    themeTypeColor: ThemeTypeColor,
    isSelected: Boolean = false,
    onClick: (ThemeTypeColor) -> Unit = {}
) {
    if (themeTypeColor == ThemeTypeColor.System) {
        DefaultPaletteItem(modifier, themeTypeColor, isSelected, onClick)
    } else {
        PaletteItem(
            modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background)
                .clickable { onClick(themeTypeColor) }
                .padding(12.dp),
            getLightScheme(themeTypeColor.code),
            isSelected
        )
    }

}

@Composable
private fun DefaultPaletteItem(
    modifier: Modifier,
    themeTypeColor: ThemeTypeColor,
    isSelected: Boolean = false,
    onClick: (ThemeTypeColor) -> Unit = {}
) {
    val selectedShape by animateFloatAsState(
        targetValue = if (isSelected) 0.5f else 1.0f, label = "", animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    Box(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable { onClick(themeTypeColor) }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(selectedShape),
            imageVector = Icons.Rounded.FormatColorFill,
            contentDescription = "Dynamic",
            tint = MaterialTheme.colorScheme.primary.copy(alpha = selectedShape)
        )
    }
}

@Composable
fun PaletteItem(modifier: Modifier, colorScheme: ColorScheme, isSelected: Boolean) {
    val selectedShape by animateFloatAsState(
        targetValue = if (isSelected) 0.6f else 0.45f, label = "", animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    Box(
        modifier = modifier
            .clip(CircleShape)
            .aspectRatio(1.0f),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(colorScheme.primaryContainer)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(colorScheme.tertiaryContainer)
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxSize(selectedShape)
                .clip(CircleShape)
                .background(colorScheme.primary)
        )
        AnimatedVisibility(visible = isSelected, enter = scaleIn(), exit = scaleOut()) {
            Icon(
                modifier = Modifier.fillMaxSize(0.4f),
                imageVector = Icons.Default.Check,
                contentDescription = "Check",
                tint = colorScheme.onPrimary
            )
        }
    }
}

@Preview
@Composable
fun PaletteItemPreview() {
    SelectablePaletteItem(
        modifier = Modifier
            .height(200.dp)
            .width(100.dp),
        themeTypeColor = ThemeTypeColor.System,
        isSelected = true
    )
}

@Preview
@Composable
fun PaletteItemSelectedPreview() {
    SelectablePaletteItem(
        modifier = Modifier.size(100.dp),
        themeTypeColor = ThemeTypeColor.FireRed,
        isSelected = true
    )
}