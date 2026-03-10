package io.github.ifa.glancewidget.features.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.window.core.layout.WindowSizeClass
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.features.settings.settingsScreenRoute
import io.github.ifa.glancewidget.features.widget.widgetScreenRoute

enum class MainScreenTab(
    val route: String,
    @param:StringRes val label: Int,
    @param:DrawableRes val selectedIcon: Int,
    @param:DrawableRes val icon: Int,
) {
    Widget(
        widgetScreenRoute, R.string.widget_tab, R.drawable.ic_analytics_filled, R.drawable.ic_analytics
    ),
    Settings(
        settingsScreenRoute, R.string.settings, R.drawable.ic_setting_filled, R.drawable.ic_settings
    );
}

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun FloatBottomBar(
    navController: NavController,
    colorScheme: ColorScheme
) {
    val layoutDirection = LocalLayoutDirection.current
    val motionScheme = motionScheme
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val systemBarsInsets = WindowInsets.systemBars.asPaddingValues()
    val cutoutInsets = WindowInsets.displayCutout.asPaddingValues()
    val wide = remember {
        windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
        )
    }
    val toolbarScrollBehavior = FloatingToolbarDefaults.exitAlwaysScrollBehavior(
        FloatingToolbarExitDirection.Bottom
    )

    Box(
        Modifier
            .fillMaxWidth()
            .padding(
                start = cutoutInsets.calculateStartPadding(layoutDirection),
                end = cutoutInsets.calculateEndPadding(layoutDirection)
            ), Alignment.Center
    ) {
        HorizontalFloatingToolbar(
            expanded = true,
            scrollBehavior = toolbarScrollBehavior,
            colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(
                toolbarContainerColor = colorScheme.primaryContainer,
                toolbarContentColor = colorScheme.onPrimaryContainer
            ),
            modifier = Modifier
                .padding(
                    top = ScreenOffset,
                    bottom = systemBarsInsets.calculateBottomPadding() + ScreenOffset
                )
                .zIndex(1f)
        ) {
            MainScreenTab.entries.fastForEach { item ->
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val selected by remember(item) {
                    derivedStateOf { navBackStackEntry?.destination?.route == item.route }
                }
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Above
                    ),
                    tooltip = { PlainTooltip { Text(stringResource(item.label)) } },
                    state = rememberTooltipState(),
                ) {
                    ToggleButton(
                        checked = selected,
                        onCheckedChange = {
                            navController.navigate(item.route) {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) { saveState = true }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = ToggleButtonDefaults.toggleButtonColors(
                            containerColor = colorScheme.primaryContainer,
                            contentColor = colorScheme.onPrimaryContainer,
                            checkedContainerColor = colorScheme.primary,
                            checkedContentColor = colorScheme.onPrimary
                        ),
                        shapes = ToggleButtonDefaults.shapes(
                            CircleShape, CircleShape, CircleShape
                        ),
                        modifier = Modifier.height(56.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Crossfade(selected) {
                                if (it) Icon(
                                    painterResource(item.selectedIcon), stringResource(item.label)
                                )
                                else Icon(
                                    painterResource(item.icon), stringResource(item.label)
                                )
                            }
                            AnimatedVisibility(
                                visible = selected || wide,
                                enter = expandHorizontally(motionScheme.defaultSpatialSpec()),
                                exit = shrinkHorizontally(motionScheme.defaultSpatialSpec())
                            ) {
                                Text(
                                    text = stringResource(item.label),
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Clip,
                                    modifier = Modifier.padding(start = ButtonDefaults.IconSpacing)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}