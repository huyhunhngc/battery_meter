package io.github.ifa.glancewidget.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.features.battery.component.BonedDeviceItem
import io.github.ifa.glancewidget.features.settings.component.WidgetSettings
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.BonnedDeviceSettings
import io.github.ifa.glancewidget.ui.component.AnimatedTextTopAppBar
import io.github.ifa.glancewidget.ui.theme.topBarColors
import io.github.ifa.glancewidget.utils.combinePadding

@Composable
fun WidgetSettingsScreen(
    uiState: SettingsViewModel.SettingsScreenUiState,
    contentPadding: PaddingValues,
    onSetDeviceShowInWidgetChanged: (String, Boolean) -> Unit,
    onNavigationIconClick: () -> Unit,
    onSetNotificationEnabled: (Boolean) -> Unit,
    onSetShowPairedDevice: (Boolean) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    WidgetSettingsScreenLayout(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        contentPadding = contentPadding,
        onSetDeviceShowInWidgetChanged = onSetDeviceShowInWidgetChanged,
        onNavigationIconClick = onNavigationIconClick,
        onSetNotificationEnabled = onSetNotificationEnabled,
        onSetShowPairedDevice = onSetShowPairedDevice
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun WidgetSettingsScreenLayout(
    uiState: SettingsViewModel.SettingsScreenUiState,
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    onSetDeviceShowInWidgetChanged: (String, Boolean) -> Unit,
    onSetNotificationEnabled: (Boolean) -> Unit,
    onSetShowPairedDevice: (Boolean) -> Unit,
    onNavigationIconClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            AnimatedTextTopAppBar(
                title = stringResource(id = R.string.widget_tab), navigationIcon = {
                    IconButton(
                        onClick = { onNavigationIconClick() },
                        colors = IconButtonDefaults.iconButtonColors().copy(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = "Back",
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        containerColor = topBarColors.containerColor,
    ) { padding ->
        val insets = combinePadding(padding, contentPadding)
        LazyColumn(
            contentPadding = insets,
            modifier = Modifier.fillMaxSize()
                .padding(top = 16.dp)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            item {
                WidgetSettings(
                    notificationSetting = uiState.notificationSetting,
                    syncColorEnabled = uiState.syncColorEnabled,
                    onSetNotificationEnabled = onSetNotificationEnabled,
                    onSetShowPairedDevice = onSetShowPairedDevice,
                )
            }

            connectedDevices(
                batteryConnectedDevices = uiState.batteryConnectedDevices,
                batteryDeviceSettings = uiState.bonedDeviceSettings,
                onShowInWidgetChanged = onSetDeviceShowInWidgetChanged
            )
        }
    }
}

private fun LazyListScope.connectedDevices(
    batteryConnectedDevices: List<BonedDevice>,
    batteryDeviceSettings: BonnedDeviceSettings,
    onShowInWidgetChanged: (String, Boolean) -> Unit,
) {
    if (batteryConnectedDevices.isNotEmpty()) {
        item {
            Text(
                text = stringResource(R.string.bluetooth_device_setting),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
            )
        }
        items(
            items = batteryConnectedDevices,
            key = { device -> device.address }
        ) { device ->
            val showInWidget = batteryDeviceSettings.settings[device.address]?.showInWidget ?: true
            BonedDeviceItem(
                device = device,
                showInWidget = showInWidget,
                onShowInWidgetChanged = onShowInWidgetChanged
            )
        }
    }
}