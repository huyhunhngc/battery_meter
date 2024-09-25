package io.github.ifa.glancewidget.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import dagger.hilt.android.AndroidEntryPoint
import io.github.ifa.glancewidget.data.batteryWidgetStore
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import io.github.ifa.glancewidget.domain.BatteryUseCase
import io.github.ifa.glancewidget.glance.battery.BatteryWidget
import io.github.ifa.glancewidget.glance.battery.BatteryWidget.Companion.BATTERY_PREFERENCES
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver.Companion.BLUETOOTH_STATE_ACTIONS
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.model.ThemeTypeColor
import io.github.ifa.glancewidget.service.NotificationHandler
import io.github.ifa.glancewidget.utils.getObject
import io.github.ifa.glancewidget.utils.getSerializable
import io.github.ifa.glancewidget.utils.setBoolean
import io.github.ifa.glancewidget.utils.setObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MonitorReceiver : BroadcastReceiver() {
    @Inject
    lateinit var batteryStateRepository: BatteryStateRepository

    @Inject
    lateinit var batteryUseCase: BatteryUseCase

    @Inject
    lateinit var notificationHandler: NotificationHandler

    @Inject
    lateinit var appSettingsRepository: AppSettingsRepository

    private val lock = Object()
    private var batteryData: BatteryData = BatteryData.initial()
        set(value) {
            synchronized(lock) {
                field = value
            }
        }

    override fun onReceive(context: Context, intent: Intent) {
        val updatedBatteryData = when (intent.action) {
            Intent.ACTION_BATTERY_CHANGED -> {
                batteryData.copy(myDevice = MyDevice.fromIntent(intent)).setPairedDevices(context)
            }

            Intent.ACTION_POWER_CONNECTED -> batteryData.setChargingStatus(true)

            Intent.ACTION_POWER_DISCONNECTED -> batteryData.setChargingStatus(false)

            in BLUETOOTH_STATE_ACTIONS -> {
                batteryData.setPairedDevices(context)
            }

            else -> batteryData
        }
        goAsync(MainScope()) {
            var shouldUpdateWidget = false
            if (intent.action == ACTION_SHOW_PAIRED_DEVICES_CHANGED) {
                val showPairedDevices = intent.getBooleanExtra(SHOW_PAIRED_DEVICES, true)
                hideOrShowPairedDevices(context, showPairedDevices)
                shouldUpdateWidget = true
            }
            handleThemeSetting(context, intent)
            if (updatedBatteryData != batteryData) {
                batteryData = updatedBatteryData
                updateBatteryWidgetData(context)
                handleNotification()
                shouldUpdateWidget = true
            }
            if (shouldUpdateWidget) {
                updateBatteryWidget(context)
            }
        }
    }

    private suspend fun updateBatteryWidgetData(context: Context) = coroutineScope {
        withContext(Dispatchers.IO) {
            val savedBatteryData = context.batteryWidgetStore.getObject<BatteryData>(
                BATTERY_PREFERENCES
            )
            if (savedBatteryData != null && batteryData == BatteryData.initial()) {
                batteryData = savedBatteryData
            }
            batteryStateRepository.saveExtraBatteryInformation()
            context.batteryWidgetStore.setObject(BATTERY_PREFERENCES, batteryData)
        }
    }

    private suspend fun hideOrShowPairedDevices(
        context: Context,
        showPairedDevices: Boolean
    ) {
        withContext(Dispatchers.IO) {
            context.batteryWidgetStore.setBoolean(
                BatteryWidget.SHOW_PAIRED_DEVICES,
                showPairedDevices
            )
        }
    }

    private suspend fun updateBatteryWidget(context: Context) {
        val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(BatteryWidget::class.java)
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                glanceId = glanceId,
            ) { _ ->
                BatteryWidget().updateIfBatteryChanged(context, glanceId)
            }
        }
    }

    private suspend fun handleThemeSetting(context: Context, intent: Intent) {
        if (intent.action == ACTION_SYNC_THEME) {
            intent.getSerializable<ThemeType>(SYNC_THEME)?.let {
                BatteryWidget().updateWidgetSetting(context) { copy(theme = it) }
            }
        }
        if (intent.action == ACTION_SYNC_THEME_COLOR) {
            intent.getSerializable<ThemeTypeColor>(SYNC_THEME_COLOR)?.let {
                BatteryWidget().updateWidgetSetting(context) { copy(themeColor = it) }
            }
        }
    }

    private suspend fun handleNotification() {
        if (!appSettingsRepository.getAppSettings().notificationSetting.batteryAlert) return
        batteryUseCase.getBatteryWrapper().firstOrNull()
            ?.let { notificationHandler.notifyBatteryMonitorNotification(it) } ?: {
            notificationHandler.notifyBatteryMonitorNotification(batteryData)
        }
    }

    companion object {
        const val ACTION_SHOW_PAIRED_DEVICES_CHANGED = "action_show_paired_devices_changed"
        const val SHOW_PAIRED_DEVICES = "show_paired_devices"
        const val ACTION_SYNC_THEME = "action_sync_theme"
        const val SYNC_THEME = "sync_theme"
        const val ACTION_SYNC_THEME_COLOR = "action_sync_theme_color"
        const val SYNC_THEME_COLOR = "sync_theme_color"
    }
}

fun BroadcastReceiver.goAsync(
    coroutineScope: CoroutineScope,
    block: suspend () -> Unit
) {
    val pendingResult = goAsync()
    coroutineScope.launch {
        block()
        pendingResult.finish()
    }
}