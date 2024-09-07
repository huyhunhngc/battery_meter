package io.github.ifa.glancewidget.glance.helper

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import io.github.ifa.glancewidget.model.WidgetSetting
import io.github.ifa.glancewidget.model.WidgetSettings

fun Context.getGlanceIdBy(appWidgetId: Int): GlanceId? {
    return kotlin.runCatching {

        GlanceAppWidgetManager(this).getGlanceIdBy(appWidgetId)
    }.getOrNull()
}

fun WidgetSettings.getSettingByGlance(context: Context, glanceId: GlanceId): WidgetSetting? {
    return kotlin.runCatching {
        val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(glanceId)
        settings[appWidgetId]
    }.getOrNull()
}