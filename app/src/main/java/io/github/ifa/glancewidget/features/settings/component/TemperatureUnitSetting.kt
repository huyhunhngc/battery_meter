package io.github.ifa.glancewidget.features.settings.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.features.settings.SettingsViewModel
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.utils.listItemColor
import io.github.ifa.glancewidget.utils.singleItemListItemShapes
import kotlinx.coroutines.launch

@SuppressLint("LocalContextConfigurationRead")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TemperatureUnitSetting(
    uiState: SettingsViewModel.SettingsScreenUiState,
    onSelectTemperatureUnit: (MyDevice.Temperature.TemperatureUnit) -> Unit
) {
    val selectedUnit = uiState.temperatureUnit ?: MyDevice.Temperature.TemperatureUnit.CELSIUS // Should mostly be populated by now, default string if not
    val sheetState = rememberModalBottomSheetState()
    val openBottomSheet = rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    SegmentedListItem(
        leadingContent = {
            Icon(painterResource(R.drawable.ic_device_thermostat), contentDescription = null)
        },
        supportingContent = {
            Text(
                text = if (selectedUnit == MyDevice.Temperature.TemperatureUnit.CELSIUS) {
                            stringResource(R.string.celsius_unit)
                       } else {
                            stringResource(R.string.fahrenheit_unit)
                       },
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        selected = openBottomSheet.value,
        shapes = ListItemDefaults.segmentedShapes(0, 1, singleItemListItemShapes),
        colors = listItemColor,
        onClick = {
            if (openBottomSheet.value) {
                scope.launch { sheetState.hide() }
            } else {
                openBottomSheet.value = true
                scope.launch { sheetState.show() }
            }
        },
    ) {
        Text(stringResource(R.string.temperature_unit))
    }

    if (openBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                openBottomSheet.value = false
                scope.launch { sheetState.hide() }
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            val units = MyDevice.Temperature.TemperatureUnit.entries
            units.forEach { unit ->
                val unitName = if (unit == MyDevice.Temperature.TemperatureUnit.CELSIUS) {
                    stringResource(R.string.celsius_unit)
                } else {
                    stringResource(R.string.fahrenheit_unit)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .padding(horizontal = 16.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            openBottomSheet.value = false
                            scope.launch { sheetState.hide() }
                            onSelectTemperatureUnit(unit)
                        }
                        .background(
                            MaterialTheme.colorScheme.surfaceContainerHigh,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 24.dp),
                ) {
                    Text(
                        text = unitName,
                        modifier = Modifier,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (unit == selectedUnit) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (unit == selectedUnit) {
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

@Preview(showBackground = true)
@Composable
fun TemperatureUnitSettingPreview() {
    MaterialTheme {
        TemperatureUnitSetting(
            uiState = SettingsViewModel.SettingsScreenUiState(
                temperatureUnit = MyDevice.Temperature.TemperatureUnit.CELSIUS
            ), onSelectTemperatureUnit = {})
    }
}
