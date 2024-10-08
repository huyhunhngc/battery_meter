package io.github.ifa.glancewidget.glance.battery.component

import android.content.ComponentName
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import io.github.ifa.glancewidget.MainActivity
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.DeviceType
import io.github.ifa.glancewidget.utils.Constants.ANDROID_SETTING_PACKAGE
import io.github.ifa.glancewidget.utils.Constants.BLUETOOTH_SETTING_CLASS

const val SUPPORTED_ROW_ELEMENTS = 10

@Composable
fun BatteryItem(
    deviceType: DeviceType,
    percent: Int,
    isCharging: Boolean,
    deviceName: String,
    fontSizeScale: Int = 14,
    modifier: GlanceModifier = GlanceModifier
) {
    val action = remember(deviceType) {
        if (deviceType == DeviceType.PHONE) {
            actionStartActivity<MainActivity>()
        } else {
            actionStartActivity(ComponentName(ANDROID_SETTING_PACKAGE, BLUETOOTH_SETTING_CLASS))
        }
    }

    Box(
        modifier = modifier
            .cornerRadius(16.dp)
            .background(GlanceTheme.colors.primaryContainer)
            .clickable(action)
    ) {
        Row(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            for (i in 0..90 step SUPPORTED_ROW_ELEMENTS) {
                Segment(currentSegment = i, percent)
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.Start,
            modifier = GlanceModifier.fillMaxSize().padding(8.dp)
        ) {
            Image(
                modifier = GlanceModifier.size(24.dp).padding(end = 4.dp),
                provider = ImageProvider(deviceType.icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(GlanceTheme.colors.primary)
            )
            Text(
                text = deviceName,
                style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = fontSizeScale.sp),
                fontWeight = FontWeight.Bold,
                modifier = GlanceModifier.defaultWeight()
            )

            if (percent > 0) {
                Text(
                    text = "$percent%",
                    style = TextStyle(color = GlanceTheme.colors.primary, fontSize = fontSizeScale.sp),
                    fontWeight = FontWeight.Bold,
                    modifier = GlanceModifier.padding(end = 4.dp)
                )
            }
            if (deviceType != DeviceType.PHONE) {
                Image(
                    modifier = GlanceModifier.size(16.dp),
                    provider = ImageProvider(if (percent > 0) R.drawable.ic_bluetooth_connected else R.drawable.ic_bluetooth),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.primary)
                )
            }

            if (isCharging) {
                Box(
                    modifier = GlanceModifier.size(16.dp).background(GlanceTheme.colors.primary)
                        .cornerRadius(8.dp), contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = GlanceModifier.size(12.dp),
                        provider = ImageProvider(R.drawable.ic_bolt),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.widgetBackground)
                    )
                }
            }
        }
    }
}

@Composable
private fun Text(
    text: String,
    style: TextStyle,
    fontWeight: FontWeight = FontWeight.Normal,
    modifier: GlanceModifier = GlanceModifier
) {
    Text(
        text = text,
        modifier = modifier,
        maxLines = 2,
        style = style.copy(fontWeight = fontWeight, color = GlanceTheme.colors.primary)
    )
}

@Composable
private fun RowScope.Segment(currentSegment: Int, percent: Int) {
    Box(
        modifier = GlanceModifier.defaultWeight().fillMaxHeight()
    ) {
        Row(modifier = GlanceModifier.fillMaxSize()) {
            for (i in 1..SUPPORTED_ROW_ELEMENTS) {
                val isFilled = i + currentSegment <= percent
                Spacer(
                    modifier = GlanceModifier.defaultWeight().fillMaxHeight().background(
                        if (isFilled) GlanceTheme.colors.inversePrimary else GlanceTheme.colors.secondaryContainer
                    )
                )
            }
        }
    }
}
