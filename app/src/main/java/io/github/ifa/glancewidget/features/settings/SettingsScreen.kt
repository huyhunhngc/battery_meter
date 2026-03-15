package io.github.ifa.glancewidget.features.settings

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AdaptStrategy
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneExpansionAnchor
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.NavigableSupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import io.github.ifa.glancewidget.BuildConfig
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.features.settings.about.AboutScreen
import io.github.ifa.glancewidget.features.main.MainScreenTab
import io.github.ifa.glancewidget.features.settings.component.LanguageSetting
import io.github.ifa.glancewidget.features.settings.component.TemperatureUnitSetting
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.ui.theme.topBarColors
import io.github.ifa.glancewidget.utils.combinePadding
import io.github.ifa.glancewidget.utils.isAppCompatLocaleDeprecated
import io.github.ifa.glancewidget.utils.listItemColor
import io.github.ifa.glancewidget.utils.navigateUrl
import io.github.ifa.glancewidget.utils.singleItemListItemShapes
import io.github.ifa.glancewidget.utils.syncShowPairedDevicesToWidget
import io.github.ifa.glancewidget.utils.syncThemeColorToWidget
import io.github.ifa.glancewidget.utils.syncThemeToWidget
import kotlinx.coroutines.launch

const val settingsScreenRoute = "settings_screen_route"

fun NavGraphBuilder.settingsScreen(
    contentPadding: PaddingValues,
) {
    composable(settingsScreenRoute) {
        SettingsSupportingPaneScreen(
            contentPadding = contentPadding,
        )
    }
}

enum class SettingsPane {
    About, WidgetSettings, ThemeSettings
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SettingsSupportingPaneScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    contentPadding: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val navigator = rememberSupportingPaneScaffoldNavigator(
        adaptStrategies = SupportingPaneScaffoldDefaults.adaptStrategies(supportingPaneAdaptStrategy = AdaptStrategy.Hide)
    )
    val scope = rememberCoroutineScope()
    val backNavigationBehavior = BackNavigationBehavior.PopUntilScaffoldValueChange
    val context = LocalContext.current
    val expansionState = rememberPaneExpansionState(
        anchors = listOf(PaneExpansionAnchor.Proportion(0.45f)),
        initialAnchoredIndex = 0
    )
    val widthExpanded = currentWindowAdaptiveInfo()
        .windowSizeClass
        .isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

