package io.github.ifa.glancewidget.presentation.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.utils.buildUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    data class SettingsScreenUiState(
        val theme: ThemeType = ThemeType.FOLLOW_SYSTEM,
    )

    private val _themeType = MutableStateFlow(ThemeType.FOLLOW_SYSTEM)
    val uiState: StateFlow<SettingsScreenUiState> = buildUiState(_themeType) { themeType ->
        SettingsScreenUiState(themeType)
    }
    fun setThemeType(themeType: ThemeType) {
        _themeType.value = themeType
    }
}