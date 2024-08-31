package io.github.ifa.glancewidget.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.batteryWidgetStore by preferencesDataStore("BatteryWidget")