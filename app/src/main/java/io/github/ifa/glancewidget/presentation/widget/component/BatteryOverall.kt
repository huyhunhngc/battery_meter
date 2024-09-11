package io.github.ifa.glancewidget.presentation.widget.component

import android.annotation.SuppressLint
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.core.content.ContextCompat.startActivity
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.ui.component.CircleProgressBar
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
    remainBatteryTime: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val power = remember(extraBatteryInfo.chargeCurrent, myDevice.isCharging) {
        extraBatteryInfo.powerInWatt(myDevice.voltage, myDevice.isCharging)
    }
    val powerPercentage = remember(power) {
        power.toFloat() / 67.0f
    }
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

        with(myDevice) {
            BatteryItem(
                deviceType = deviceType,
                percent = level,
                isCharging = isCharging,
                deviceName = name,
                isShowLargeLevel = true,
                description = remainTime,
                isTransparent = true,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
            )
        }
        Row {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                CircleProgressBar(
                    modifier = Modifier.size(120.dp).padding(8.dp),
                    value = String.format("%.1f", power),
                    label = WATT_UNIT,
                    progressPercentage = powerPercentage
                )
            }

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (myDevice.isCharging) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Column {
                    ShortInformationRow(
                        key = stringResource(id = if (myDevice.isCharging) R.string.charging else R.string.discharging),
                        value = myDevice.chargeType.type
                    )
                    Text(
                        text = "${extraBatteryInfo.chargeCurrent} $MA_UNIT",
                        modifier = Modifier.padding(bottom = 4.dp),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_bolt),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }
    }
}

@Preview
@Composable
fun BatteryOverallPreview() {
    BatteryOverall(
        myDevice = MyDevice.fromIntent(Intent()).copy(isCharging = true, voltage = 4.5f),
        extraBatteryInfo = ExtraBatteryInfo(4100, 100, 100, chargeCurrent = 1200),
        remainBatteryTime = "00:00:00",
    )
}