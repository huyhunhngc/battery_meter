package io.github.ifa.glancewidget.presentation.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.model.ThemeTypeColor
import io.github.ifa.glancewidget.presentation.settings.SettingsViewModel
import io.github.ifa.glancewidget.ui.component.SwitchWithDescription
import io.github.ifa.glancewidget.ui.component.TextWithImage
import io.github.ifa.glancewidget.ui.component.appPadding

@Composable
fun ThemeSetting(
    onSelectTheme: (ThemeType) -> Unit,
    onSelectThemeColor: (ThemeTypeColor) -> Unit,
    uiState: SettingsViewModel.SettingsScreenUiState
) {
    var syncEnabled by remember(uiState.syncColorEnabled) {
        mutableStateOf(uiState.syncColorEnabled)
    }
    TextWithImage(
        text = stringResource(R.string.theme),
        image = painterResource(id = R.drawable.ic_palette),
        modifier = Modifier.appPadding()
    )
    Spacer(modifier = Modifier.height(16.dp))
    SwitchWithDescription(
        label = stringResource(id = R.string.sync_color_scheme_with_widget),
        description = stringResource(id = R.string.sync_color_scheme_with_widget_description),
        onCheckedChange = scope@{ checked ->
            syncEnabled = checked
        },
        checked = syncEnabled,
        modifier = Modifier.appPadding()
    )
    Spacer(modifier = Modifier.height(16.dp))
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
                themeType = theme,
                onSelectTheme = onSelectTheme,
                selected = theme == uiState.theme
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    LazyRow(Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 16.dp)) {
        items(ThemeTypeColor.entries()) { item ->
            SelectablePaletteItem(
                modifier = Modifier.padding(2.dp).size(64.dp),
                themeTypeColor = item,
                onClick = onSelectThemeColor,
                isSelected = item == uiState.colorScheme,
            )
        }
    }
}