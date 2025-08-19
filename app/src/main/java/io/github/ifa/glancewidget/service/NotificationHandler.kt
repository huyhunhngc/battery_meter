package io.github.ifa.glancewidget.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.widget.RemoteViews
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.BatteryMeterNotification
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.service.BatteryStatusService.Companion.SERVICE_ID
import javax.inject.Inject

class NotificationHandler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val notificationManager: NotificationManager? by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    private fun createChannelIfAbsent(channel: NotificationChannel) {
        if (notificationManager?.getNotificationChannel(channel.id) == null) {
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun createStartMonitorNotification(myDevice: MyDevice?): Notification {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
        )
        createChannelIfAbsent(channel)
        return createBatteryMonitorNotification(
            batteryNotificationData = BatteryNotificationData(
                channelId = channel.id,
                level = myDevice?.level ?: 0,
                isCharging = myDevice?.isCharging ?: false,
                temperature = myDevice?.temperature
            )
        )
    }

    @SuppressLint("MissingPermission")
    fun notifyBatteryMonitorNotification(
        notification: BatteryMeterNotification
    ) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
        )
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
        notificationManager?.notify(SERVICE_ID, notification)
    }

    @SuppressLint("RemoteViewLayout")
    private fun createBatteryMonitorNotification(
        batteryNotificationData: BatteryNotificationData
    ): Notification {
        val packageName = context.packageName
        val notificationLayout =
            RemoteViews(packageName, R.layout.layout_notification_battery_small)
        val notificationLayoutExpanded =
            RemoteViews(packageName, R.layout.layout_notification_battery_large)
        notificationLayout.applyData(batteryNotificationData)
        notificationLayoutExpanded.applyData(batteryNotificationData)
        val channelId = batteryNotificationData.channelId
        val action = Notification.Action(
            R.drawable.ic_settings,
            context.getString(R.string.settings),
            BatteryStatusService.createOpenBatteryStatusSettingsIntent(context, channelId)
        )
        return Notification.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_charger)
            .setBadgeIconType(Notification.BADGE_ICON_SMALL)
            .setStyle(Notification.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
            .addAction(action)
            .setOngoing(true)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .build()
    }

    private fun RemoteViews.applyData(batteryNotificationData: BatteryNotificationData) {
        this.apply {
            setTextViewText(R.id.title, context.getString(R.string.battery_status))
            setTextViewText(R.id.battery_level, "${batteryNotificationData.level}%")
            setTextViewText(R.id.temperature, batteryNotificationData.temperatureDisplay)
            setTextViewText(
                R.id.charge_status, context.getString(batteryNotificationData.chargeDisplay)
            )
            val remainTime = if (batteryNotificationData.isCharging == true) {
                "${context.getString(R.string.remain_time_charging)} ${batteryNotificationData.remainChargeTime}"
            } else {
                "${context.getString(R.string.remain_time_battery)} ${batteryNotificationData.remainBatteryTime}"
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
        val levelIcon = if (isCharging == true) {
            when ((level ?: 0) * 7 / 100) {
                0 -> R.drawable.ic_battery_charging_10
                1 -> R.drawable.ic_battery_charging_20
                2 -> R.drawable.ic_battery_charging_30
                3 -> R.drawable.ic_battery_charging_50
                4 -> R.drawable.ic_battery_charging_60
                5 -> R.drawable.ic_battery_charging_80
                6 -> R.drawable.ic_battery_charging_90
                7 -> R.drawable.ic_battery_charging_full
                else -> R.drawable.ic_battery_charging_full
            }
        } else {
            when ((level ?: 0) * 7 / 100) {
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

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "battery_status_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Battery Status"
    }

}
