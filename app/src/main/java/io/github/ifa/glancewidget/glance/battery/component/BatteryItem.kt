package io.github.ifa.glancewidget.glance.battery.component

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import io.github.ifa.glancewidget.R

@Composable
fun BatteryItem(
    percent: Int, isCharging: Boolean, deviceName: String, modifier: GlanceModifier = GlanceModifier
) {
    Box(
        modifier = modifier.fillMaxWidth().height(60.dp).cornerRadius(16.dp)
            .background(GlanceTheme.colors.inversePrimary)
    ) {
        Row(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            for (i in 1..100 step 10) {
                Segment(i <= percent)
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.Start,
            modifier = GlanceModifier.fillMaxSize().padding(4.dp)
        ) {
            Text(text = deviceName, style = TextStyle(color = GlanceTheme.colors.onPrimary))
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(text = "$percent%", style = TextStyle(color = GlanceTheme.colors.onPrimary))
            if (isCharging) {
                Image(
                    provider = ImageProvider(R.drawable.ic_bolt),
                    contentDescription = "My image",
                )
            }

        }
    }
}

@Composable
private fun RowScope.Segment(isFilled: Boolean) {
    Box(
        modifier = GlanceModifier.defaultWeight().fillMaxHeight()
            .background(if (isFilled) GlanceTheme.colors.primary else GlanceTheme.colors.inversePrimary)
    ) {}
}
