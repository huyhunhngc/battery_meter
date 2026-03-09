package io.github.ifa.glancewidget.features.appusage.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.domain.UsageStatsWrapper
import io.github.ifa.glancewidget.features.appusage.AppUsageScreenUiState
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

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppUsageList(
    modifier: Modifier = Modifier,
    appUsageStats: List<UsageStatsWrapper>,
    usageRange: AppUsageScreenUiState.UsageRange,
    onUsageRangeChange: (AppUsageScreenUiState.UsageRange) -> Unit,
    onAppUsageClick: (String) -> Unit,
) {
    val options = remember {
        AppUsageScreenUiState.UsageRange.options()
    }
    val lazyListState = rememberLazyListState()
    val isHeaderStuck by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemScrollOffset > 0
        }
    }
    val headerBackgroundColor by animateColorAsState(
        targetValue = if (isHeaderStuck) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            MaterialTheme.colorScheme.background
        },
        label = "headerBackgroundColor"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        stickyHeader {
            SelectableChips(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBackgroundColor)
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                options = options.map { stringResource(it.displayName()) },
                selected = options.indexOf(usageRange),
                onSelect = {
                    onUsageRangeChange(options[it])
                }
            )
        }
        item {
            if (appUsageStats.isNotEmpty()) {
                AppUsageChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    appUsageStats = appUsageStats.take(4)
                )
            }
        }
        if (appUsageStats.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        items(
            items = appUsageStats,
            key = { appUsage -> appUsage.usageStats?.packageName.orEmpty() }
        ) { appUsage ->
            AppUsageItem(
                appName = appUsage.appName,
                appIcon = appUsage.appIcon,
                usageTime = appUsage.usageStats?.totalTimeInForeground ?: 0,
                modifier = Modifier.animateItem(
                    // Add animation here
                    fadeInSpec = tween(500), // Fade-in animation
                    fadeOutSpec = tween(500), // Fade-out animation
                    placementSpec = tween(500), // Slide-in animation
                ),
                onAppClick = {
                    onAppUsageClick(appUsage.usageStats?.packageName.orEmpty())
                }
            )
        }
    }
}
