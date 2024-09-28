package io.github.ifa.glancewidget.di.playbilling

import com.android.billingclient.api.BillingClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import io.github.ifa.glancewidget.data.playbilling.DefaultPlayBillingRepository
import io.github.ifa.glancewidget.di.RepositoryQualifier
import io.github.ifa.glancewidget.domain.PlayBillingRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayBillingRepositoryModule {
    @Binds
    @RepositoryQualifier
    @IntoMap
    @ClassKey(PlayBillingRepository::class)// use for LocalRepositories for compose in the future
    abstract fun bindPlayBillingRepository(repository: PlayBillingRepository): Any

    companion object {
        @Singleton
        @Provides
        fun providePlayBillingRepository(
            billingClient: BillingClient
        ): PlayBillingRepository {
            return DefaultPlayBillingRepository(billingClient)
        }
    }
}