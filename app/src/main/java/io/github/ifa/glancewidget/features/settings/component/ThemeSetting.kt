package io.github.ifa.glancewidget.features.settings.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.features.settings.SettingsViewModel
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.model.ThemeTypeColor
import io.github.ifa.glancewidget.ui.component.SwitchWithDescription
import io.github.ifa.glancewidget.ui.component.appPadding

@Composable
fun ThemeSetting(
    onSelectTheme: (ThemeType) -> Unit,
    onSelectThemeColor: (ThemeTypeColor) -> Unit,
    onEnableBlackDark: (Boolean) -> Unit,
    uiState: SettingsViewModel.SettingsScreenUiState
) {
    var isBlackDarkEnabled by remember(uiState.isBlackDarkEnabled) {
        mutableStateOf(uiState.isBlackDarkEnabled)
    }
    val isDarkTheme = when (uiState.theme) {
        ThemeType.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        ThemeType.DARK_THEME -> true
        ThemeType.LIGHT_THEME -> false
    }
    LazyVerticalGrid(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .appPadding()
            .fillMaxWidth()
            .height(96.dp),
        columns = GridCells.Adaptive(minSize = 96.dp)
    ) {
        items(ThemeType.entries) { theme ->
            ThemeSettingItem(
                themeType = theme, onSelectTheme = onSelectTheme, selected = theme == uiState.theme
            )
        }
    }
    SwitchWithDescription(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp)),
        icon = Icons.Rounded.Contrast,
        enabled = isDarkTheme,
        label = stringResource(id = R.string.black_dark),
        description = stringResource(id = R.string.black_dark_description),
        onCheckedChange = scope@{ checked ->
            isBlackDarkEnabled = checked
            onEnableBlackDark(checked)
        },
        checked = isBlackDarkEnabled,
    )
    LazyRow(Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 16.dp)) {
        items(ThemeTypeColor.entries()) { item ->
            SelectablePaletteItem(
                modifier = Modifier
                    .padding(2.dp)
                    .size(64.dp),
                themeTypeColor = item,
                onClick = onSelectThemeColor,
                isSelected = item == uiState.colorScheme,
            )
        }
    }
}