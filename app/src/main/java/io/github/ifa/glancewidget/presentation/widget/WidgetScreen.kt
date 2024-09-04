package io.github.ifa.glancewidget.presentation.widget

import android.app.Activity.RESULT_OK
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.presentation.widget.component.BatteryItem
import io.github.ifa.glancewidget.utils.findActivity

const val widgetScreenRoute = "widget_screen_route"

fun NavGraphBuilder.widgetScreen() {
    composable(widgetScreenRoute) {
        WidgetScreen()
    }
}


@Composable
fun WidgetScreen(
    viewModel: WidgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()

    val showBottomSheet by rememberUpdatedState(uiState.setupWidgetId != INVALID_APPWIDGET_ID)
    LaunchedEffect(Unit) {
        val intent = activity?.intent ?: return@LaunchedEffect
        viewModel.controlExtras(intent)
    }
    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                with(uiState.batteryData.myDevice) {
                    BatteryItem(
                        deviceType = deviceType,
                        percent = level,
                        isCharging = isCharging ?: false,
                        deviceName = name,
                        isTransparent = true,
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth(0.8f)
                    )
                }
            }
        }


        if (showBottomSheet) {
            AddWidgetBottomSheet(uiState = uiState, padding = padding) {
                val resultValue = Intent().apply {
                    putExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID, uiState.setupWidgetId
                    )
                }
                activity?.setResult(RESULT_OK, resultValue)
                activity?.finish()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddWidgetBottomSheet(
    uiState: WidgetViewModel.WidgetScreenUiState,
    padding: PaddingValues,
    onClickAddWidget: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var isTransparentSelected by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = {
            //scope.launch { sheetState.hide() }
        }, sheetState = sheetState
    ) {
        Text(text = "Add your widget")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
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
                        .height(100.dp)
                        .fillMaxWidth(0.8f)
                )
            }
            IconToggleButton(
                checked = isTransparentSelected,
                onCheckedChange = { isTransparentSelected = !isTransparentSelected },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(24.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clip(CircleShape)
            ) {
                AnimatedVisibility(
                    visible = isTransparentSelected,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            Button(
                onClick = onClickAddWidget,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(0.85f)
            ) {
                Text(text = "Add Widget")
            }
        }

    }
}
