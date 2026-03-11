package io.github.ifa.glancewidget.features.settings.component

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
    Surface(
        onClick = { onSelectTheme(themeType) },
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(90.dp).fillMaxWidth(),
        tonalElevation = if (selected) 6.dp else 0.dp,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
    ) {
        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Icon(
                    themeType.themeIcon(), contentDescription = null
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    themeType.themeName(),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun ThemeType.themeIcon(): Painter = when (this) {
    ThemeType.FOLLOW_SYSTEM -> painterResource(id = R.drawable.ic_brightness_auto)
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