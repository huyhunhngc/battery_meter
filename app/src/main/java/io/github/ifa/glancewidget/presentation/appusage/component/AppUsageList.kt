package io.github.ifa.glancewidget.presentation.appusage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.domain.UsageStatsWrapper

fun Long.formatDuration(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return buildString {
        if (hours > 0) append("${hours}h ")
        if (minutes > 0) append("${minutes}m ")
        append("${seconds}s")
    }.trim()
}

@Composable
fun AppUsageList(
    appUsageStats: List<UsageStatsWrapper>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        item {
            TimeRangePicker()
        }
        items(
            items = appUsageStats,
            key = { appUsage -> appUsage.usageStats?.packageName.orEmpty() }
        ) { appUsage ->
            AppUsageItem(
                appName = appUsage.appName,
                appIcon = appUsage.appIcon,
                usageTime = appUsage.usageStats?.totalTimeInForeground ?: 0
            )
        }
    }
}
