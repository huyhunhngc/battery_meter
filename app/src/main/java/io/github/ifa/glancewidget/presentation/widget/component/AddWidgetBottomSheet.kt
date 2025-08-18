package io.github.ifa.glancewidget.presentation.widget.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.AddWidgetParams
import io.github.ifa.glancewidget.model.WidgetSetting
import io.github.ifa.glancewidget.presentation.widget.WidgetViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWidgetBottomSheet(
    uiState: WidgetViewModel.WidgetScreenUiState,
    onDisMiss: () -> Unit,
    onClickAddWidget: (AddWidgetParams) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    var widgetSelectionType by remember { mutableStateOf(WidgetSelectionType.Horizontal) }
    ModalBottomSheet(
        onDismissRequest = {
            onDisMiss()
            scope.launch { sheetState.hide() }
        },
        sheetState = sheetState
    ) {
        Text(
            text = stringResource(id = R.string.add_your_widget),
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            SelectedBox(
                isSelected = widgetSelectionType == WidgetSelectionType.Horizontal,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 24.dp),
                onSelected = {
                    widgetSelectionType = WidgetSelectionType.Horizontal
                }
            ) {
                with(uiState.batteryOverall.batteryData.myDevice) {
                    BatteryItem(
                        deviceType = deviceType,
                        percent = level,
                        isCharging = isCharging,
                        deviceName = "",
                        isTransparent = false,
                        modifier = Modifier
                            .height(90.dp)
                            .fillMaxWidth(0.75f)
                    )
                }
            }
            SelectedBox(
                isSelected = widgetSelectionType == WidgetSelectionType.HorizontalTransparent,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 24.dp),
                onSelected = {
                    widgetSelectionType = WidgetSelectionType.HorizontalTransparent
                }
            ) {
                with(uiState.batteryOverall.batteryData.myDevice) {
                    BatteryItem(
                        deviceType = deviceType,
                        percent = level,
                        isCharging = isCharging,
                        deviceName = "",
                        isTransparent = true,
                        modifier = Modifier
                            .height(96.dp)
                            .fillMaxWidth(0.78f)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SelectedBox(
                    isSelected = widgetSelectionType == WidgetSelectionType.Circle,
                    modifier = Modifier.weight(1f).padding(top = 16.dp, start = 24.dp),
                    onSelected = { widgetSelectionType = WidgetSelectionType.Circle }
                ) {
                    with(uiState.batteryOverall.batteryData.myDevice) {
                        CirCleBatteryItem(
                            batteryLevel = level,
                            isCharging = isCharging,
                            deviceType = deviceType,
                            transparent = false,
                        )
                    }
                }
                SelectedBox(
                    isSelected = widgetSelectionType == WidgetSelectionType.CircleTransparent,
                    modifier = Modifier.weight(1f).padding(top = 16.dp, end = 24.dp),
                    onSelected = { widgetSelectionType = WidgetSelectionType.CircleTransparent }
                ) {
                    with(uiState.batteryOverall.batteryData.myDevice) {
                        CirCleBatteryItem(
                            batteryLevel = level,
                            isCharging = isCharging,
                            deviceType = deviceType,
                            transparent = true,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onClickAddWidget(
                        AddWidgetParams(
                            isTransparent = widgetSelectionType.isTransparent(),
                            widgetStyle = when (widgetSelectionType) {
                                WidgetSelectionType.Circle,
                                WidgetSelectionType.CircleTransparent -> WidgetSetting.Style.Circle
                                WidgetSelectionType.Horizontal,
                                WidgetSelectionType.HorizontalTransparent -> WidgetSetting.Style.Horizontal
                            }
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.add_widget),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

enum class WidgetSelectionType {
    Circle, CircleTransparent, Horizontal, HorizontalTransparent;
     fun isTransparent(): Boolean {
         return this == HorizontalTransparent || this == CircleTransparent
     }
}

@Composable
private fun SelectedBox(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onSelected: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val borderWidth by animateFloatAsState(
        targetValue = if (isSelected) 2f else 0f,
        label = "Border Width",
    )
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
    }
    Box(
        modifier = modifier
            .border(
                width = borderWidth.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelected() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}