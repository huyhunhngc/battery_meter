package io.github.ifa.glancewidget.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.LocaleManager
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.ContextWrapper
import android.content.res.Resources
import android.os.BatteryManager
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.DeviceType
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.utils.Constants.MAX_DESIGN_CAPACITY
import io.github.ifa.glancewidget.utils.Constants.MIN_DESIGN_CAPACITY
import java.util.Locale

@SuppressLint("MissingPermission")
fun Context.getPairedDevices(): List<BonedDevice> {
    val pairedDevices = kotlin.runCatching {
        val btManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        btManager.adapter.bondedDevices
    }.getOrDefault(emptyList())

    return pairedDevices.map {
        BonedDevice(
            name = it.name,
            address = it.address,
            batteryInPercentage = it.batteryLevel,
            batteryInMinutes = 0,
            deviceType = DeviceType.fromClass(it.bluetoothClass.deviceClass)
        )
    }.sortedBy { it.deviceType.ordinal }
}

fun Context.getExtraBatteryInformation(): ExtraBatteryInfo {
    val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val chargeCounter =
        batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
    val capacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    val fullChargeCapacity = chargeCounter.toFloat() / capacity.toFloat() * 100f
    val chargingTimeRemaining = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        batteryManager.computeChargeTimeRemaining() / 1000
    } else {
        -1
    }

    val chargeCurrent = batteryManager.getIntProperty(
        BatteryManager.BATTERY_PROPERTY_CURRENT_NOW
    ) / 1_000 // micro to millis

    val remainedCapacity = batteryManager.getIntProperty(
        BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER
    ) / 1_000_000

    return ExtraBatteryInfo(
        capacity = getDesignCapacity(),
        fullChargeCapacity = fullChargeCapacity.toInt(),
        chargeCounter = chargeCounter,
        chargingTimeRemaining = chargingTimeRemaining,
        chargeCurrent = chargeCurrent
    )
}

@SuppressLint("PrivateApi")
private fun Context.getDesignCapacity(): Int {
    val powerProfileClass = "com.android.internal.os.PowerProfile"
    val mPowerProfile = Class.forName(powerProfileClass).getConstructor(
        Context::class.java
    ).newInstance(this)
    val designCapacity = (Class.forName(powerProfileClass).getMethod(
        "getBatteryCapacity"
    ).invoke(mPowerProfile) as Double).toInt()

    return when {
        designCapacity == 0 || designCapacity < MIN_DESIGN_CAPACITY || designCapacity > MAX_DESIGN_CAPACITY -> MIN_DESIGN_CAPACITY
        else -> designCapacity
    }
}

//fun Context.getChargingTimeRemaining(
//    myDevice: MyDevice,
//    extraBatteryInfo: ExtraBatteryInfo
//): String {
//    var chargingTimeRemaining: Double
//    val batteryLevel = myDevice.level
//    val currentCapacity = extraBatteryInfo.chargeCounter
//    val residualCapacity = extraBatteryInfo.capacity
//    return "-"
//}

fun Context.setLocale(localeCode: String) {
    val code = if (localeCode == AppSettings.Language.DEFAULT.code) {
        getSystemLocale()
    } else {
        localeCode
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        setLocaleForDevicesHigherApi33(code)
    } else {
        setLocaleForDevicesLowerApi33(code)
    }
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
}

private fun getSystemLocale(): String {
    val systemConfig = Resources.getSystem().configuration
    return systemConfig.locales[0].language
}

private fun Context.setLocaleForDevicesHigherApi33(localeCode: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSystemService(LocaleManager::class.java).applicationLocales =
            LocaleList.forLanguageTags(localeCode)
    }
}

private fun Context.setLocaleForDevicesLowerApi33(localeTag: String) {
    val locale = Locale(localeTag)
    Locale.setDefault(locale)
    val configuration = resources.configuration
    configuration.setLocale(locale)
    createConfigurationContext(configuration)
    resources.updateConfiguration(configuration, resources.displayMetrics)
}