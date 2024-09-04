package io.github.ifa.glancewidget.domain

import androidx.compose.runtime.Composable
import io.github.ifa.glancewidget.di.LocalRepositories
import io.github.ifa.glancewidget.model.BatteryData
import kotlinx.coroutines.flow.Flow

interface BatteryStateRepository {
    @Composable
    fun battery(): BatteryData

    fun batteryFlow(): Flow<BatteryData>
}

@Composable
fun localAuthenticationRepository(): BatteryStateRepository {
    return LocalRepositories.current[BatteryStateRepository::class] as BatteryStateRepository
}
