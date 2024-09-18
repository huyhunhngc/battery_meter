package io.github.ifa.glancewidget.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import javax.inject.Inject

@AndroidEntryPoint
class BatteryAlertService: Service() {
    @Inject
    lateinit var batteryStateRepository: BatteryStateRepository

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            createNotificationChannel()
            startForeground(
                SERVICE_ID,
                NotificationCompat.Builder(this, CHANNEL_ID)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setOngoing(true)
                    .build()
            )
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, "Running App", importance)
        val notificationManager: NotificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val CHANNEL_ID = "on_foreground"
        private const val SERVICE_ID = 1234
    }
}