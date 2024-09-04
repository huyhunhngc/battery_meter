package io.github.ifa.glancewidget.presentation.widget.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.DeviceType

@Composable
fun BatteryItem(
    deviceType: DeviceType,
    percent: Int,
    isCharging: Boolean,
    deviceName: String,
    isTransparent: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (!isTransparent) MaterialTheme.colorScheme.background else Color.Transparent)
            .padding(8.dp)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .align(Alignment.CenterStart)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(percent / 100f)
                .align(Alignment.CenterStart)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.inversePrimary)
        )


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Image(
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 4.dp),
                painter = painterResource(deviceType.icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            ItemText(text = deviceName, modifier = Modifier.fillMaxWidth(0.45f))
            Spacer(modifier = Modifier.weight(1f))
            ItemText(text = "$percent%", modifier = Modifier.padding(end = 4.dp))
            if (isCharging) {
                Image(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(2.dp),
                    painter = painterResource(R.drawable.ic_bolt),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background)
                )
            }
        }
    }
}

@Composable
private fun ItemText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
    )
}

@Preview
@Composable
fun BatteryPreview() {
    BatteryItem(
        deviceType = DeviceType.PHONE,
        percent = 50,
        isCharging = true,
        deviceName = "Xiaomi 4545453333",
        isTransparent = true,
        modifier = Modifier
            .height(100.dp)
            .width(200.dp)
    )
}
