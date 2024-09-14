package io.github.ifa.glancewidget.presentation.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.presentation.settings.SettingsViewModel
import io.github.ifa.glancewidget.ui.component.TextWithImage

@Composable
fun ThemeSetting(
    onSelectTheme: (ThemeType) -> Unit,
    uiState: SettingsViewModel.SettingsScreenUiState
) {
    TextWithImage(
        text = stringResource(R.string.theme),
        image = painterResource(id = R.drawable.ic_palette)
    )
    Spacer(modifier = Modifier.height(16.dp))
    LazyVerticalGrid(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
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
}