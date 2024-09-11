package io.github.ifa.glancewidget.presentation.settings.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.ThemeType

@Composable
fun ThemeSettingItem(
    modifier: Modifier = Modifier,
    themeType: ThemeType,
    onSelectTheme: (ThemeType) -> Unit,
    selected: Boolean,
) {
    val selectedAlpha = if (selected) 1.0f else 0.5f
    val textColor = MaterialTheme.colorScheme.primary.copy(alpha = selectedAlpha)
    Surface(
        onClick = { onSelectTheme(themeType) },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = selectedAlpha)
        ),
        modifier = modifier.height(90.dp).fillMaxWidth(),
        tonalElevation = if (selected) 6.dp else 0.dp,
        contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = selectedAlpha)
    ) {
        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Icon(
                    themeType.themeIcon(), contentDescription = null, tint = textColor
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    themeType.themeName(),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun ThemeType.themeIcon(): Painter = when (this) {
    ThemeType.FOLLOW_SYSTEM -> painterResource(id = R.drawable.ic_contrast)
    ThemeType.DARK_THEME -> painterResource(id = R.drawable.ic_dark_mode)
    ThemeType.LIGHT_THEME -> painterResource(id = R.drawable.ic_light_mode)
}

@Composable
private fun ThemeType.themeName(): String = when (this) {
    ThemeType.FOLLOW_SYSTEM -> stringResource(R.string.follow_system)
    ThemeType.DARK_THEME -> stringResource(R.string.dark_theme)
    ThemeType.LIGHT_THEME -> stringResource(R.string.light_theme)
}

@Preview
@Composable
fun ThemeSettingItemSelectedPreview() {
    ThemeSettingItem(themeType = ThemeType.FOLLOW_SYSTEM, onSelectTheme = {}, selected = true)
}

@Preview
@Composable
fun ThemeSettingItemPreview() {
    ThemeSettingItem(themeType = ThemeType.FOLLOW_SYSTEM, onSelectTheme = {}, selected = false)
}