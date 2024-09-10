package io.github.ifa.glancewidget.presentation.widget.component

import android.annotation.SuppressLint
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.ui.component.SessionText
import io.github.ifa.glancewidget.utils.Constants.MAH_UNIT
import io.github.ifa.glancewidget.utils.Constants.MA_UNIT
import io.github.ifa.glancewidget.utils.Constants.WATT_UNIT
import io.github.ifa.glancewidget.utils.toHHMMSS

@SuppressLint("DefaultLocale")
@Composable
fun BatteryOverall(
    myDevice: MyDevice,
    extraBatteryInfo: ExtraBatteryInfo,
    batteryHealth: MyDevice.BatteryHealth,
    remainBatteryTime: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            SessionText(
                text = stringResource(id = R.string.battery_status),
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                startActivity(context, Intent(Intent.ACTION_POWER_USAGE_SUMMARY), null)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_monitoring),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            }
        }
        val remainTime = if (myDevice.isCharging) {
            stringResource(
                id = R.string.remain_time_charging,
                extraBatteryInfo.chargingTimeRemaining.toHHMMSS()
            )
        } else {
            stringResource(
                id = R.string.remain_time_battery,
                remainBatteryTime
            )
        }
        Text(
            text = remainTime,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 8.dp)
        )
        Row {
            with(myDevice) {
                BatteryItem(
                    deviceType = deviceType,
                    percent = level,
                    isCharging = isCharging,
                    deviceName = name,
                    isShowLargeLevel = true,
                    isTransparent = true,
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth(0.5f)
                )
                Column(modifier.padding(8.dp)) {
                    ShortInformationRow(
                        key = stringResource(id = if (isCharging) R.string.charging else R.string.discharging),
                        value = myDevice.chargeType.type
                    )
                    val power = extraBatteryInfo.powerInWatt(myDevice.voltage)
                    if (power > 0) {
                        ShortInformationRow(
                            key = stringResource(id = R.string.power),
                            value = "${String.format("%.2f", power)} $WATT_UNIT"
                        )
                    }
                    ShortInformationRow(
                        key = stringResource(id = R.string.current),
                        value = "${extraBatteryInfo.chargeCurrent} $MA_UNIT"
                    )
                }
            }
        }

        SessionText(
            text = stringResource(id = R.string.battery_information),
            modifier = Modifier.padding(8.dp)
        )
        Column {
            InformationRow(
                key = R.string.temperature, value = myDevice.temperature.formatTemperature()
            )
            InformationRow(key = R.string.technology, value = myDevice.technology)
            InformationRow(key = R.string.health, value = stringResource(id = batteryHealth.type))
            InformationRow(key = R.string.voltage, value = "${myDevice.voltage} V")
            InformationRow(
                key = R.string.design_capacity, value = "${extraBatteryInfo.capacity} $MAH_UNIT"
            )
            InformationRow(
                key = R.string.full_charge_capacity,
                value = "${extraBatteryInfo.fullChargeCapacity} $MAH_UNIT"
            )
            InformationRow(
                key = R.string.charge_counter, value = "${extraBatteryInfo.chargeCounter} $MAH_UNIT"
            )
            InformationRow(key = R.string.cycle_count, value = "${myDevice.cycleCount}")
        }
    }
}

@Composable
private fun ShortInformationRow(key: String, value: String) {
    Row {
        Text(
            text = key,
            modifier = Modifier.padding(bottom = 4.dp, end = 4.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = value,
            modifier = Modifier.padding(bottom = 4.dp),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
private fun InformationRow(@StringRes key: Int, value: String) {
    Row {
        Text(
            text = stringResource(id = key),
            modifier = Modifier
                .width(190.dp)
                .padding(8.dp),
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Preview
@Composable
fun BatteryOverallPreview() {
    BatteryOverall(
        myDevice = MyDevice.fromIntent(Intent()),
        extraBatteryInfo = ExtraBatteryInfo(4100, 100, 100),
        remainBatteryTime = "00:00:00",
        batteryHealth = MyDevice.BatteryHealth.GOOD
    )
}