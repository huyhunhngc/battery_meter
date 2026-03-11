package io.github.ifa.glancewidget.features.health.component

import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.BatterySaver
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.ChangeCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeCap.Companion
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.wrapper.BatteryDataWrapper
import io.github.ifa.glancewidget.ui.component.AnimatedCounter
import io.github.ifa.glancewidget.utils.Constants.MAH_UNIT

data class HealthItemData(
    @StringRes val label: Int,
    val value: String,
    val icon: ImageVector
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BatteryExtraInformation(
    modifier: Modifier = Modifier,
    batteryDataWrapper: BatteryDataWrapper,
) {
    val myDevice = batteryDataWrapper.batteryData.myDevice
    val batteryHealth = batteryDataWrapper.batteryHealth
    val extraBatteryInfo = batteryDataWrapper.extraBatteryInfo

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val wide = remember(windowSizeClass) {
        windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
        )
    }
    val columns = if (wide) 3 else 2

    val fullCapacity = extraBatteryInfo.fullChargeCapacity.toFloat()
    val designCapacity = extraBatteryInfo.capacity.toFloat()
    
    val percentRaw = if (designCapacity > 0) {
        ((fullCapacity / designCapacity) * 100).toInt()
    } else 0
    
    val percent = if (percentRaw in 1..100) percentRaw else 100

    val animatePercentFloat = remember { Animatable(0f) }
    LaunchedEffect(percent) {
        animatePercentFloat.animateTo(
            targetValue = percent / 100f,
            animationSpec = tween(durationMillis = 500, easing = LinearEasing)
        )
    }

    val contents = listOf(
        HealthItemData(R.string.technology, myDevice.technology, Icons.Rounded.Build),
        HealthItemData(R.string.health, stringResource(id = batteryHealth.type), Icons.Rounded.Favorite),
        HealthItemData(R.string.design_capacity, "${extraBatteryInfo.capacity} $MAH_UNIT", Icons.Rounded.BatterySaver),
        HealthItemData(R.string.full_charge_capacity, "${extraBatteryInfo.fullChargeCapacity} $MAH_UNIT", Icons.Rounded.BatteryFull),
        HealthItemData(R.string.charge_counter, "${extraBatteryInfo.chargeCounter} $MAH_UNIT", Icons.Rounded.Refresh),
        HealthItemData(R.string.cycle_count, myDevice.cycleCount.toString(), Icons.Rounded.ChangeCircle)
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularWavyProgressIndicator(
                progress = { animatePercentFloat.value },
                modifier = Modifier.size(180.dp),
                stroke = Stroke(
                    width = 16f,
                    cap = StrokeCap.Round,
                ),
                wavelength = 32.dp
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.Bottom) {
                    AnimatedCounter(
                        count = percent,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Text(
                    text = stringResource(id = R.string.capacity),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = columns
        ) {
            contents.forEach { item ->
                HealthItemCard(
                    modifier = Modifier.weight(1f),
                    item = item
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HealthItemCard(modifier: Modifier = Modifier, item: HealthItemData) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.largeIncreased)
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = item.label),
            color = MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text = item.value,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
fun BatteryExtraInformationPreview() {
    BatteryExtraInformation(
        batteryDataWrapper = BatteryDataWrapper()
    )
}
