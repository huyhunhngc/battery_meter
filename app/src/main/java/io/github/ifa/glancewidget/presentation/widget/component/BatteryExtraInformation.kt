package io.github.ifa.glancewidget.presentation.widget.component

import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import io.github.ifa.glancewidget.utils.Constants.MAH_UNIT

@Composable
fun BatteryExtraInformation(
    myDevice: MyDevice,
    extraBatteryInfo: ExtraBatteryInfo,
    batteryHealth: MyDevice.BatteryHealth,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp)
    ) {
        SessionText(
            text = stringResource(id = R.string.battery_information),
            modifier = Modifier.padding(horizontal = 8.dp).padding(top = 8.dp)
        )
        Column(modifier = Modifier.padding(8.dp)) {
            InformationRow(key = R.string.technology, value = myDevice.technology)
            HorizontalDivider()
            InformationRow(key = R.string.health, value = stringResource(id = batteryHealth.type))
            HorizontalDivider()
            InformationRow(
                key = R.string.design_capacity, value = "${extraBatteryInfo.capacity} $MAH_UNIT"
            )
            HorizontalDivider()
            InformationRow(
                key = R.string.full_charge_capacity,
                value = "${extraBatteryInfo.fullChargeCapacity} $MAH_UNIT"
            )
            HorizontalDivider()
            InformationRow(
                key = R.string.charge_counter, value = "${extraBatteryInfo.chargeCounter} $MAH_UNIT"
            )
            HorizontalDivider()
            InformationRow(key = R.string.cycle_count, value = "${myDevice.cycleCount}")
        }
    }
}

@Composable
private fun InformationRow(@StringRes key: Int, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = key),
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Preview
@Composable
fun BatteryExtraInformationPreview() {
    BatteryExtraInformation(
        myDevice = MyDevice.fromIntent(Intent()),
        extraBatteryInfo = ExtraBatteryInfo(4100, 100, 100, chargeCurrent = 1200),
        batteryHealth = MyDevice.BatteryHealth.GOOD
    )
}
