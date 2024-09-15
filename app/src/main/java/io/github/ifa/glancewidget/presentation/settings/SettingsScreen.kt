package io.github.ifa.glancewidget.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.presentation.main.MainScreenTab
import io.github.ifa.glancewidget.presentation.settings.component.LanguageSetting
import io.github.ifa.glancewidget.presentation.settings.component.NotificationSetting
import io.github.ifa.glancewidget.presentation.settings.component.OtherSession
import io.github.ifa.glancewidget.presentation.settings.component.ThemeSetting
import io.github.ifa.glancewidget.ui.component.AnimatedTextTopAppBar
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
                NotificationSetting(
                    notificationSetting = uiState.notificationSetting,
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
