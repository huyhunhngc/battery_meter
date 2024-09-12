package io.github.ifa.glancewidget.presentation.about

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.BuildConfig
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.ui.component.AnimatedTextTopAppBar
import io.github.ifa.glancewidget.utils.Constants.IFA_GITHUB_URL
import io.github.ifa.glancewidget.utils.Constants.IFA_SUPPORT_URL
import io.github.ifa.glancewidget.utils.Constants.IFA_TEAM_URL
import io.github.ifa.glancewidget.utils.sendMail

const val aboutScreenRoute = "about_screen_route"

fun NavGraphBuilder.aboutScreen(
    onNavigationIconClick: () -> Unit,
    onExternalUrlClick: Context.(String) -> Unit
) {
    composable(aboutScreenRoute) {
        AboutScreen(
            onNavigationIconClick = onNavigationIconClick,
            onExternalUrlClick = onExternalUrlClick
        )
    }
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AboutScreen(
    uiState: AboutViewModel.AboutScreenUiState,
    snackbarHostState: SnackbarHostState,
    onNavigationIconClick: () -> Unit,
    onExternalUrlClick: Context.(String) -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            AnimatedTextTopAppBar(
                title = stringResource(id = R.string.about_tab),
                navigationIcon = {
                    IconButton(onClick = { onNavigationIconClick() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            tint = MaterialTheme.colorScheme.tertiary,
                            contentDescription = "Back",
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .padding(24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .size(72.dp),
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = "app icon"
            )
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall,
                text = stringResource(
                    id = R.string.version,
                    "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                ),
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(32.dp))
            TonalButton(
                onClick = { context.sendMail() },
                icon = R.drawable.ic_mail,
                text = R.string.contact_us
            )
            Row {
                TonalButton(
                    onClick = { onExternalUrlClick(context, IFA_SUPPORT_URL) },
                    icon = R.drawable.ic_chat_info,
                    text = R.string.support
                )
                Spacer(modifier = Modifier.width(8.dp))
                TonalButton(
                    onClick = {
                        onExternalUrlClick(context, IFA_TEAM_URL)
                    },
                    icon = R.drawable.ic_language,
                    text = R.string.website
                )

            }
            Row(modifier = Modifier.padding(top = 16.dp)) {
                Image(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { onExternalUrlClick(context, IFA_GITHUB_URL) },
                    painter = painterResource(id = R.drawable.ic_github),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .size(72.dp),
                    painter = painterResource(id = R.drawable.img_ifa_logo),
                    contentDescription = null
                )
                Text(
                    text = "From IFA team with love ♥",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun TonalButton(
    onClick: () -> Unit,
    icon: Int,
    text: Int
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.filledTonalButtonColors()
            .copy(contentColor = MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "Website"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(id = text))
    }
}

@Preview
@Composable
fun AboutScreenPreview() {
    AboutScreen(onNavigationIconClick = {}, onExternalUrlClick = {})
}