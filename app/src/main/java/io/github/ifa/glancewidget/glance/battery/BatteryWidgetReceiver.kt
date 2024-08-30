package io.github.ifa.glancewidget.glance.battery

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import dagger.hilt.android.AndroidEntryPoint
import io.github.ifa.glancewidget.model.BatteryData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BatteryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget get() = BatteryWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    private var batteryData: BatteryData? = null

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d("!@#", "onReceive: ${intent.action}")
        batteryData = BatteryData.fromIntent(intent)
        observeData(context)
    }

    private fun observeData(context: Context) {
        MainScope().launch {
            val glanceIds =
                GlanceAppWidgetManager(context).getGlanceIds(BatteryWidget::class.java)
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(
                    context = context,
                    glanceId = glanceId,
                ) { preferences ->
                    preferences[batteryPercent] = (batteryData?.batteryDevice?.level ?: 0).toFloat()
                    preferences[isCharging] = batteryData?.batteryDevice?.isCharging ?: false
                    glanceAppWidget.update(
                        context = context,
                        id = glanceId
                    )
                }
            }
        }

    }

    companion object {
        val batteryPercent = floatPreferencesKey("batteryPercent")
        val isCharging = booleanPreferencesKey("isCharging")
    }
}