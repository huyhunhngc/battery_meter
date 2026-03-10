package io.github.ifa.glancewidget.features.gopro

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.features.gopro.component.PlanPremiumCard
import io.github.ifa.glancewidget.features.gopro.component.PurchaseStateBottomSheet
import io.github.ifa.glancewidget.ui.component.PagerIndicator
import io.github.ifa.glancewidget.ui.theme.DynamicAnimatedTheme
import io.github.ifa.glancewidget.ui.theme.DynamicThemeConstants.AnimatedThemeDuration
import io.github.ifa.glancewidget.ui.theme.LocalDynamicAnimatedTheme
import io.github.ifa.glancewidget.utils.findActivity

const val paywallPremiumScreenRoute = "go_pro_screen_route"

fun NavGraphBuilder.paywallPremiumScreen(
    onNavigationIconClick: () -> Unit,
) {
    composable(paywallPremiumScreenRoute) {
        PaywallPremiumScreen(onNavigationIconClick = onNavigationIconClick)
    }
}

@Composable
fun PaywallPremiumScreen(
    viewModel: GoPremiumViewModel = hiltViewModel(),
    onNavigationIconClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()
    DynamicAnimatedTheme {
        PremiumPlan.Provide(premiumPlans = uiState.premiumPlanData) {
            PaywallPremiumScreen(
                uiState = uiState,
                onSelectYearlyPlan = { planId ->
                    viewModel.processSubscription(planId) { params ->
                        if (activity != null) {
                            launchBillingFlow(activity, params)
                        }
                    }
                },
                onSelectLifetimePlan = { planId ->
                    viewModel.processPurchases(planId) { params ->
                        if (activity != null) {
                            launchBillingFlow(activity, params)
                        }
                    }
                },
                hidePurchaseStateBottomSheet = { viewModel.hidePurchaseStateBottomSheet() },
                onNavigationIconClick = onNavigationIconClick
            )
        }
    }
}

@Composable
private fun PaywallPremiumScreen(
    uiState: GoPremiumViewModel.GoPremiumUiState,
    onSelectYearlyPlan: (String) -> Unit,
    onSelectLifetimePlan: (String) -> Unit,
    hidePurchaseStateBottomSheet: () -> Unit,
    onNavigationIconClick: () -> Unit,
) {
    val colorScheme = LocalDynamicAnimatedTheme.current
    val surfaceContainer by animateColor(colorScheme.surfaceContainer)
    val primaryContainer by animateColor(colorScheme.primaryContainer)
    val secondaryColor by animateColor(colorScheme.secondary)
    val primaryColor by animateColor(colorScheme.primary)
    val tertiaryColor by animateColor(colorScheme.tertiary)
    val tertiaryContainer by animateColor(colorScheme.tertiaryContainer)
    val premiumPlan = LocalPremiumPlan.current
    val pagerState = rememberPagerState(pageCount = { premiumPlan.size })
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(Brush.verticalGradient(listOf(surfaceContainer, primaryContainer)))
                }
            //.padding(16.dp),
        ) {
            LazyColumn(
                modifier = Modifier.padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(48.dp))
                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = stringResource(id = R.string.choose_your_plan),
                        style = MaterialTheme.typography.bodyMedium,
                        color = secondaryColor,
                    )
                    HorizontalPager(
                        state = pagerState,
                        pageSpacing = 8.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp),
                    ) { page ->
                        PlanPremiumCard(
                            pagerState = pagerState,
                            page = page,
                            onSelectLifetimePlan = onSelectLifetimePlan,
                            onSelectYearlyPlan = onSelectYearlyPlan
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
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.subscription_terms),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        color = secondaryColor
                    )
                    Image(
                        modifier = Modifier
                            .padding(16.dp)
                            .shadow(3.dp, RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .background(primaryContainer)
                            .size(80.dp),
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        colorFilter = ColorFilter.tint(primaryColor),
                        contentDescription = "app icon"
                    )
                }
            }
            IconButton(
                modifier = Modifier
                    .padding(top = 40.dp, start = 16.dp),
                onClick = onNavigationIconClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = tertiaryColor
                )
            }
            if (uiState.showPurchaseStateBottomSheet) {
                PurchaseStateBottomSheet(
                    purchaseState = uiState.purchaseState,
                    padding = padding,
                    onDismiss = hidePurchaseStateBottomSheet,
                )
            }
        }
    }
}

@Composable
fun animateColor(targetValue: Color): State<Color> {
    return animateColorAsState(
        targetValue = targetValue,
        label = "dynamic",
        animationSpec = tween(AnimatedThemeDuration)
    )
}
