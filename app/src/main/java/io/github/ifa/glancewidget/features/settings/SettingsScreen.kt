package io.github.ifa.glancewidget.features.settings

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.model.AppExtra
import io.github.ifa.glancewidget.model.AppIntent
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.model.ThemeTypeColor
import io.github.ifa.glancewidget.features.main.MainScreenTab
import io.github.ifa.glancewidget.features.settings.component.LanguageSetting
import io.github.ifa.glancewidget.features.settings.component.WidgetSettings
import io.github.ifa.glancewidget.features.settings.component.OtherSession
import io.github.ifa.glancewidget.features.settings.component.ThemeSetting
import io.github.ifa.glancewidget.ui.theme.topBarColors
import io.github.ifa.glancewidget.utils.Constants.IFA_LICENSES_URL
import io.github.ifa.glancewidget.utils.findActivity
import io.github.ifa.glancewidget.utils.isAppCompatLocaleDeprecated
import io.github.ifa.glancewidget.utils.mergePaddingValues
import io.github.ifa.glancewidget.utils.navigateLicencesScreen
import io.github.ifa.glancewidget.utils.navigateUrl
import io.github.ifa.glancewidget.utils.syncShowPairedDevicesToWidget
import io.github.ifa.glancewidget.utils.syncThemeColorToWidget
import io.github.ifa.glancewidget.utils.syncThemeToWidget

const val settingsScreenRoute = "settings_screen_route"

fun NavGraphBuilder.settingsScreen(
    contentPadding: PaddingValues,
    onOpenAboutScreen: () -> Unit,
) {
    composable(settingsScreenRoute) {
        SettingsScreen(
            contentPadding = contentPadding,
            onOpenAboutScreen = onOpenAboutScreen,
        )
    }
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    contentPadding: PaddingValues,
    onOpenAboutScreen: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    SettingsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        contentPadding = contentPadding,
        onOpenAboutScreen = onOpenAboutScreen,
        onSelectTheme = viewModel::setThemeType,
        onSelectThemeColor = viewModel::setThemeTypeColor,
        onEnableBlackDark = viewModel::setEnableBlackDark,
        onSelectLanguage = viewModel::setLanguage,
        onSetNotificationEnabled = viewModel::onBatteryAlertChanged,
        onSetShowPairedDevice = viewModel::onShowPairedDeviceChanged,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun SettingsScreen(
    uiState: SettingsViewModel.SettingsScreenUiState,
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    onOpenAboutScreen: () -> Unit,
    onSelectTheme: (ThemeType) -> Unit,
    onSelectThemeColor: (ThemeTypeColor) -> Unit,
    onEnableBlackDark: (Boolean) -> Unit,
    onSelectLanguage: (AppSettings.Language) -> Unit,
    onSetNotificationEnabled: (Boolean) -> Unit,
    onSetShowPairedDevice: (Boolean) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val onOpenLicensesScreen = {
        context.navigateLicencesScreen()
    }
    val onOpenPrivacyPolicy = {
        context.navigateUrl(IFA_LICENSES_URL)
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = MainScreenTab.Settings.label),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                subtitle = {},
                titleHorizontalAlignment = Alignment.CenterHorizontally,
                scrollBehavior = scrollBehavior,
                colors = topBarColors
            )
        },
        containerColor = topBarColors.containerColor,
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        val insets = mergePaddingValues(padding, contentPadding)
        LazyColumn(
            contentPadding = insets,
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isAppCompatLocaleDeprecated()) {
                item {
                    LanguageSetting(onSelectLanguage = onSelectLanguage, uiState = uiState)
                }
            }
            item {
                ThemeSetting(
                    onSelectTheme = { theme ->
                        onSelectTheme(theme)
                        context.syncThemeToWidget(theme)
                    },
                    onSelectThemeColor = { themeColor ->
                        onSelectThemeColor(themeColor)
                        context.syncThemeColorToWidget(themeColor)
                    },
                    onEnableBlackDark = {
                        onEnableBlackDark(it)
                    },
                    uiState = uiState
                )
            }
            item {
                WidgetSettings(
                    notificationSetting = uiState.notificationSetting,
                    syncColorEnabled = uiState.syncColorEnabled,
                    onSetNotificationEnabled = onSetNotificationEnabled,
                    onSetShowPairedDevice = { enabled ->
                        context.syncShowPairedDevicesToWidget(enabled)
                        onSetShowPairedDevice(enabled)
                    }
                )
            }

            item {
                OtherSession(onOpenAboutScreen, onOpenLicensesScreen, onOpenPrivacyPolicy)
            }
        }
    }
}
