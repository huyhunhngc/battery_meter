package io.github.ifa.glancewidget.presentation.gopro.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.presentation.gopro.LocalPremiumPlan
import io.github.ifa.glancewidget.presentation.gopro.PremiumPlanData
import io.github.ifa.glancewidget.presentation.gopro.animateColor
import io.github.ifa.glancewidget.ui.component.TextWithIcon
import io.github.ifa.glancewidget.ui.theme.LocalDynamicAnimatedTheme
import kotlin.math.absoluteValue

@Composable
fun PlanPremiumCard(
    pagerState: PagerState,
    page: Int,
    onSelectYearlyPlan: (String) -> Unit = {},
    onSelectLifetimePlan: (String) -> Unit = {}
) {
    val premiumPlanData = LocalPremiumPlan.current
    val plan = remember(page, premiumPlanData) {
        premiumPlanData[page]
    }
    GradientCard(pagerState = pagerState, page = page) {
        Title(text = stringResource(id = plan.title))
        Features(features = plan.features)
        Description(text = stringResource(id = R.string.free_trial_description))
        PrimaryButton(
            text = stringResource(id = plan.primaryAction.text),
            description = stringResource(
                id = plan.primaryAction.description, plan.primaryAction.price
            ),
            enabled = plan.primaryAction.planId.isNotEmpty(),
            onClick = {
                onSelectYearlyPlan(plan.primaryAction.planId)
            }
        )
        SecondaryButton(
            text = stringResource(id = plan.secondaryAction.text),
            description = stringResource(
                id = plan.secondaryAction.description, plan.secondaryAction.price
            ),
            enabled = plan.secondaryAction.planId.isNotEmpty(),
            onClick = {
                onSelectLifetimePlan(plan.secondaryAction.planId)
            }
        )
    }
}


@Composable
private fun GradientCard(
    pagerState: PagerState,
    page: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    val colorScheme = LocalDynamicAnimatedTheme.current
    val surfaceContainerColor by animateColor(colorScheme.surfaceContainerHigh)
    val tertiaryContainerColor by animateColor(colorScheme.tertiaryContainer)
    val secondaryContainerColor by animateColor(colorScheme.secondaryContainer)
    val containerColor = if (page % 2 == 0) {
        listOf(secondaryContainerColor, tertiaryContainerColor)
    } else {
        listOf(surfaceContainerColor, tertiaryContainerColor)
    }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .graphicsLayer {
                val pageOffset =
                    ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                        .absoluteValue
                alpha = lerp(
                    start = 0.5f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                )
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors().copy(containerColor = surfaceContainerColor)
    ) {
        Column(
            modifier = Modifier
                .background(Brush.verticalGradient(colors = containerColor))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            content()
        }
    }
}


@Composable
private fun Title(text: String) {
    val colorScheme = LocalDynamicAnimatedTheme.current
    val primaryColor by animateColor(colorScheme.primary)
    Row(
        modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(28.dp),
            painter = painterResource(id = R.drawable.ic_workspace_premium),
            colorFilter = ColorFilter.tint(primaryColor),
            contentDescription = "app icon"
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = primaryColor
        )
    }
}

@Composable
private fun Description(text: String) {
    val colorScheme = LocalDynamicAnimatedTheme.current
    val secondaryColor by animateColor(colorScheme.secondary)
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        textAlign = TextAlign.Center,
        color = secondaryColor,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun Features(features: List<PremiumPlanData.Feature>) {
    val colorScheme = LocalDynamicAnimatedTheme.current
    val secondaryColor by animateColor(colorScheme.secondary)
    Column(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        features.forEach {
            TextWithIcon(
                text = stringResource(id = it.text),
                color = secondaryColor,
                icon = painterResource(id = it.icon)
            )
        }
    }
}

@Composable
private fun PrimaryButton(text: String, description: String, enabled: Boolean, onClick: () -> Unit) {
    val colorScheme = LocalDynamicAnimatedTheme.current
    val primaryColor by animateColor(colorScheme.primary)
    val onPrimaryColor by animateColor(colorScheme.onPrimary)
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            contentColor = onPrimaryColor,
            containerColor = primaryColor
        )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun SecondaryButton(text: String, description: String, enabled: Boolean, onClick: () -> Unit) {
    val colorScheme = LocalDynamicAnimatedTheme.current
    val primaryColor by animateColor(colorScheme.primary)
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = primaryColor
        ),
        border = BorderStroke(1.5.dp, primaryColor)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
