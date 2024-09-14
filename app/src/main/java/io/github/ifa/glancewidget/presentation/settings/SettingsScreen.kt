package io.github.ifa.glancewidget.presentation.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import io.github.ifa.glancewidget.BuildConfig
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.presentation.main.MainScreenTab
import io.github.ifa.glancewidget.presentation.settings.component.LanguageSetting
import io.github.ifa.glancewidget.presentation.settings.component.NotificationSetting
import io.github.ifa.glancewidget.presentation.settings.component.OtherSession
import io.github.ifa.glancewidget.presentation.settings.component.ThemeSetting
import io.github.ifa.glancewidget.presentation.settings.component.ThemeSettingItem
import io.github.ifa.glancewidget.ui.component.AnimatedTextTopAppBar
import io.github.ifa.glancewidget.ui.component.AppAlertDialog
import io.github.ifa.glancewidget.ui.component.DropdownTextField
import io.github.ifa.glancewidget.ui.component.SwitchWithDescription
import io.github.ifa.glancewidget.ui.component.TextWithImage
import io.github.ifa.glancewidget.ui.component.TextWithRightArrow
import io.github.ifa.glancewidget.ui.component.appPadding
import io.github.ifa.glancewidget.utils.BluetoothPermissions
import io.github.ifa.glancewidget.utils.checkPermissions

const val settingsScreenRoute = "settings_screen_route"

val ApplicationDetailsSettingsIntent by lazy {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
    }
}

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
