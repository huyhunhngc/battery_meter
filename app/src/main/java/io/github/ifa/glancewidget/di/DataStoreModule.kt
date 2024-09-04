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
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class BatteryDataStoreQualifier

@InstallIn(SingletonComponent::class)
@Module
class DataStoreModule {
    @BatteryDataStoreQualifier
    @Provides
    @Singleton
    fun provideBatteryDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.batteryWidgetStore
}