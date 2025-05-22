package io.github.ifa.glancewidget.presentation.appusage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ifa.glancewidget.domain.AppUsageRepository
import io.github.ifa.glancewidget.domain.UsageStatsWrapper
import io.github.ifa.glancewidget.utils.buildUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppUsageViewModel @Inject constructor(
    private val appUsageRepository: AppUsageRepository
) : ViewModel() {
    data class AppUsageScreenUiState(
        val appUsageStats: List<UsageStatsWrapper> = emptyList(),
        val hasAppUsagePermission: Boolean = false
    )

    private val _hasAppUsagePermission = appUsageRepository.hasAppUsagePermission().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )
    private val _appUsageStatsWrapper = appUsageRepository.getAppUsageStats().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val uiState: StateFlow<AppUsageScreenUiState> = buildUiState(
        _appUsageStatsWrapper, _hasAppUsagePermission
    ) { appUsageStats, hasAppUsagePermission ->
        AppUsageScreenUiState(
            appUsageStats = appUsageStats,
            hasAppUsagePermission = hasAppUsagePermission
        )
    }
}