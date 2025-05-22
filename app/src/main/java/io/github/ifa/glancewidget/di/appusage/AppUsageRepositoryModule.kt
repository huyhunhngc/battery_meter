package io.github.ifa.glancewidget.di.appusage

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import io.github.ifa.glancewidget.data.appusage.DefaultAppUsageRepository
import io.github.ifa.glancewidget.di.RepositoryQualifier
import io.github.ifa.glancewidget.domain.AppUsageRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppUsageRepositoryModule {
    @Binds
    @RepositoryQualifier
    @IntoMap
    @ClassKey(AppUsageRepository::class)
    abstract fun bindAppUsageRepository(repository: AppUsageRepository): Any

    companion object {
        @Singleton
        @Provides
        fun provideAppUsageRepository(
            @ApplicationContext context: Context
        ): AppUsageRepository {
            return DefaultAppUsageRepository(context)
        }
    }
}