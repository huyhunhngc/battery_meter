package io.github.ifa.glancewidget.presentation.widget.component

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.presentation.widget.wattsmonitor.WattsDetailDestination
import io.github.ifa.glancewidget.ui.component.CircleProgressBar
import io.github.ifa.glancewidget.ui.localcomposition.LocalAnimatedVisibilityScope
import io.github.ifa.glancewidget.ui.localcomposition.LocalSharedTransitionScope
import io.github.ifa.glancewidget.utils.Constants.WATT_UNIT

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("DefaultLocale")
@Composable
fun WattsMonitor(
    modifier: Modifier,
    power: Float,
    powerPercentage: Float
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedScope = LocalAnimatedVisibilityScope.current
    val sharedModifier = if (sharedTransitionScope != null && animatedScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedElement(
                state = rememberSharedContentState(key = WattsDetailDestination.STATE_KEY),
                animatedVisibilityScope = animatedScope,
            )
        }
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
    ) {
        CircleProgressBar(
            modifier = sharedModifier.fillMaxSize().padding(8.dp),
            value = String.format("%.1f", power),
            label = WATT_UNIT,
            progressPercentage = powerPercentage
        )
    }
}