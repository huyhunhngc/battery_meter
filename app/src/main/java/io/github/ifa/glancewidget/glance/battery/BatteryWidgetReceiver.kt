package io.github.ifa.glancewidget.glance.battery

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import dagger.hilt.android.AndroidEntryPoint
import io.github.ifa.glancewidget.model.BatteryData
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BatteryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget get() = BatteryWidget()

    private var batteryData: BatteryData? = null
    private val monitorBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action in BATTERY_ACTIONS) {
                batteryData = BatteryData.fromIntent(intent)
                observeData(context)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val filter = IntentFilter().apply {
            BATTERY_ACTIONS.forEach {
                addAction(it)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.applicationContext.registerReceiver(
                monitorBroadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            context.applicationContext.registerReceiver(monitorBroadcastReceiver, filter)
        }
    }

    private fun observeData(context: Context) {
        MainScope().launch {
            val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(BatteryWidget::class.java)
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(
                    context = context,
                    glanceId = glanceId,
                ) { _ ->
                    (glanceAppWidget as? BatteryWidget)?.updateIfBatteryChanged(
                        context, glanceId, batteryData
                    ) ?: run {
                        glanceAppWidget.update(context, glanceId)
                    }
                }
            }
        }
    }

    companion object {
        val BATTERY_ACTIONS = listOf(
            Intent.ACTION_BATTERY_CHANGED,
            Intent.ACTION_BATTERY_LOW,
            Intent.ACTION_POWER_DISCONNECTED,
            Intent.ACTION_POWER_CONNECTED,
            Intent.ACTION_BATTERY_OKAY
        )
    }
}