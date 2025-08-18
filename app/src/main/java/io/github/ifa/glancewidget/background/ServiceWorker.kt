package io.github.ifa.glancewidget.background

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.github.ifa.glancewidget.utils.startBatteryStatus

class ServiceWorker(
    val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        context.startBatteryStatus()
        return Result.success()
    }
}