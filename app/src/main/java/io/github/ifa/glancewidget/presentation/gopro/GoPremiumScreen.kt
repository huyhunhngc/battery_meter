package io.github.ifa.glancewidget.presentation.gopro

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.presentation.gopro.component.PlanPremiumCard
import io.github.ifa.glancewidget.ui.component.PagerIndicator
import io.github.ifa.glancewidget.ui.theme.DynamicAnimatedTheme
import io.github.ifa.glancewidget.ui.theme.DynamicThemeConstants.AnimatedThemeDuration
import io.github.ifa.glancewidget.ui.theme.LocalDynamicAnimatedTheme

const val goPremiumScreenRoute = "go_pro_screen_route"

fun NavGraphBuilder.goPremiumScreen(
    onNavigationIconClick: () -> Unit,
) {
    composable(goPremiumScreenRoute) {
        GoPremiumScreen(onNavigationIconClick = onNavigationIconClick)
    }
}

@Composable
fun GoPremiumScreen(
    viewModel: GoPremiumViewModel = hiltViewModel(),
    onNavigationIconClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DynamicAnimatedTheme {
        PremiumPlan.Provide {
            GoPremiumScreen(uiState, onNavigationIconClick)
        }
    }
}

@Composable
private fun GoPremiumScreen(
    uiState: GoPremiumViewModel.GoPremiumUiState,
    onNavigationIconClick: () -> Unit,
) {
    val colorScheme = LocalDynamicAnimatedTheme.current
    val surfaceContainer by animateColor(colorScheme.surfaceContainer)
    val primaryContainer by animateColor(colorScheme.primaryContainer)
    val secondaryColor by animateColor(colorScheme.secondary)
    val primaryColor by animateColor(colorScheme.primary)
    val tertiaryColor by animateColor(colorScheme.tertiary)
    val premiumPlan = LocalPremiumPlan.current
    val pagerState = rememberPagerState(pageCount = { premiumPlan.size })
    Scaffold { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(Brush.verticalGradient(listOf(surfaceContainer, primaryContainer)))
                }
                .padding(16.dp),
        ) {


            LazyColumn(
                modifier = Modifier.padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Image(
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(primaryContainer)
                            .size(80.dp),
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        colorFilter = ColorFilter.tint(primaryColor),
                        contentDescription = "app icon"
                    )
                    Text(
                        text = stringResource(id = R.string.choose_your_plan),
                        style = MaterialTheme.typography.bodyMedium,
                        color = secondaryColor,
                    )
                    HorizontalPager(
                        state = pagerState,
                        pageSpacing = 8.dp,
                    ) { page ->
                        PlanPremiumCard(
                            pagerState = pagerState,
                            page = page,
                            onSelectLifetimePlan = {},
                            onSelectYearlyPlan = {}
                        )
                    }
                    PagerIndicator(
                        pagerState = pagerState,
                        contentColor = primaryColor,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                item {
                    Text(
                        text = stringResource(id = R.string.subscription_terms),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        color = secondaryColor
                    )
                }
            }
            IconButton(
                modifier = Modifier.padding(top = 24.dp),
                onClick = onNavigationIconClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = tertiaryColor
                )
            }
        }
    }
}

@Composable
fun animateColor(targetValue: Color): State<Color> {
    return animateColorAsState(
        targetValue = targetValue,
        label = "",
        animationSpec = tween(AnimatedThemeDuration)
    )
}
