package io.github.ifa.glancewidget.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.presentation.main.MainScreenTab
import io.github.ifa.glancewidget.presentation.settings.component.ThemeSettingItem
import io.github.ifa.glancewidget.ui.component.AnimatedTextTopAppBar
import io.github.ifa.glancewidget.ui.component.CancellableFloatingActionButton
import io.github.ifa.glancewidget.ui.component.DropdownTextField
import io.github.ifa.glancewidget.ui.component.SwitchWithDescription
import io.github.ifa.glancewidget.ui.component.TextWithImage
import io.github.ifa.glancewidget.ui.component.TextWithRightArrow
import io.github.ifa.glancewidget.ui.component.appPadding

const val settingsScreenRoute = "settings_screen_route"

fun NavGraphBuilder.settingsScreen(onOpenAboutScreen: () -> Unit) {
    composable(settingsScreenRoute) {
        SettingsScreen(onOpenAboutScreen = onOpenAboutScreen)
    }
}

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel(), onOpenAboutScreen: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    SettingsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onOpenAboutScreen = onOpenAboutScreen,
        onSelectTheme = viewModel::setThemeType,
        onSelectLanguage = viewModel::setLanguage,
        onSetNotificationEnabled = viewModel::onBatteryAlertChanged,
        onSetShowPairedDevice = viewModel::onShowPairedDeviceChanged,
        onApplySettings = viewModel::onApplySettings
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    uiState: SettingsViewModel.SettingsScreenUiState,
    snackbarHostState: SnackbarHostState,
    onOpenAboutScreen: () -> Unit,
    onSelectTheme: (ThemeType) -> Unit,
    onSelectLanguage: (AppSettings.Language) -> Unit,
    onSetNotificationEnabled: (Boolean) -> Unit,
    onSetShowPairedDevice: (Boolean) -> Unit,
    onApplySettings: (Boolean) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            AnimatedTextTopAppBar(
                title = stringResource(id = MainScreenTab.Settings.label),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = uiState.showApplyButton,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                CancellableFloatingActionButton(onApply = onApplySettings)
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .appPadding()
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            item {
                ThemeSetting(onSelectTheme = onSelectTheme, uiState = uiState)
            }
            item {
                LanguageSetting(onSelectLanguage = onSelectLanguage, uiState = uiState)
            }
            item {
                JobSetting(
                    notificationSetting = uiState.notificationSetting,
                    newSettings = uiState.newNotificationSetting,
                    onSetNotificationEnabled = onSetNotificationEnabled,
                    onSetShowPairedDevice = onSetShowPairedDevice
                )
            }
            item {
                OtherSession(onOpenAboutScreen)
            }
        }
    }
}

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

@Composable
fun JobSetting(
    notificationSetting: AppSettings.NotificationSetting,
    newSettings: AppSettings.NotificationSetting?,
    onSetNotificationEnabled: (Boolean) -> Unit,
    onSetShowPairedDevice: (Boolean) -> Unit,
) {
    var notificationEnabled by remember(
        key1 = notificationSetting.batteryAlert,
        key2 = newSettings?.batteryAlert
    ) {
        mutableStateOf(newSettings?.batteryAlert ?: notificationSetting.batteryAlert)
    }
    var showPairedDevice by remember(
        key1 = notificationSetting.showPairedDevices,
        key2 = newSettings?.showPairedDevices
    ) {
        mutableStateOf(newSettings?.showPairedDevices ?: notificationSetting.showPairedDevices)
    }
    TextWithImage(
        text = stringResource(R.string.notification_settings),
        image = painterResource(id = R.drawable.ic_notifications),
        modifier = Modifier.padding(vertical = 16.dp)
    )

    SwitchWithDescription(
        label = stringResource(id = R.string.battery_alert),
        description = stringResource(id = R.string.battery_alert_desc),
        onCheckedChange = { checked ->
            onSetNotificationEnabled(checked)
            notificationEnabled = checked
        },
        checked = notificationEnabled
    )

    Spacer(modifier = Modifier.height(16.dp))

    SwitchWithDescription(
        label = stringResource(id = R.string.show_paired_devices),
        description = stringResource(id = R.string.show_paired_devices_desc),
        onCheckedChange = { checked ->
            onSetShowPairedDevice(checked)
            showPairedDevice = checked
        },
        checked = showPairedDevice
    )
}

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

@Composable
fun OtherSession(onOpenAboutScreen: () -> Unit) {
    TextWithImage(
        text = stringResource(R.string.other),
        image = painterResource(id = R.drawable.ic_info),
        modifier = Modifier.padding(vertical = 16.dp)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 16.dp),
    ) {
        TextWithRightArrow(
            text = stringResource(id = R.string.about_tab),
            onClick = onOpenAboutScreen
        )
        HorizontalDivider(thickness = 0.5.dp)
        TextWithRightArrow(text = stringResource(id = R.string.license)) {

        }
        HorizontalDivider(thickness = 0.5.dp)
        TextWithRightArrow(text = stringResource(id = R.string.privacy_policy)) {

        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}
