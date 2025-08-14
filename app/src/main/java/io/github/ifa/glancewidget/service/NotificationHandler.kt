package io.github.ifa.glancewidget.service

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.BatteryMeterNotification
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.model.wrapper.BatteryDataWrapper
import io.github.ifa.glancewidget.service.BatteryStatusService.Companion.SERVICE_ID
import javax.inject.Inject

class NotificationHandler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(context)
    }

    private fun createChannelIfAbsent(channel: NotificationChannelCompat) {
        if (notificationManager.getNotificationChannel(channel.id) == null) {
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createStartMonitorNotification(myDevice: MyDevice?): Notification {
        val channel = NotificationChannels.BatteryStatus
        createChannelIfAbsent(channel)
        return createBatteryMonitorNotification(
            batteryNotificationData = BatteryNotificationData(
                channelId = channel.id,
                level = myDevice?.level ?: 0,
                isCharging = myDevice?.isCharging ?: false,
                temperature = myDevice?.temperature
            ),
            priority = NotificationCompat.PRIORITY_HIGH
        )
    }

    @SuppressLint("MissingPermission")
    fun notifyBatteryMonitorNotification(
        notification: BatteryMeterNotification
    ) {
        val channel = NotificationChannels.BatteryStatus
        createChannelIfAbsent(channel)
        val notification = createBatteryMonitorNotification(
            batteryNotificationData = BatteryNotificationData(
                channelId = channel.id,
                level = notification.batteryLevel,
                isCharging = notification.isCharging,
                remainBatteryTime = notification.remainBatteryTime,
                remainChargeTime = notification.remainChargeTime,
                temperature = notification.temperature
            )
        )
        notificationManager.notify(SERVICE_ID, notification)
    }

    @SuppressLint("RemoteViewLayout")
    private fun createBatteryMonitorNotification(
        batteryNotificationData: BatteryNotificationData,
        priority: Int = NotificationCompat.PRIORITY_MIN
    ): Notification {
        val packageName = context.packageName
        val notificationLayout =
            RemoteViews(packageName, R.layout.layout_notification_battery_small)
        val notificationLayoutExpanded =
            RemoteViews(packageName, R.layout.layout_notification_battery_large)
        notificationLayout.applyData(batteryNotificationData)
        notificationLayoutExpanded.applyData(batteryNotificationData)
        val channelId = batteryNotificationData.channelId
        val action = NotificationCompat.Action(
            R.drawable.ic_settings,
            context.getString(R.string.settings),
            BatteryStatusService.createOpenBatteryStatusSettingsIntent(context, channelId)
        )
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
            .addAction(action)
            .setPriority(priority)
            .setSound(null)
            .setOngoing(true)
            .build()
    }

    private fun RemoteViews.applyData(batteryNotificationData: BatteryNotificationData) {
        this.apply {
            setTextViewText(R.id.title, context.getString(R.string.battery_status))
            setTextViewText(R.id.battery_level, "${batteryNotificationData.level}%")
            setTextViewText(R.id.temperature, batteryNotificationData.temperatureDisplay)
            setTextViewText(
                R.id.charge_status,
                context.getString(batteryNotificationData.chargeDisplay)
            )
            val remainTime = if (batteryNotificationData.isCharging == true) {
                context.getString(
                    R.string.remain_time_charging,
                    batteryNotificationData.remainChargeTime
                )
            } else {
                context.getString(
                    R.string.remain_time_battery,
                    batteryNotificationData.remainBatteryTime
                )
            }
            setTextViewText(R.id.remain_time, remainTime)
            setImageViewResource(R.id.battery_icon, batteryNotificationData.levelIcon)
        }
    }

    data class BatteryNotificationData(
        val channelId: String,
        val level: Int?,
        val isCharging: Boolean?,
        val temperature: MyDevice.Temperature?,
        val remainBatteryTime: String = "--",
        val remainChargeTime: String = "--"
    ) {
        val temperatureDisplay = temperature?.formatTemperature() ?: "--"
        val chargeDisplay = if (isCharging == true) R.string.charging else R.string.discharging
        val levelIcon = when ((level ?: 0) * 7 / 100) {
            0 -> R.drawable.ic_battery_0_bar
            1 -> R.drawable.ic_battery_1_bar
            2 -> R.drawable.ic_battery_2_bar
            3 -> R.drawable.ic_battery_3_bar
            4 -> R.drawable.ic_battery_4_bar
            5 -> R.drawable.ic_battery_5_bar
            6 -> R.drawable.ic_battery_6_bar
            7 -> R.drawable.ic_battery_full
            else -> R.drawable.ic_battery_level
        }
    }

}

object NotificationChannels {
    val BatteryStatus = NotificationChannelCompat.Builder(
        "battery-status-notification-channel-id",
        NotificationManagerCompat.IMPORTANCE_LOW
    ).setName("Battery status").setShowBadge(true).build()
}