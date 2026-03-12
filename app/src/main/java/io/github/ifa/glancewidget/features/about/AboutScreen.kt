package io.github.ifa.glancewidget.features.about

import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material.icons.rounded.Support
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
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
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.ui.component.AnimatedTextTopAppBar
import io.github.ifa.glancewidget.ui.component.TextWithRightArrow
import io.github.ifa.glancewidget.ui.theme.topBarColors
import io.github.ifa.glancewidget.utils.Constants.IFA_GITHUB_URL
import io.github.ifa.glancewidget.utils.Constants.IFA_LICENSES_URL
import io.github.ifa.glancewidget.utils.Constants.IFA_SUPPORT_URL
import io.github.ifa.glancewidget.utils.Constants.IFA_TEAM_URL
import io.github.ifa.glancewidget.utils.cookieShape
import io.github.ifa.glancewidget.utils.navigateLicencesScreen
import io.github.ifa.glancewidget.utils.navigateUrl
import io.github.ifa.glancewidget.utils.sendMail

@Composable
fun AboutScreen(
    viewModel: AboutViewModel = hiltViewModel(),
    onNavigationIconClick: () -> Unit,
    onExternalUrlClick: Context.(String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    AboutScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNavigationIconClick = onNavigationIconClick,
        onExternalUrlClick = onExternalUrlClick
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AboutScreen(
    uiState: AboutViewModel.AboutScreenUiState,
    snackbarHostState: SnackbarHostState,
    onNavigationIconClick: () -> Unit,
    onExternalUrlClick: Context.(String) -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val onOpenLicensesScreen = {
        context.navigateLicencesScreen()
    }
    val onOpenPrivacyPolicy = {
        context.navigateUrl(IFA_LICENSES_URL)
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, topBar = {
        AnimatedTextTopAppBar(
            title = stringResource(id = R.string.about_tab), navigationIcon = {
                IconButton(
                    onClick = { onNavigationIconClick() },
                    colors = IconButtonDefaults.iconButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = "Back",
                    )
                }
            }, scrollBehavior = scrollBehavior
        )
    }, containerColor = topBarColors.containerColor
    ) { padding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.largeIncreased),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.size(64.dp)) {
                            val infiniteTransition = rememberInfiniteTransition(label = "rotate")
                            val rotation by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(
                                        durationMillis = 4000, easing = LinearEasing
                                    )
                                ),
                                label = "rotation"
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .rotate(rotation)
                                    .clip(cookieShape())
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                            )

                            Icon(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(48.dp),
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                tint = MaterialTheme.colorScheme.secondary,
                                contentDescription = "app icon"
                            )
                        }
                        Column {
                            Text(
                                text = stringResource(id = R.string.app_name),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                style = MaterialTheme.typography.bodySmall,
                                text = stringResource(
                                    id = R.string.version, uiState.appVersion
                                ),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.largeIncreased)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .size(64.dp),
                        ) {
                            Icon(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(48.dp),
                                painter = painterResource(id = R.drawable.img_dotsdev),
                                tint = MaterialTheme.colorScheme.secondary,
                                contentDescription = "app icon"
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Dotsdev", style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                style = MaterialTheme.typography.bodySmall,
                                text = "@huyhunhngc from IFATeam",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TonalButton(
                                    onClick = { context.sendMail() },
                                    imageVector = Icons.Rounded.Mail,
                                )
                                TonalButton(
                                    onClick = { onExternalUrlClick(context, IFA_SUPPORT_URL) },
                                    imageVector = Icons.Rounded.Support,
                                )

                                TonalButton(
                                    onClick = {
                                        onExternalUrlClick(context, IFA_TEAM_URL)
                                    },
                                    icon = R.drawable.ic_globe,
                                )
                                TonalButton(
                                    onClick = {
                                        onExternalUrlClick(context, IFA_GITHUB_URL)
                                    },
                                    icon = R.drawable.ic_github,
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.largeIncreased),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    TextWithRightArrow(
                        text = stringResource(id = R.string.license),
                        icon = painterResource(id = R.drawable.ic_license),
                        onClick = onOpenLicensesScreen
                    )
                    TextWithRightArrow(
                        text = stringResource(id = R.string.privacy_policy),
                        icon = painterResource(id = R.drawable.ic_policy),
                        rightIcon = Icons.AutoMirrored.Rounded.OpenInNew,
                        onClick = onOpenPrivacyPolicy
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TonalButton(
    onClick: () -> Unit,
    icon: Int? = null,
    imageVector: ImageVector? = null,
) {
    FilledTonalIconButton(
        onClick = onClick, modifier = Modifier.width(48.dp)
    ) {
        if (icon != null) {
            Icon(
                painter = painterResource(id = icon),
                modifier = Modifier.size(20.dp),
                contentDescription = "Website"
            )
        } else {
            Icon(
                imageVector = imageVector ?: Icons.Rounded.QuestionMark,
                modifier = Modifier.size(20.dp),
                contentDescription = "Website"
            )
        }
    }
}

@Preview
@Composable
fun AboutScreenPreview() {
    AboutScreen(onNavigationIconClick = {}, onExternalUrlClick = {})
}