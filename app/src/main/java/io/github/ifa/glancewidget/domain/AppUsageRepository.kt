package io.github.ifa.glancewidget.domain

import android.app.usage.UsageStats
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import io.github.ifa.glancewidget.di.LocalRepositories
import kotlinx.coroutines.flow.Flow

interface AppUsageRepository {
    fun getAppUsageStats(): Flow<List<UsageStatsWrapper>>
    fun hasAppUsagePermission(): Flow<Boolean>

    fun onChangedUsageRange(start: Long, end: Long)
}

@Composable
fun localAppUsageRepository(): AppUsageRepository {
    return LocalRepositories.current[AppUsageRepository::class] as AppUsageRepository
}

data class UsageStatsWrapper(
    val usageStats: UsageStats? = null,
    val appIcon: Drawable,
    val appName: String
)
