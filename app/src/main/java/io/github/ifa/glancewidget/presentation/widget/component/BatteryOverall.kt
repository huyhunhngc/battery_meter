package io.github.ifa.glancewidget.presentation.widget.component

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.ui.component.SessionText

@Composable
fun BatteryOverall(
    myDevice: MyDevice, extraBatteryInfo: ExtraBatteryInfo, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp),
    ) {
        SessionText(
            text = stringResource(id = R.string.battery_status), modifier = Modifier.padding(8.dp)
        )
        Row {
            with(myDevice) {
                BatteryItem(
                    deviceType = deviceType,
                    percent = level,
                    isCharging = isCharging ?: false,
                    deviceName = name,
                    isShowLargeLevel = true,
                    isTransparent = true,
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth(0.5f)
                )
                Column(modifier.padding(8.dp)) {
                    Text(
                        text = if (isCharging == true) "Charging ${myDevice.chargeType.type}" else "Discharging",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Current 230mA",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Power 1.4W", color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        SessionText(
            text = stringResource(id = R.string.battery_information),
            modifier = Modifier.padding(8.dp)
        )
        Column {
            InformationRow(key = "Temperature", value = myDevice.temperature.toString())
            InformationRow(key = "Technology", value = myDevice.technology.toString())
            InformationRow(key = "Voltage", value = "${myDevice.voltage} V")
            InformationRow(key = "Capacity", value = "${extraBatteryInfo.capacity} mAh")
            InformationRow(key = "Full Charge Capacity", value = "${extraBatteryInfo.fullChargeCapacity} mAh")
            InformationRow(key = "Charge Counter", value = "${extraBatteryInfo.chargeCounter} mAh")
            InformationRow(key = "Cycle Count", value = "${myDevice.cycleCount}")
        }
    }
}

@Composable
private fun InformationRow(key: String, value: String) {
    Row {
        Text(
            text = key,
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
        extraBatteryInfo = ExtraBatteryInfo(4100, 100, 100)
    )
}