package io.github.ifa.glancewidget.presentation.about

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ifa.glancewidget.utils.buildUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor() : ViewModel() {
    data class AboutScreenUiState(
        val appVersion: String = "",
    )
    private val _appVersion = MutableStateFlow("")

    val uiState: StateFlow<AboutScreenUiState> = buildUiState(_appVersion) { appVersion ->
        AboutScreenUiState(appVersion)
    }

}