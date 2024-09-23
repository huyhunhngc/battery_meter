package io.github.ifa.glancewidget.presentation.gopro

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class GoPremiumViewModel @Inject constructor(): ViewModel() {
    data class GoPremiumUiState(
        val plan: String
    )

    val uiState = MutableStateFlow(GoPremiumUiState(plan = "Free"))

}