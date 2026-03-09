package io.github.ifa.glancewidget.features.appusage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.domain.AppUsageRepository
import io.github.ifa.glancewidget.domain.UsageStatsWrapper
import io.github.ifa.glancewidget.utils.buildUiState
import io.github.ifa.glancewidget.utils.getEndOfDay
import io.github.ifa.glancewidget.utils.getStartOfDay
import io.github.ifa.glancewidget.utils.subtractDays
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AppUsageViewModel @Inject constructor(
    private val appUsageRepository: AppUsageRepository
) : ViewModel() {
    private val _usageRange = MutableStateFlow(AppUsageScreenUiState.UsageRange.TODAY)

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
        _appUsageStatsWrapper, _hasAppUsagePermission, _usageRange
    ) { appUsageStats, hasAppUsagePermission, usageRange ->
        AppUsageScreenUiState(
            appUsageStats = appUsageStats,
            usageRange = usageRange,
            hasAppUsagePermission = hasAppUsagePermission
        )
    }

    fun setUsageRange(usageRange: AppUsageScreenUiState.UsageRange) {
        _usageRange.value = usageRange
        val (startTime, endTime) = usageRange.getRangeTime()
        appUsageRepository.onChangedUsageRange(startTime, endTime)
    }
}

data class AppUsageScreenUiState(
    val appUsageStats: List<UsageStatsWrapper> = emptyList(),
    val usageRange: UsageRange = UsageRange.TODAY,
    val hasAppUsagePermission: Boolean = false
) {
    enum class UsageRange {
        TODAY, YESTERDAY, LAST_7_DAYS;

        fun displayName(): Int {
            return when (this) {
                TODAY -> R.string.today
                YESTERDAY -> R.string.yesterday
                LAST_7_DAYS -> R.string.last_seven_day
            }
        }

        fun getRangeTime(): Pair<Long, Long> {
            return when (this) {
                TODAY -> {
                    val startTime = Calendar.getInstance().getStartOfDay().timeInMillis
                    val endTime = Calendar.getInstance().timeInMillis
                    Pair(startTime, endTime)
                }

                YESTERDAY -> {
                    val endTime = Calendar.getInstance()
                        .subtractDays(1)
                        .getEndOfDay()
                        .timeInMillis
                    val startTime = Calendar.getInstance()
                        .subtractDays(1)
                        .getStartOfDay().timeInMillis
                    Pair(startTime, endTime)
                }

                LAST_7_DAYS -> {
                    val startTime = Calendar.getInstance().subtractDays(7).timeInMillis
                    val endTime = Calendar.getInstance().timeInMillis
                    Pair(startTime, endTime)
                }
            }
        }

        companion object {
            fun options(): List<UsageRange> {
                return listOf(
                    TODAY, YESTERDAY, LAST_7_DAYS
                )
            }
        }
    }
}