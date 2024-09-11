package io.github.ifa.glancewidget.presentation.widget

import android.app.Activity.RESULT_OK
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.AddWidgetParams
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.presentation.main.MainScreenTab
import io.github.ifa.glancewidget.presentation.widget.component.BatteryExtraInformation
import io.github.ifa.glancewidget.presentation.widget.component.BatteryItem
import io.github.ifa.glancewidget.presentation.widget.component.BatteryOverall
import io.github.ifa.glancewidget.presentation.widget.component.ConnectedDevice
import io.github.ifa.glancewidget.ui.component.AnimatedTextTopAppBar
import io.github.ifa.glancewidget.utils.findActivity
import kotlinx.coroutines.launch

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
        onMoreClick = {},
        onClickAddWidget = { params ->
            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, uiState.setupWidgetId)
            }
            viewModel.saveTransparentSettings(params.isTransparent, uiState.setupWidgetId)
            activity?.setResult(RESULT_OK, resultValue)
            activity?.finish()
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WidgetScreen(
    uiState: WidgetViewModel.WidgetScreenUiState,
    snackbarHostState: SnackbarHostState,
    isShowBottomSheet: Boolean = false,
    onMoreClick: () -> Unit,
    onDisMissBottomSheet: () -> Unit = {},
    onClickAddWidget: (AddWidgetParams) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            AnimatedTextTopAppBar(title = stringResource(id = MainScreenTab.Widget.label),
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = onMoreClick) {
                        Icon(
                            Icons.Filled.MoreVert, contentDescription = null
                        )
                    }
                })
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            batteryOverall(
                myDevice = uiState.batteryData.myDevice,
                extraBatteryInfo = uiState.extraBatteryInfo,
                remainBatteryTime = uiState.remainBatteryTime,
                modifier = Modifier.padding(16.dp)
            )
            batteryExtraInformation(
                myDevice = uiState.batteryData.myDevice,
                extraBatteryInfo = uiState.extraBatteryInfo,
                batteryHealth = uiState.batteryHealth,
                modifier = Modifier.padding(16.dp)
            )
            connectedDevices(
                batteryConnectedDevices = uiState.batteryData.batteryConnectedDevices,
                modifier = Modifier.padding(16.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddWidgetBottomSheet(
    uiState: WidgetViewModel.WidgetScreenUiState,
    padding: PaddingValues,
    onDisMiss: () -> Unit,
    onClickAddWidget: (AddWidgetParams) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var isTransparentSelected by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = {
            onDisMiss()
            scope.launch { sheetState.hide() }
        }, sheetState = sheetState
    ) {
        Text(
            text = stringResource(id = R.string.add_your_widget),
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            with(uiState.batteryData.myDevice) {
                BatteryItem(
                    deviceType = deviceType,
                    percent = level,
                    isCharging = isCharging ?: false,
                    deviceName = name,
                    isTransparent = isTransparentSelected,
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth(0.8f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(16.dp)
                    .clickable { isTransparentSelected = !isTransparentSelected },
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconToggleButton(
                    checked = isTransparentSelected,
                    onCheckedChange = { isTransparentSelected = !isTransparentSelected },
                    modifier = Modifier
                        .size(20.dp)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clip(CircleShape)
                ) {
                    AnimatedVisibility(
                        visible = isTransparentSelected, enter = scaleIn(), exit = scaleOut()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
                Text(
                    text = stringResource(id = R.string.show_transparent),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Button(
                onClick = {
                    onClickAddWidget(AddWidgetParams(isTransparent = isTransparentSelected))
                }, modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(16.dp)
            ) {
                Text(text = stringResource(id = R.string.add_widget))
            }
        }
    }
}