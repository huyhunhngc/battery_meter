package io.github.ifa.glancewidget.presentation.widget.component

import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f, label = ""
    )
    val contents = listOf(
        R.string.technology to myDevice.technology,
        R.string.health to stringResource(id = batteryHealth.type),
        R.string.design_capacity to "${extraBatteryInfo.capacity} $MAH_UNIT",
        R.string.full_charge_capacity to "${extraBatteryInfo.fullChargeCapacity} $MAH_UNIT",
        R.string.charge_counter to "${extraBatteryInfo.chargeCounter} $MAH_UNIT",
        R.string.cycle_count to myDevice.cycleCount.toString()
    )
    Column(
        modifier = modifier
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded }
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp)
        ) {
            SessionText(
                text = stringResource(id = R.string.battery_information),
                modifier = Modifier.weight(1f)
            )
            IconButton(
                modifier = Modifier
                    .rotate(rotationState),
                onClick = { expanded = !expanded }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Drop-Down Arrow"
                )
            }
        }

        if (expanded) {
            Column(modifier = Modifier.padding(8.dp)) {
                contents.forEachIndexed { index, (session, value) ->
                    InformationRow(key = session, value = value)
                    if (index != contents.lastIndex) {
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun InformationRow(@StringRes key: Int, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
