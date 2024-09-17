package io.github.ifa.glancewidget

import android.app.Application
import androidx.work.Configuration
import dagger.hilt.EntryPoints
import dagger.hilt.android.HiltAndroidApp
import io.github.ifa.glancewidget.di.HiltWorkerFactoryEntryPoint

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration = Configuration.Builder()
        .setWorkerFactory(
            EntryPoints.get(this, HiltWorkerFactoryEntryPoint::class.java).workerFactory()
        )
        .build()
}