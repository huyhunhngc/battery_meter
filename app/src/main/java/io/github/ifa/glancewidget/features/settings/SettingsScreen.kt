package io.github.ifa.glancewidget.features.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AdaptStrategy
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.BuildConfig
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.features.settings.about.AboutScreen
import io.github.ifa.glancewidget.features.main.MainScreenTab
import io.github.ifa.glancewidget.features.settings.component.LanguageSetting
import io.github.ifa.glancewidget.model.AppSettings
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
    val expansionState = rememberPaneExpansionState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    BackHandler(navigator.canNavigateBack()) {
        scope.launch {
            navigator.navigateBack()
        }
    }

    SupportingPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
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
                )
            }
        },
        supportingPane = {
            AnimatedPane {
                when (navigator.currentDestination?.contentKey) {
                    SettingsPane.About -> {
                        AboutScreen(
                            onNavigationIconClick = {
                                scope.launch { navigator.navigateBack() }
                            },
                            onExternalUrlClick = { navigateUrl(it) }
                        )
                    }
                    SettingsPane.ThemeSettings -> {
                        ThemeSettingsScreen(
                            uiState = uiState,
                            onNavigationIconClick = {
                                scope.launch { navigator.navigateBack() }
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
                            onNavigationIconClick = {
                                scope.launch { navigator.navigateBack() }
                            },
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
        paneExpansionDragHandle = {
            val interactionSource = remember { MutableInteractionSource() }
            VerticalDragHandle(
                modifier = Modifier
                    .paneExpansionDraggable(
                        expansionState,
                        LocalMinimumInteractiveComponentSize.current,
                        interactionSource
                    )
                    .systemGestureExclusion()
            )
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isAppCompatLocaleDeprecated()) {
                item {
                    LanguageSetting(onSelectLanguage = onSelectLanguage, uiState = uiState)
                }
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
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    supportingContent = {
                        Text(text = stringResource(R.string.theme_settings_desc))
                    },
                    shapes = ListItemDefaults.segmentedShapes(0, 1, singleItemListItemShapes),
                    colors = listItemColor,
                    onClick = onOpenThemeSettings,
                ) {
                    Text(stringResource(id = R.string.theme))
                }
            }
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
                        Text(stringResource(id = R.string.notification_settings))
                    },
                    shapes = ListItemDefaults.segmentedShapes(0, 1, singleItemListItemShapes),
                    colors = listItemColor,
                    onClick = onOpenWidgetSettings,
                ) {
                    Text(stringResource(id = R.string.widget_tab))
                }
            }
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
