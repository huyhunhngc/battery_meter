package io.github.ifa.glancewidget.glance.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import io.github.ifa.glancewidget.MainActivity
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.batteryPercent
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.isCharging
import io.github.ifa.glancewidget.model.BatteryData

class BatteryWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val batteryStatus = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_BATTERY_OKAY)
        }.let { filter ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(
                    BatteryWidgetReceiver(),
                    filter,
                    Context.RECEIVER_NOT_EXPORTED
                )
            } else {
                context.registerReceiver(BatteryWidgetReceiver(), filter)
            }
        }
        provideContent {
            val batteryData = batteryStatus?.let { BatteryData.fromIntent(it) }
            Content(batteryData)
        }
    }

    @Composable
    private fun Content(
        batteryData: BatteryData?
    ) {

        val percent = currentState(batteryPercent) ?: 100.0F
        val isCharging = currentState(isCharging) ?: false
        Column(
            modifier = GlanceModifier.fillMaxSize().background(GlanceTheme.colors.background),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$percent %",
                modifier = GlanceModifier.padding(12.dp)
            )
            Row(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    text = if (isCharging) "Charging" else "Not Charging",
                    onClick = actionStartActivity<MainActivity>()
                )
            }
        }
    }
}

//private suspend fun GlanceId.updateAppWidgetState(
//    context: Context,
//    batteryData: BatteryData?
//) {
//    BatteryWidget().update(
//        context = context,
//        id = this
//    )
//    this.
//    updateAppWidgetState(
//        context = context,
//        glanceId = this,
//    ) { preferences ->
//        preferences[BatteryMeterWidgetReceiver.lastUpdatedMillis] =
//            lastUpdatedMillis
//        preferences[BatteryMeterWidgetReceiver.batteryPercent] = batteryPercent
//
//    }
//}