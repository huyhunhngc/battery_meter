package io.github.ifa.glancewidget.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val containerColorAlpha60
    @Composable get() = colorScheme.surfaceContainer.copy(alpha = 0.6f)

val topBarColors: TopAppBarColors
    @Composable get() =
        TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.surfaceContainer,
            scrolledContainerColor = colorScheme.surfaceContainer
        )