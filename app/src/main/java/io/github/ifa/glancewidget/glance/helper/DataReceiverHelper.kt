package io.github.ifa.glancewidget.glance.helper

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context

object DataReceiverHelper {
	inline fun <reified WidgetClass>updateWidget(
		context: Context,
		callback: (AppWidgetManager, Int) -> Unit
	) {
		val appWidgetManager = AppWidgetManager.getInstance(context)
		val appWidgetIds = ComponentName(context, WidgetClass::class.java)
			.let { provider -> appWidgetManager.getAppWidgetIds(provider) }

		for (appWidgetId in appWidgetIds) {
			callback(appWidgetManager, appWidgetId)
		}
	}
}