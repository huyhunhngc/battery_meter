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
import io.github.ifa.glancewidget.utils.getPairedDevices
import io.github.ifa.glancewidget.utils.getSerializable
import io.github.ifa.glancewidget.utils.goAsyncCoroutine
import io.github.ifa.glancewidget.utils.updateBatteryWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@AndroidEntryPoint
class BatteryWidgetMonitor : BroadcastReceiver() {
    @Inject
    lateinit var notificationHandler: NotificationHandler

    @Inject
    lateinit var monitorUseCase: MonitorUseCase
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BATTERY_CHANGED -> {
                monitorUseCase.updateBatteryDevice(MyDevice.fromIntent(intent))
                val pairedDevices = try {
                    context.getPairedDevices()
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
                monitorUseCase.setPairedDevices(pairedDevices)
            }

            Intent.ACTION_POWER_CONNECTED -> {
                monitorUseCase.setChargingStatus(true)
            }

            Intent.ACTION_POWER_DISCONNECTED -> {
                monitorUseCase.setChargingStatus(false)
            }

            AppIntent.ACTION_SHOW_PAIRED_DEVICES_CHANGED -> {
                val showPairedDevices = intent.getBooleanExtra(AppExtra.SHOW_PAIRED_DEVICES, true)
                goAsyncCoroutine(scope) {
                    monitorUseCase.changePairedDevicesVisibility(showPairedDevices)
                }
            }

            AppIntent.ACTION_SYNC_THEME -> {
                goAsyncCoroutine(scope) {
                    intent.getSerializable<ThemeType>(AppExtra.SYNC_THEME)?.let {
                        BatteryWidget().updateWidgetSetting(context) { copy(theme = it) }
                    }
                }
            }

            AppIntent.ACTION_SYNC_THEME_COLOR -> {
                goAsyncCoroutine(scope) {
                    intent.getSerializable<ThemeTypeColor>(AppExtra.SYNC_THEME_COLOR)?.let {
                        BatteryWidget().updateWidgetSetting(context) { copy(themeColor = it) }
                    }
                }
            }

            in BLUETOOTH_STATE_ACTIONS -> {
                val pairedDevices = try {
                    context.getPairedDevices()
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
                monitorUseCase.setPairedDevices(pairedDevices)
            }
        }
        goAsyncCoroutine(scope) {
            monitorUseCase.refreshBatteryInformation()
            monitorUseCase.onDetectBatteryInfo {
                notificationHandler.notifyBatteryMonitorNotification(it)
            }
            context.updateBatteryWidget()
        }
    }
}
