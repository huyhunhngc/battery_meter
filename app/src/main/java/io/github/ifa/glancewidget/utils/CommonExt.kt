package io.github.ifa.glancewidget.utils

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.DeviceType
import io.github.ifa.glancewidget.model.WidgetSize
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
    return json.decodeFromString<T>(jsonString)
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
        Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN
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


@SuppressLint("MissingPermission")
fun Context.getPairedDevices(): List<BonedDevice> {
    val btManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
    val pairedDevices = btManager.adapter.bondedDevices

    return pairedDevices.filter { it.batteryLevel in 0..100 }.map {
        BonedDevice(
            name = it.name,
            address = it.address,
            batteryInPercentage = it.batteryLevel,
            batteryInMinutes = 0,
            deviceType = DeviceType.fromClass(it.bluetoothClass.deviceClass)
        )
    }.sortedBy { it.deviceType.ordinal }
}

fun List<BonedDevice>.take(widgetSize: WidgetSize): List<BonedDevice> {
//    val item = when {
//        widgetSize.width
//    }
    return take(2)
}