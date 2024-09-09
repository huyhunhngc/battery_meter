package io.github.ifa.glancewidget.di.settings

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import io.github.ifa.glancewidget.data.appsettings.AppSettingDataStore
import io.github.ifa.glancewidget.data.appsettings.DefaultAppSettingsRepository
import io.github.ifa.glancewidget.di.RepositoryQualifier
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppSettingsRepositoryModule {
    @Binds
    @RepositoryQualifier
    @IntoMap
    @ClassKey(AppSettingsRepository::class)// use for LocalRepositories for compose in the future
    abstract fun bindAppSettingsRepository(repository: AppSettingsRepository): Any

    companion object {
        @Singleton
        @Provides
        fun provideAppSettingsRepository(
            appSettingDataStore: AppSettingDataStore,
            @ApplicationContext context: Context
        ): AppSettingsRepository {
            return DefaultAppSettingsRepository(appSettingDataStore, context)
        }
    }
}