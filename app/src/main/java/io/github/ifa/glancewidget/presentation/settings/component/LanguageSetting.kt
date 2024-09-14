package io.github.ifa.glancewidget.presentation.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.presentation.settings.SettingsViewModel
import io.github.ifa.glancewidget.ui.component.DropdownTextField
import io.github.ifa.glancewidget.ui.component.TextWithImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSetting(
    uiState: SettingsViewModel.SettingsScreenUiState,
    onSelectLanguage: (AppSettings.Language) -> Unit
) {
    val context = LocalContext.current
    val deviceLocale = context.resources.configuration.locales.get(0)
    val selectedLanguage = uiState.language ?: AppSettings.Language.fromCode(deviceLocale.language)
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextWithImage(
            text = stringResource(R.string.language),
            image = painterResource(id = R.drawable.ic_language),
            modifier = Modifier.weight(1f)
        )
        DropdownTextField(
            selectedValue = selectedLanguage.code,
            options = AppSettings.Language.options(),
            transformOption = { option ->
                stringResource(id = AppSettings.Language.fromCode(option).displayNameResId())
            },
            onValueChangedEvent = { selectedValue ->
                onSelectLanguage(AppSettings.Language.fromCode(selectedValue))
            },
        ) { selectedValue, expanded ->
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .width(150.dp)
                    .menuAnchor(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = selectedValue,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    null,
                    Modifier.rotate(if (expanded) 180f else 0f),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}