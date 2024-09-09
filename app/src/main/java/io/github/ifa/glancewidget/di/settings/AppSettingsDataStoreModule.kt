package io.github.ifa.glancewidget.di.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.ifa.glancewidget.data.appsettings.AppSettingDataStore
import io.github.ifa.glancewidget.di.AppSettingsDataStoreQualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class AppSettingsDataStoreModule {
    @Singleton
    @Provides
    fun provideAppSettingsDataStore(
        @AppSettingsDataStoreQualifier
        dataStore: DataStore<Preferences>,
    ): AppSettingDataStore {
        return AppSettingDataStore(dataStore)
    }
}