package io.github.ifa.glancewidget.features.widget.component

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.ChartRecord
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.model.wrapper.BatteryDataWrapper
import io.github.ifa.glancewidget.features.widget.wattsmonitor.WattsDetailDestination
import io.github.ifa.glancewidget.ui.component.SessionText
import io.github.ifa.glancewidget.ui.theme.containerColorAlpha60
import io.github.ifa.glancewidget.utils.Constants.MA_UNIT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BatteryOverall(
    modifier: Modifier = Modifier,
    batteryDataWrapper: BatteryDataWrapper,
    chartTrackingData: ChartRecord,
    onOpenWattsDetailScreen: (WattsDetailDestination) -> Unit = {}
) {
    val myDevice = batteryDataWrapper.batteryData.myDevice
    val extraBatteryInfo = batteryDataWrapper.extraBatteryInfo
    val power = remember(extraBatteryInfo.chargeCurrent) {
        extraBatteryInfo.powerInWatt(myDevice.voltage)
    }
    val powerPercentage = remember(power) {
        power.toFloat() / extraBatteryInfo.maxWattsChargeInput
    }
    val context = LocalContext.current
    val remainTimeLabel = if (myDevice.isCharging && myDevice.level < 100) {
        stringResource(id = R.string.remain_time_charging)
    } else {
        stringResource(id = R.string.remain_time_battery)
    }
    val remainTime = if (myDevice.isCharging && myDevice.level < 100) {
        batteryDataWrapper.remainChargeTime(context)
    } else {
        batteryDataWrapper.remainBatteryTime(context)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Header(Modifier.fillMaxWidth())
        Row(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BatteryItem(
                deviceType = myDevice.deviceType,
                percent = myDevice.level,
                isCharging = myDevice.isCharging,
                deviceName = myDevice.name,
                isShowLargeLevel = true,
                isTransparent = true,
                modifier = Modifier.weight(55f)
            )
            CurrentAndChargingMonitor(
                modifier = Modifier.weight(45f),
                isCharging = myDevice.isCharging,
                chargeType = myDevice.chargeType,
                chargeCurrent = extraBatteryInfo.getChargeDisChargeCurrent(myDevice.isCharging)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WattsMonitor(
                modifier = Modifier
                    .size(120.dp)
                    .clickable {
                        onOpenWattsDetailScreen(
                            WattsDetailDestination(
                                power = power.toFloat(),
                                powerPercentage = powerPercentage
                            )
                        )
                    },
                power = power.toFloat(),
                powerPercentage = powerPercentage
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = remainTimeLabel,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                )
                Text(
                    text = remainTime,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painterResource(R.drawable.ic_timelapse),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.BottomEnd),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TemperatureMonitor(
                modifier = Modifier.weight(1f),
                temperature = myDevice.temperature,
                temperatureTracking = chartTrackingData.temperatures
            )
            VoltageMonitor(
                modifier = Modifier.weight(1f),
                voltage = myDevice.voltage,
                voltageTracking = chartTrackingData.voltages
            )
        }

    }
}

@Composable
private fun Header(
    modifier: Modifier
) {
    val context = LocalContext.current
    Row(modifier = modifier) {
        SessionText(
            text = stringResource(id = R.string.battery_status),
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = {
                context.startActivity(Intent(Intent.ACTION_POWER_USAGE_SUMMARY))
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_monitoring),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun CurrentAndChargingMonitor(
    modifier: Modifier,
    isCharging: Boolean,
    chargeType: MyDevice.ChargeType,
    chargeCurrent: Int,
) {
    val chargeIcon = when (chargeType) {
        MyDevice.ChargeType.AC -> R.drawable.ic_power
        MyDevice.ChargeType.USB -> R.drawable.ic_usb
        MyDevice.ChargeType.WIRELESS -> R.drawable.ic_lightning_stand
        else -> R.drawable.ic_power_off
    }
    Box(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isCharging) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ShortInformationRow(
                key = stringResource(id = if (isCharging) R.string.charging else R.string.discharging),
                value = chargeType.type
            )
            Text(
                text = chargeCurrent.toString(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = MA_UNIT,
                modifier = Modifier.padding(bottom = 4.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        AnimatedContent(
            targetState = chargeIcon,
            label = "",
            modifier = Modifier.align(Alignment.BottomEnd),
        ) { chargeIcon ->
            Icon(
                painter = painterResource(id = chargeIcon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp)

            )
        }
    }
}

@Composable
private fun TemperatureMonitor(
    modifier: Modifier,
    temperature: MyDevice.Temperature,
    temperatureTracking: List<Float>
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(temperatureTracking) {
        withContext(Dispatchers.Default) {
            modelProducer.runTransaction {
                lineSeries { series(temperatureTracking.ifEmpty { listOf(temperature.temperature) }) }
            }
        }
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .aspectRatio(1.0f)
            .background(MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        LineChart(
            modelProducer = modelProducer,
            modifier = Modifier.padding(top = 32.dp),
            minY = 25.0,
            maxY = 50.0
        )
        Text(
            text = temperature.formatTemperature(),
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_device_thermostat),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(8.dp)
                .size(24.dp)
                .align(Alignment.TopEnd)
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun VoltageMonitor(
    modifier: Modifier,
    voltage: Float,
    voltageTracking: List<Float>,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(voltageTracking) {
        withContext(Dispatchers.Default) {
            modelProducer.runTransaction {
                lineSeries { series(voltageTracking.ifEmpty { listOf(voltage) }) }
            }
        }
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .aspectRatio(1.0f)
            .background(MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        LineChart(
            modelProducer = modelProducer,
            modifier = Modifier.padding(top = 32.dp),
            minY = 1.0,
            maxY = 5.0
        )
        Text(
            text = String.format("%.2f", voltage) + " V",
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_vital_signs),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(8.dp)
                .size(24.dp)
                .align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun LineChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier,
    minY: Double,
    maxY: Double
) {
    val marker = rememberMarker()
    val lineColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(
                            remember(lineColor) { LineCartesianLayer.LineFill.single(fill(lineColor)) }
                        )
                    ),
                    rangeProvider = remember {
                        CartesianLayerRangeProvider.fixed(minY = minY, maxY = maxY)
                    },
                    pointSpacing = 1.dp
                ),
                marker = marker,
            ),
        modelProducer = modelProducer,
        modifier = modifier,
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )
}


@Preview
@Composable
fun BatteryOverallPreview() {
    BatteryOverall(
        batteryDataWrapper = BatteryDataWrapper(),
        chartTrackingData = ChartRecord()
    )
}