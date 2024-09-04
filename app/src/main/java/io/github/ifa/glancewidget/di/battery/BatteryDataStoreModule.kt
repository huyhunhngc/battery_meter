package io.github.ifa.glancewidget.di.battery

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.ifa.glancewidget.data.battery.BatteryDataStore
import io.github.ifa.glancewidget.di.BatteryDataStoreQualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class BatteryDataStoreModule {
    @Singleton
    @Provides
    fun provideAuthenticationUserDataStore(
        @BatteryDataStoreQualifier
        dataStore: DataStore<Preferences>,
    ): BatteryDataStore {
        return BatteryDataStore(dataStore)
    }
}