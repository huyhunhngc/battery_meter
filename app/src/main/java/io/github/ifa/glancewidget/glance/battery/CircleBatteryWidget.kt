package io.github.ifa.glancewidget.glance.battery

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent

class CircleBatteryWidget: GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            CircleBattery()
        }
    }

    @Composable
    private fun CircleBattery() {

    }
}