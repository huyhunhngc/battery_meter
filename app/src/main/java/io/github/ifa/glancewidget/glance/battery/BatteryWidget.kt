package io.github.ifa.glancewidget.glance.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import io.github.ifa.glancewidget.MainActivity
import io.github.ifa.glancewidget.data.batteryWidgetStore
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.utils.fromJson
import io.github.ifa.glancewidget.utils.setObject
import kotlinx.coroutines.flow.first

class BatteryWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val filter = IntentFilter().apply {
            BatteryWidgetReceiver.BATTERY_ACTIONS.forEach {
                addAction(it)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                BatteryWidgetReceiver(), filter, Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            context.registerReceiver(BatteryWidgetReceiver(), filter)
        }
        val batteryWidgetStore = context.batteryWidgetStore
        val initial = batteryWidgetStore.data.first()

        provideContent {
            val data by batteryWidgetStore.data.collectAsState(initial)
            Content(
                fromJson<BatteryData>(data[BATTERY_PREFERENCES])
            )
        }

    }

    suspend fun updateIfBatteryChanged(
        context: Context, glanceId: GlanceId, batteryData: BatteryData?
    ) {
        context.batteryWidgetStore.setObject(BATTERY_PREFERENCES, batteryData)
        update(context, glanceId)
    }

    @Composable
    private fun Content(
        battery: BatteryData?
    ) {
        val percent = battery?.batteryDevice?.level ?: 100
        val isCharging = battery?.batteryDevice?.isCharging ?: false

        Column(
            modifier = GlanceModifier.fillMaxSize().background(GlanceTheme.colors.background),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$percent %", modifier = GlanceModifier.padding(12.dp)
            )
            Row(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    text = if (isCharging) "Charging" else "Not Charging",
                    onClick = actionStartActivity<MainActivity>()
                )
            }
        }
    }

    companion object {
        val BATTERY_PREFERENCES = stringPreferencesKey("batteryData")
    }
}