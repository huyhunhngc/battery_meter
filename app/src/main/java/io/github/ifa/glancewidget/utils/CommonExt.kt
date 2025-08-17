package io.github.ifa.glancewidget.utils

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import io.github.ifa.glancewidget.glance.battery.BatteryWidget
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver
import io.github.ifa.glancewidget.model.AddWidgetParams
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.Serializable
import java.util.Calendar

inline fun <reified T> fromJson(jsonString: String?): T? {
    if (jsonString == null) return null
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
    return runCatching { json.decodeFromString<T>(jsonString) }.getOrNull()
}

inline fun <reified T> toJson(value: T): String {
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
    return json.encodeToString(value)
}

suspend inline fun <reified T> DataStore<Preferences>.setObject(
    key: Preferences.Key<String>, value: T
) {
    edit {
        it[key] = toJson(value)
    }
}

suspend inline fun <reified T> DataStore<Preferences>.getObject(
    key: Preferences.Key<String>
): T? {
    val string = data.map { it[key] }.firstOrNull() ?: return null
    return fromJson(string)
}

suspend inline fun  DataStore<Preferences>.setBoolean(
    key: Preferences.Key<Boolean>, value: Boolean
) {
    edit {
        it[key] = value
    }
}

suspend inline fun DataStore<Preferences>.setInt(
    key: Preferences.Key<Int>, value: Int
) {
    edit {
        it[key] = value
    }
}

suspend inline fun DataStore<Preferences>.getInt(
    key: Preferences.Key<Int>
): Int? {
    return data.map { it[key] }.firstOrNull()
}
inline fun <reified T: Serializable> Intent.getSerializable(key: String?): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getSerializableExtra(key) as? T
    }
}

val BluetoothPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    listOf(
        Manifest.permission.BLUETOOTH_CONNECT
    )
} else {
    listOf(
        Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN
    )
}

val NotificationPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    listOf(Manifest.permission.POST_NOTIFICATIONS)
} else {
    emptyList()
}

val AppPermissions = BluetoothPermissions + NotificationPermissions

val BluetoothDevice.batteryLevel
    get() = this.let { device ->
        val method = device.javaClass.getMethod("getBatteryLevel")
        method.invoke(device) as Int?
    } ?: -1

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun Context.requestToPinWidget(params: AddWidgetParams): Boolean {
    val appWidgetManager = getSystemService(AppWidgetManager::class.java)
    val myProvider = ComponentName(applicationContext, BatteryWidgetReceiver::class.java)
    if (appWidgetManager.isRequestPinAppWidgetSupported) {
        val successIntent = Intent(applicationContext, BatteryWidgetReceiver::class.java).apply {
            action = "ACTION_PINNED_SUCCESS"
            putExtra("EXTRA_DEVICE_ADDRESS", "Huy's Device")
        }

        val successCallback = PendingIntent.getBroadcast(
            applicationContext,
            System.currentTimeMillis().toInt(),
            successIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
    }
    return false
}

fun isSupportedDynamicColor(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}

fun isAppCompatLocaleDeprecated(): Boolean {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
}

fun Calendar.getStartOfDay(): Calendar {
    return this.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}

fun Calendar.getEndOfDay(): Calendar {
    return this.apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
}

fun Calendar.subtractDays(days: Int): Calendar {
    return this.apply {
        add(Calendar.DAY_OF_MONTH, -days)
    }
}

fun Calendar.subtractHours(hours: Int): Calendar {
    return this.apply {
        add(Calendar.HOUR_OF_DAY, -hours)
    }
}

fun BroadcastReceiver.goAsyncCoroutine(
    coroutineScope: CoroutineScope,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: suspend () -> Unit
) {
    val pendingResult = goAsync()
    coroutineScope.launch(dispatcher) {
        block()
        pendingResult.finish()
    }
}

suspend fun Context.updateBatteryWidget() {
    val glanceIds = GlanceAppWidgetManager(this).getGlanceIds(BatteryWidget::class.java)
    glanceIds.forEach { glanceId ->
        updateAppWidgetState(
            context = this,
            glanceId = glanceId,
        ) { _ ->
            BatteryWidget().updateIfBatteryChanged(this, glanceId)
        }
    }
}

fun Long.timeMillisToHours(): Float {
    return (this.toDouble() / (1000 * 60 * 60)).toFloat()
}