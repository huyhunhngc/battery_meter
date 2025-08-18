package io.github.ifa.glancewidget.presentation.settings.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.presentation.settings.SettingsViewModel
import io.github.ifa.glancewidget.ui.component.TextWithImage
import io.github.ifa.glancewidget.ui.component.appPadding
import kotlinx.coroutines.launch

@SuppressLint("LocalContextConfigurationRead")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSetting(
    uiState: SettingsViewModel.SettingsScreenUiState,
    onSelectLanguage: (AppSettings.Language) -> Unit
) {
    val context = LocalContext.current
    val deviceLocale = context.resources.configuration.locales.get(0)
    val selectedLanguage = uiState.language ?: AppSettings.Language.fromCode(deviceLocale.language)
    val sheetState = rememberModalBottomSheetState()
    val openBottomSheet = rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .appPadding()
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable {
                if (openBottomSheet.value) {
                    scope.launch { sheetState.hide() }
                } else {
                    openBottomSheet.value = true
                    scope.launch { sheetState.show() }
                }
            }
            .padding(16.dp)) {
        TextWithImage(
            text = stringResource(R.string.language),
            image = painterResource(id = R.drawable.ic_language),
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = stringResource(selectedLanguage.displayNameResId()),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

    }
    if (openBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                openBottomSheet.value = false
                scope.launch { sheetState.hide() }
            },
            sheetState = sheetState,
        ) {
            AppSettings.Language.options().forEach { languageCode ->
                val language = AppSettings.Language.fromCode(languageCode)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .padding(horizontal = 16.dp)
                        .height(56.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(16.dp))
                        .clickable {
                            openBottomSheet.value = false
                            scope.launch { sheetState.hide() }
                            onSelectLanguage(language)
                        }
                        .padding(horizontal = 24.dp),
                ) {
                    Text(
                        text = stringResource(id = language.displayNameResId()),
                        modifier = Modifier,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (language == selectedLanguage) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (language == selectedLanguage) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

            }
        }
    }
}