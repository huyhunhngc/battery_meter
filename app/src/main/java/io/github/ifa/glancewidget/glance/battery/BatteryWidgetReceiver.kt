package io.github.ifa.glancewidget.glance.battery

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BatteryWidgetReceiver: GlanceAppWidgetReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
    }
    override val glanceAppWidget: GlanceAppWidget
        get() = BatteryWidget()
}