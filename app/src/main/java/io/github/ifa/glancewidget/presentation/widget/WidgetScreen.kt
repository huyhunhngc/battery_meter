package io.github.ifa.glancewidget.presentation.widget

import android.app.Activity.RESULT_OK
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.model.AddWidgetParams
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.presentation.main.MainScreenTab
import io.github.ifa.glancewidget.presentation.widget.component.AddWidgetBottomSheet
import io.github.ifa.glancewidget.presentation.widget.component.BatteryExtraInformation
import io.github.ifa.glancewidget.presentation.widget.component.BatteryOverall
import io.github.ifa.glancewidget.presentation.widget.component.ConnectedDevice
import io.github.ifa.glancewidget.presentation.widget.component.DropdownMenu
import io.github.ifa.glancewidget.ui.component.AnimatedTextTopAppBar
import io.github.ifa.glancewidget.ui.component.appPadding
import io.github.ifa.glancewidget.utils.findActivity
import io.github.ifa.glancewidget.utils.requestToPinWidget

const val widgetScreenRoute = "widget_screen_route"

fun NavGraphBuilder.widgetScreen() {
    composable(widgetScreenRoute) {
        WidgetScreen()
    }
}

@Composable
internal fun WidgetScreen(
    viewModel: WidgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showBottomSheet by rememberUpdatedState(uiState.setupWidgetId != INVALID_APPWIDGET_ID)
    val context = LocalContext.current
    val activity = context.findActivity()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        val intent = activity?.intent ?: return@LaunchedEffect
        viewModel.controlExtras(intent)
    }
    WidgetScreen(uiState = uiState,
        snackbarHostState = snackbarHostState,
        isShowBottomSheet = showBottomSheet,
        onDisMissBottomSheet = viewModel::hideBottomSheet,
        onClickAddWidget = { params ->
            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, uiState.setupWidgetId)
            }
            viewModel.saveTransparentSettings(params.isTransparent, uiState.setupWidgetId)
            activity?.setResult(RESULT_OK, resultValue)
            activity?.finish()
        },
        onRequestPiningWidget = viewModel::createWidget
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WidgetScreen(
    uiState: WidgetViewModel.WidgetScreenUiState,
    snackbarHostState: SnackbarHostState,
    isShowBottomSheet: Boolean = false,
    onDisMissBottomSheet: () -> Unit = {},
    onClickAddWidget: (AddWidgetParams) -> Unit,
    onRequestPiningWidget: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Appbar(scrollBehavior = scrollBehavior, onClickAddWidget = onRequestPiningWidget)
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .appPadding()
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            batteryOverall(
                myDevice = uiState.batteryData.myDevice,
                extraBatteryInfo = uiState.extraBatteryInfo,
                remainBatteryTime = uiState.remainBatteryTime,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            batteryExtraInformation(
                myDevice = uiState.batteryData.myDevice,
                extraBatteryInfo = uiState.extraBatteryInfo,
                batteryHealth = uiState.batteryHealth,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            connectedDevices(
                batteryConnectedDevices = uiState.batteryData.batteryConnectedDevices,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (isShowBottomSheet) {
            AddWidgetBottomSheet(
                uiState = uiState,
                padding = padding,
                onDisMiss = onDisMissBottomSheet,
                onClickAddWidget = onClickAddWidget
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Appbar(scrollBehavior: TopAppBarScrollBehavior, onClickAddWidget: () -> Unit) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    AnimatedTextTopAppBar(
        title = stringResource(id = MainScreenTab.Widget.label),
        scrollBehavior = scrollBehavior,
        actions = {
            IconButton(onClick = { dropdownExpanded = true }) {
                Icon(
                    Icons.Filled.MoreVert, contentDescription = null
                )
            }
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
                onAddWidgetClick = {
                    onClickAddWidget()
                    dropdownExpanded = false
                }
            )
        }
    )
}


private fun LazyListScope.batteryOverall(
    myDevice: MyDevice,
    extraBatteryInfo: ExtraBatteryInfo,
    remainBatteryTime: String,
    modifier: Modifier = Modifier
) {
    item {
        BatteryOverall(myDevice, extraBatteryInfo, remainBatteryTime, modifier)
    }
}

private fun LazyListScope.batteryExtraInformation(
    myDevice: MyDevice,
    extraBatteryInfo: ExtraBatteryInfo,
    batteryHealth: MyDevice.BatteryHealth,
    modifier: Modifier = Modifier
) {
    item {
        BatteryExtraInformation(myDevice, extraBatteryInfo, batteryHealth, modifier)
    }
}

private fun LazyListScope.connectedDevices(
    batteryConnectedDevices: List<BonedDevice>, modifier: Modifier = Modifier
) {
    if (batteryConnectedDevices.isNotEmpty()) {
        item {
            ConnectedDevice(batteryConnectedDevices, modifier)
        }
    }
}