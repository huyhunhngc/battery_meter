package io.github.ifa.glancewidget

import android.app.Application
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.EntryPoints
import dagger.hilt.android.HiltAndroidApp
import io.github.ifa.glancewidget.background.BatteryWidgetWorker
import io.github.ifa.glancewidget.di.HiltWorkerFactoryEntryPoint
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration = Configuration.Builder()
        .setWorkerFactory(
            EntryPoints.get(this, HiltWorkerFactoryEntryPoint::class.java).workerFactory()
        )
        .build()

    override fun onCreate() {
        super.onCreate()
        val periodicUpdateRequest =
            PeriodicWorkRequestBuilder<BatteryWidgetWorker>(15, TimeUnit.MINUTES)
                .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "BatteryWidgetMonitor",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicUpdateRequest
        )
    }
}