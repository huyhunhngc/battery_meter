package io.github.ifa.glancewidget.utils

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.LocaleManager
import android.app.usage.UsageStatsManager
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.BatteryManager
import android.os.Build
import android.os.LocaleList
import android.os.Process.myUid
import android.text.TextPaint
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.TextUnit
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.LocaleListCompat
import androidx.core.util.TypedValueCompat.spToPx
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.DeviceType
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.utils.Constants.MAX_DESIGN_CAPACITY
import io.github.ifa.glancewidget.utils.Constants.MIN_DESIGN_CAPACITY


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
    val propertyChargeCounter =
        batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
    // this value is current charge counter in micro amp hours. But some device return in millis
    val chargeCounter = if (propertyChargeCounter / 1000 < MAX_DESIGN_CAPACITY / 1000) {
        propertyChargeCounter
    } else {
        propertyChargeCounter / 1000
    }
    val capacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

    val fullChargeCapacity = chargeCounter.toFloat() / capacity.toFloat() * 100f
    // micro amp to millis amp
    val chargeCurrent = batteryManager.getIntProperty(
        BatteryManager.BATTERY_PROPERTY_CURRENT_NOW
    ) / 1_000
    val designCapacity = getDesignCapacity()

    return ExtraBatteryInfo(
        capacity = designCapacity,
        fullChargeCapacity = fullChargeCapacity.roundToNearestHundred(designCapacity),
        chargeCounter = chargeCounter,
        chargeCurrent = chargeCurrent
    )
}

fun Float.roundToNearestHundred(capacity: Int): Int {
    val rounded = (this.toInt() + 50) / 100 * 100
    return minOf(rounded, capacity)
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
        designCapacity == 0 || designCapacity < MIN_DESIGN_CAPACITY
                || designCapacity > MAX_DESIGN_CAPACITY -> MIN_DESIGN_CAPACITY
        else -> designCapacity
    }
}

fun Context.setLocale(localeCode: String) {
    val code = if (localeCode == AppSettings.Language.DEFAULT.code) {
        ""
    } else {
        localeCode
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        setLocaleForDevicesHigherApi33(code)
    } else {
        setLocaleForDevicesLowerApi33(code)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun Context.setLocaleForDevicesHigherApi33(localeCode: String) {
    getSystemService(LocaleManager::class.java).applicationLocales =
        LocaleList.forLanguageTags(localeCode)
}

private fun setLocaleForDevicesLowerApi33(localeTag: String) {
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeTag))
}

fun Context.textAsBitmap(
    text: String,
    fontSize: TextUnit,
    color: Color = Color.Black,
    letterSpacing: Float = 0.1f,
    font: Int
): Bitmap {
    val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    paint.textSize = spToPx(fontSize.value, resources.displayMetrics)
    paint.color = color.toArgb()
    paint.letterSpacing = letterSpacing
    paint.typeface = ResourcesCompat.getFont(this, font)

    val baseline = -paint.ascent()
    val width = (paint.measureText(text)).toInt()
    val height = (baseline + paint.descent()).toInt()
    val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(image)
    canvas.drawText(text, 0f, baseline, paint)
    return image
}

fun Context.checkAppUsagePermission(): Boolean {
    val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        appOps.unsafeCheckOpNoThrow(OPSTR_GET_USAGE_STATS, myUid(), packageName)
    } else {
        appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, myUid(), packageName)
    }
    return mode == AppOpsManager.MODE_ALLOWED
}

fun Context.getUsageStatsManager(): UsageStatsManager? {
    return getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
}

@SuppressLint("QueryPermissionsNeeded")
fun Context.getInstalledApps(): List<String> {
    val flags = PackageManager.GET_META_DATA
    val installedApps = packageManager.getInstalledApplications(flags).map { it.packageName }
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    val resolvedApps = packageManager.queryIntentActivities(intent, flags).map { it.toString() }
    return installedApps + resolvedApps
}