package io.github.ifa.glancewidget

import android.app.Application
import com.jaredrummler.android.device.DeviceName
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        DeviceName.init(this)
    }
}