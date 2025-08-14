package io.github.ifa.glancewidget.background

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.ifa.glancewidget.glance.battery.BatteryWidget

@HiltWorker
class BatteryWidgetWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val glanceAppWidget = BatteryWidget()
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(glanceAppWidget::class.java)

            glanceIds.forEach { glanceId ->
                glanceAppWidget.update(context, glanceId)
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}