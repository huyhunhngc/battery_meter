package io.github.ifa.glancewidget.di.battery

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import io.github.ifa.glancewidget.data.battery.BatteryDataStore
import io.github.ifa.glancewidget.data.battery.DefaultBatteryStateRepository
import io.github.ifa.glancewidget.di.RepositoryQualifier
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BatteryRepositoryModule {
    @Binds
    @RepositoryQualifier
    @IntoMap
    @ClassKey(BatteryStateRepository::class)
    abstract fun bindBatteryStateRepository(repository: BatteryStateRepository): Any

    companion object {
        @Singleton
        @Provides
        fun provideBatteryStateRepository(
            batteryDataStore: BatteryDataStore
        ): BatteryStateRepository {
            return DefaultBatteryStateRepository(batteryDataStore)
        }
    }
}