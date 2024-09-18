package io.github.ifa.glancewidget.service

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.wrapper.BatteryDataWrapper

class NotificationHandler(
    private val context: Context,
) {
    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(context)
    }

    private fun createChannelIfAbsent(channel: NotificationChannelCompat) {
        if (notificationManager.getNotificationChannel(channel.id) == null) {
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createStartMonitorNotification(): Notification {
        val channel = NotificationChannels.REMIND
        createChannelIfAbsent(channel)
        return NotificationCompat.Builder(context, channel.id)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .build()
    }

    @SuppressLint("MissingPermission")
    fun createBatteryMonitorNotification(batteryDataWrapper: BatteryDataWrapper) {
        val channel = NotificationChannels.REMIND
        createChannelIfAbsent(channel)
        val notification = NotificationCompat.Builder(context, channel.id)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setContentText(
                if (batteryDataWrapper.batteryData.myDevice.isCharging) {
                    batteryDataWrapper.remainChargeTime
                } else batteryDataWrapper.remainBatteryTime
            )
            .build()
        notificationManager.notify(1, notification)
    }
}

object NotificationChannels {
    val REMIND = NotificationChannelCompat.Builder(
        "remind-notification-channel-id",
        NotificationManagerCompat.IMPORTANCE_DEFAULT
    ).setName("Remind Notification").setShowBadge(false).build()
}