    NavigableSupportingPaneScaffold(
        navigator = navigator,
        mainPane = {
            AnimatedPane {
                SettingsScreenLayout(
                    uiState = uiState,
                    snackbarHostState = snackbarHostState,
                    contentPadding = contentPadding,
                    onOpenAboutScreen = {
                        scope.launch {
                            navigator.navigateTo(SupportingPaneScaffoldRole.Supporting, SettingsPane.About)
                        }
                    },
                    onOpenWidgetSettings = {
                        scope.launch {
                            navigator.navigateTo(SupportingPaneScaffoldRole.Supporting, SettingsPane.WidgetSettings)
                        }
                    },
                    onOpenThemeSettings = {
                        scope.launch {
                            navigator.navigateTo(SupportingPaneScaffoldRole.Supporting, SettingsPane.ThemeSettings)
                        }
                    },
                    onSelectLanguage = viewModel::setLanguage,
                    onSelectTemperatureUnit = viewModel::setTemperatureUnit,
                )
            }
        },
        supportingPane = {
            AnimatedPane {
                when (navigator.currentDestination?.contentKey) {
                    null -> {
                        DetailPlaceholder(R.drawable.ic_settings_filled)
                    }
                    SettingsPane.About -> {
                        AboutScreen(
                            contentPadding = contentPadding,
                            showNavigationIcon = !widthExpanded,
                            onNavigationIconClick = {
                                scope.launch { navigator.navigateBack(backNavigationBehavior) }
                            },
                            onExternalUrlClick = { navigateUrl(it) }
                        )
                    }
                    SettingsPane.ThemeSettings -> {
                        ThemeSettingsScreen(
                            uiState = uiState,
                            showNavigationIcon = !widthExpanded,
                            onNavigationIconClick = {
                                scope.launch { navigator.navigateBack(backNavigationBehavior) }
                            },
                            onSelectTheme = { theme ->
                                viewModel.setThemeType(theme)
                                context.syncThemeToWidget(theme)
                            },
                            onSelectThemeColor = { themeColor ->
                                viewModel.setThemeTypeColor(themeColor)
                                context.syncThemeColorToWidget(themeColor)
                            },
                            onEnableBlackDark = viewModel::setEnableBlackDark
                        )
                    }
                    SettingsPane.WidgetSettings -> {
                        WidgetSettingsScreen(
                            uiState = uiState,
                            showNavigationIcon = !widthExpanded,
                            contentPadding = contentPadding,
                            onNavigationIconClick = {
                                scope.launch { navigator.navigateBack(backNavigationBehavior) }
                            },
                            onSetDeviceShowInWidgetChanged = viewModel::updateDeviceShowInWidget,
                            onSetNotificationEnabled = viewModel::onBatteryAlertChanged,
                            onSetShowPairedDevice = { enabled ->
                                context.syncShowPairedDevicesToWidget(enabled)
                                viewModel.onShowPairedDeviceChanged(enabled)
                            },
                        )
                    }
                }
            }
        },
        paneExpansionState = expansionState
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun SettingsScreenLayout(
    uiState: SettingsViewModel.SettingsScreenUiState,
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    onOpenAboutScreen: () -> Unit,
    onOpenWidgetSettings: () -> Unit,
    onOpenThemeSettings: () -> Unit,
    onSelectLanguage: (AppSettings.Language) -> Unit,
    onSelectTemperatureUnit: (MyDevice.Temperature.TemperatureUnit) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = MainScreenTab.Settings.label),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                subtitle = {},
                titleHorizontalAlignment = Alignment.CenterHorizontally,
                scrollBehavior = scrollBehavior,
                colors = topBarColors
            )
        },
        containerColor = topBarColors.containerColor,
    ) { padding ->
        val insets = combinePadding(padding, contentPadding)
        LazyColumn(
            contentPadding = insets,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            if (!isAppCompatLocaleDeprecated()) {
                item {
                    LanguageSetting(onSelectLanguage = onSelectLanguage, uiState = uiState)
                }
            }
            item {
                TemperatureUnitSetting(onSelectTemperatureUnit = onSelectTemperatureUnit, uiState = uiState)
            }
            item {
                SegmentedListItem(
                    leadingContent = {
                        Icon(painterResource(R.drawable.ic_brightness_auto), contentDescription = null)
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = colorScheme.primary
                        )
                    },
                    supportingContent = {
                        Text(text = stringResource(R.string.theme_settings_desc))
                    },
                    shapes = ListItemDefaults.segmentedShapes(2, 3),
                    colors = listItemColor,
                    onClick = onOpenThemeSettings,
                ) {
                    Text(stringResource(id = R.string.appearance))
                }
            }
            item { Spacer(modifier = Modifier.height(14.dp)) }
            item {
                SegmentedListItem(
                    leadingContent = {
                        Icon(painterResource(R.drawable.ic_widgets_filled), contentDescription = null)
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    supportingContent = {
                        Text(stringResource(id = R.string.notification_settings_desc))
                    },
                    shapes = ListItemDefaults.segmentedShapes(0, 1, singleItemListItemShapes),
                    colors = listItemColor,
                    onClick = onOpenWidgetSettings,
                ) {
                    Text(stringResource(id = R.string.notification_settings))
                }
            }
            item { Spacer(modifier = Modifier.height(14.dp)) }
            item {
                SegmentedListItem(
                    leadingContent = {
                        Icon(painterResource(R.drawable.ic_info_filled), contentDescription = null)
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    supportingContent = {
                        Text(
                            text = BuildConfig.VERSION_NAME,
                        )
                    },
                    shapes = ListItemDefaults.segmentedShapes(0, 1, singleItemListItemShapes),
                    colors = listItemColor,
                    onClick = onOpenAboutScreen,
                ) {
                    Text(stringResource(id = R.string.about_tab))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailPlaceholder(
    @DrawableRes icon: Int,
    background: Color = colorScheme.surfaceContainerLow
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                Spacer(
                    Modifier
                        .background(
                            colorScheme.secondaryContainer,
                            MaterialShapes.Cookie12Sided.toShape()
                        )
                        .size(128.dp)
                )
                Icon(
                    painterResource(icon),
                    contentDescription = null,
                    tint = colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .size(72.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenLayoutPreview() {
    MaterialTheme {
        SettingsScreenLayout(
            uiState = SettingsViewModel.SettingsScreenUiState(),
            snackbarHostState = remember { SnackbarHostState() },
            contentPadding = PaddingValues(),
            onOpenAboutScreen = {},
            onOpenWidgetSettings = {},
            onOpenThemeSettings = {},
            onSelectLanguage = {},
            onSelectTemperatureUnit = {}
        )
    }
}
