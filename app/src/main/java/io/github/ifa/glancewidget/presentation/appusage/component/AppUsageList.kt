package io.github.ifa.glancewidget.presentation.appusage.component

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.domain.UsageStatsWrapper
import io.github.ifa.glancewidget.presentation.appusage.AppUsageScreenUiState
import io.github.ifa.glancewidget.ui.component.SelectableChips

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
    usageRange: AppUsageScreenUiState.UsageRange,
    onUsageRangeChange: (AppUsageScreenUiState.UsageRange) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = remember {
        AppUsageScreenUiState.UsageRange.options()
    }
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        item {
            SelectableChips(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                options = options.map { stringResource(it.displayName()) },
                selected = options.indexOf(usageRange),
                onSelect = {
                    onUsageRangeChange(options[it])
                }
            )
        }
        items(
            items = appUsageStats,
            key = { appUsage -> appUsage.usageStats?.packageName.orEmpty() }
        ) { appUsage ->
            AppUsageItem(
                appName = appUsage.appName,
                appIcon = appUsage.appIcon,
                usageTime = appUsage.usageStats?.totalTimeInForeground ?: 0,
                modifier = Modifier.animateItem( // Add animation here
                    fadeInSpec = tween(500), // Fade-in animation
                    fadeOutSpec = tween(500), // Fade-out animation
                    placementSpec = tween(500), // Slide-in animation
                )
            )
        }
    }
}
