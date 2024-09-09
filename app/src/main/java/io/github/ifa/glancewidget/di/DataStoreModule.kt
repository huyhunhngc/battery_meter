package io.github.ifa.glancewidget.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.ifa.glancewidget.data.batteryWidgetStore
import io.github.ifa.glancewidget.data.createDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class BatteryDataStoreQualifier

@Qualifier
annotation class AppSettingsDataStoreQualifier

@InstallIn(SingletonComponent::class)
@Module
class DataStoreModule {
    @BatteryDataStoreQualifier
    @Provides
    @Singleton
    fun provideBatteryDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.batteryWidgetStore

    @AppSettingsDataStoreQualifier
    @Provides
    @Singleton
    fun provideAppSettingsDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = createDataStore(
        coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        producePath = { context.cacheDir.resolve(DATA_STORE_APP_SETTINGS_PREFERENCE_FILE_NAME).path },
        context = context
    )

    companion object {
        private const val DATA_STORE_APP_SETTINGS_PREFERENCE_FILE_NAME = "battery_meter.app_settings.preferences_pb"
    }
}