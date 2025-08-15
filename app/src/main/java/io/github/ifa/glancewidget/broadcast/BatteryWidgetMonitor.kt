package io.github.ifa.glancewidget.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import io.github.ifa.glancewidget.domain.MonitorUseCase
import io.github.ifa.glancewidget.glance.battery.BatteryWidget
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.BLUETOOTH_STATE_ACTIONS
import io.github.ifa.glancewidget.model.AppExtra
import io.github.ifa.glancewidget.model.AppIntent
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.model.ThemeTypeColor
import io.github.ifa.glancewidget.service.NotificationHandler
import io.github.ifa.glancewidget.utils.getSerializable
import io.github.ifa.glancewidget.utils.goAsyncCoroutine
import io.github.ifa.glancewidget.utils.safeGetPairedDevices
import io.github.ifa.glancewidget.utils.updateBatteryWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@AndroidEntryPoint
class BatteryWidgetMonitor : BroadcastReceiver() {
    @Inject
    lateinit var notificationHandler: NotificationHandler

    @Inject
    lateinit var monitorUseCase: MonitorUseCase

    override fun onReceive(context: Context, intent: Intent) {
        goAsyncCoroutine(MainScope(), Dispatchers.IO) {
            when (intent.action) {
                Intent.ACTION_BATTERY_CHANGED -> {
                    monitorUseCase.updateBatteryDevice(MyDevice.fromIntent(intent))
                    monitorUseCase.setPairedDevices(context.safeGetPairedDevices())
                    monitorUseCase.onDetectBatteryInfo {
                        notificationHandler.notifyBatteryMonitorNotification(it)
                    }
                }

                Intent.ACTION_POWER_CONNECTED -> {
                    monitorUseCase.setChargingStatus(true)
                    monitorUseCase.onDetectBatteryInfo {
                        notificationHandler.notifyBatteryMonitorNotification(it)
                    }
                }

                Intent.ACTION_POWER_DISCONNECTED -> {
                    monitorUseCase.setChargingStatus(false)
                    monitorUseCase.onDetectBatteryInfo {
                        notificationHandler.notifyBatteryMonitorNotification(it)
                    }
                }

                AppIntent.ACTION_SHOW_PAIRED_DEVICES_CHANGED -> {
                    val showPairedDevices =
                        intent.getBooleanExtra(AppExtra.SHOW_PAIRED_DEVICES, true)
                    monitorUseCase.changePairedDevicesVisibility(showPairedDevices)
                }

                AppIntent.ACTION_SYNC_THEME -> {
                    intent.getSerializable<ThemeType>(AppExtra.SYNC_THEME)?.let {
                        BatteryWidget().updateWidgetSetting(context) { copy(theme = it) }
                    }
                }

                AppIntent.ACTION_SYNC_THEME_COLOR -> {
                    intent.getSerializable<ThemeTypeColor>(AppExtra.SYNC_THEME_COLOR)?.let {
                        BatteryWidget().updateWidgetSetting(context) { copy(themeColor = it) }
                    }
                }

                in BLUETOOTH_STATE_ACTIONS -> {
                    monitorUseCase.setPairedDevices(context.safeGetPairedDevices())
                }
            }
            monitorUseCase.refreshBatteryInformation()
            context.updateBatteryWidget()
        }
    }
}
