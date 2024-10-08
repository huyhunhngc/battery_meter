package io.github.ifa.glancewidget.utils

import android.Manifest
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import io.github.ifa.glancewidget.glance.battery.BatteryWidgetReceiver
import io.github.ifa.glancewidget.model.AddWidgetParams
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


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

val BluetoothPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    listOf(
        Manifest.permission.BLUETOOTH_CONNECT
    )
} else {
    listOf(
        Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN
    )
}

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
        return appWidgetManager.requestPinAppWidget(myProvider, null, null)
    }
    return false
}
