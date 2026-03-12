package io.github.ifa.glancewidget.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
fun mergePaddingValues(
    topSource: PaddingValues,
    restSource: PaddingValues
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current

    return PaddingValues(
        top = topSource.calculateTopPadding(),
        bottom = restSource.calculateBottomPadding(),
        start = restSource.calculateStartPadding(layoutDirection),
        end = restSource.calculateEndPadding(layoutDirection)
    )
}
