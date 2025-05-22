package io.github.ifa.glancewidget.data.appusage

import android.app.usage.UsageStats
import android.content.Context
import android.content.pm.PackageManager
import io.github.ifa.glancewidget.domain.AppUsageRepository
import io.github.ifa.glancewidget.domain.UsageStatsWrapper
import io.github.ifa.glancewidget.utils.checkAppUsagePermission
import io.github.ifa.glancewidget.utils.getInstalledApps
import io.github.ifa.glancewidget.utils.getStartOfDay
import io.github.ifa.glancewidget.utils.getUsageStatsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Calendar

class DefaultAppUsageRepository(
    private val context: Context
) : AppUsageRepository {
    private val startTime = MutableStateFlow(Calendar.getInstance().getStartOfDay().timeInMillis)
    private val endTime = MutableStateFlow(Calendar.getInstance().timeInMillis)

    override fun getAppUsageStats(): Flow<List<UsageStatsWrapper>> {
        val usageStatsManager = context.getUsageStatsManager()
        val installedApp = context.getInstalledApps()
        return combine(
            startTime,
            endTime
        ) { start, end ->
            val usageStats = usageStatsManager
                ?.queryAndAggregateUsageStats(start, end)?.values?.toList()
                ?.filter { it.totalTimeInForeground > 60 * 1000 }
                .orEmpty()
            buildUsageStatsWrapper(installedApp, usageStats)
        }.flowOn(Dispatchers.IO)
    }

    override fun hasAppUsagePermission(): Flow<Boolean> {
        return flow {
            emit(context.checkAppUsagePermission())
        }
    }

    private fun buildUsageStatsWrapper(
        packageNames: List<String>,
        usageStats: List<UsageStats>
    ): List<UsageStatsWrapper> {
        val packageManager = context.packageManager
        val statPackageNames = usageStats.map { it.packageName }
        return packageNames.mapNotNull { packageName ->
            if (statPackageNames.contains(packageName)) {
                usageStats.firstOrNull { it.packageName == packageName }
                    ?.toUsageStatsWrapper(packageManager, packageName)
            } else {
                null
            }
        }.sortedByDescending { it.usageStats?.totalTimeInForeground }
    }
}

private fun UsageStats.toUsageStatsWrapper(
    packageManager: PackageManager,
    packageName: String
): UsageStatsWrapper? {
    val applicationInfo = try {
        packageManager.getApplicationInfo(packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        return null
    }
    return UsageStatsWrapper(
        usageStats = this,
        appIcon = packageManager.getApplicationIcon(applicationInfo),
        appName = packageManager.getApplicationLabel(applicationInfo).toString()
    )
}

private fun String.toUsageStatsWrapper(
    packageManager: PackageManager,
): UsageStatsWrapper? {
    val applicationInfo = try {
        packageManager.getApplicationInfo(this, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        return null
    }
    return UsageStatsWrapper(
        appIcon = packageManager.getApplicationIcon(applicationInfo),
        appName = packageManager.getApplicationLabel(applicationInfo).toString()
    )
}