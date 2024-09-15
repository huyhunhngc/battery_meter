package io.github.ifa.glancewidget.presentation.widget.component

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import io.github.ifa.glancewidget.utils.Constants.MA_UNIT
import io.github.ifa.glancewidget.utils.Constants.WATT_UNIT
import io.github.ifa.glancewidget.utils.toHHMMSS

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BatteryOverall(
    myDevice: MyDevice,
    extraBatteryInfo: ExtraBatteryInfo,
    remainBatteryTime: String,
    modifier: Modifier = Modifier
) {
    val power = remember(extraBatteryInfo.chargeCurrent, myDevice.isCharging) {
        extraBatteryInfo.powerInWatt(myDevice.voltage, myDevice.isCharging)
    }
    val powerPercentage = remember(power) {
        power.toFloat() / extraBatteryInfo.maxWattsChargeInput
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

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp)
    ) {
        Header(Modifier.fillMaxWidth())
        BatteryItem(
            deviceType = myDevice.deviceType,
            percent = myDevice.level,
            isCharging = myDevice.isCharging,
            deviceName = myDevice.name,
            isShowLargeLevel = true,
            description = remainTime,
            isTransparent = true,
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
        )
        WattsMonitor(
            modifier = Modifier.padding(8.dp),
            power = power.toFloat(),
            powerPercentage = powerPercentage
        )
        CurrentAndChargingMonitor(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f),
            isCharging = myDevice.isCharging,
            chargeType = myDevice.chargeType,
            chargeCurrent = extraBatteryInfo.getChargeDisChargeCurrent(myDevice.isCharging)
        )
        TemperatureMonitor(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.5f),
            temperature = myDevice.temperature
        )
        VoltageMonitor(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f),
            voltage = myDevice.voltage
        )
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
}

@SuppressLint("DefaultLocale")
@Composable
private fun WattsMonitor(
    modifier: Modifier,
    power: Float,
    powerPercentage: Float
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
    ) {
        CircleProgressBar(
            modifier = Modifier
                .size(120.dp)
                .padding(8.dp),
            value = String.format("%.1f", power),
            label = WATT_UNIT,
            progressPercentage = powerPercentage
        )
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
            .background(if (isCharging) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ShortInformationRow(
                key = stringResource(id = if (isCharging) R.string.charging else R.string.discharging),
                value = chargeType.type
            )
            Text(
                text = "$chargeCurrent $MA_UNIT",
                modifier = Modifier.padding(bottom = 4.dp),
                fontWeight = FontWeight.Bold,
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
private fun TemperatureMonitor(modifier: Modifier, temperature: MyDevice.Temperature) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .height(90.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
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
                .align(Alignment.BottomEnd)
        )
    }
}

@Composable
private fun VoltageMonitor(modifier: Modifier, voltage: Float) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .height(90.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "$voltage V",
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
                .align(Alignment.BottomEnd)
        )
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