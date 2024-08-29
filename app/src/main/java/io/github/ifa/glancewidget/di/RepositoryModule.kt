package io.github.ifa.glancewidget.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import io.github.ifa.glancewidget.data.BatteryStateRepository
import io.github.ifa.glancewidget.domain.DefaultBatteryStateRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @RepositoryQualifier
    @IntoMap
    @ClassKey(BatteryStateRepository::class)
    abstract fun bindBatteryStateRepository(repository: BatteryStateRepository): Any

    companion object {
        @Singleton
        @Provides
        fun provideBatteryStateRepository(): BatteryStateRepository {
            return DefaultBatteryStateRepository()
        }
    }
}
