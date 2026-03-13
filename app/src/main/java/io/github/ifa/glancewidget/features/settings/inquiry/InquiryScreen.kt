package io.github.ifa.glancewidget.features.settings.inquiry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.BonnedDeviceSettings
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.model.ThemeTypeColor
import io.github.ifa.glancewidget.ui.component.AnimatedTextTopAppBar
import io.github.ifa.glancewidget.ui.theme.AppTheme
import io.github.ifa.glancewidget.ui.theme.topBarColors
import io.github.ifa.glancewidget.utils.sendInquiryEmail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

const val inquiryScreenRoute = "inquiry_screen_route"

fun NavGraphBuilder.inquiryScreen(
    onNavigationIconClick: () -> Unit,
) {
    composable(inquiryScreenRoute) {
        InquiryScreen(
            onNavigationIconClick = onNavigationIconClick
        )
    }
}

fun NavController.navigateToInquiryScreen() {
    navigate(inquiryScreenRoute)
}

@Composable
fun InquiryScreen(
    viewModel: InquiryViewModel = hiltViewModel(),
    onNavigationIconClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    InquiryScreen(
        uiState = uiState,
        onNavigationIconClick = onNavigationIconClick,
        onTitleChange = viewModel::updateTitle,
        onDescriptionChange = viewModel::updateDescription
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun InquiryScreen(
    uiState: InquiryUiState,
    onNavigationIconClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AnimatedTextTopAppBar(
                title = stringResource(R.string.inquiry),
                navigationIcon = {
                    IconButton(
                        onClick = onNavigationIconClick,
                        colors = IconButtonDefaults.iconButtonColors().copy(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = "Back",
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = topBarColors.containerColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.feedback_or_bug_report),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = uiState.title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.title)) },
                singleLine = true,
                shape = MaterialTheme.shapes.largeIncreased,
                colors = OutlinedTextFieldDefaults.colors().copy(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                )
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth().weight(1f).padding(bottom = 16.dp),
                placeholder = { Text(stringResource(R.string.describe_the_issue_or_bug)) },
                shape = MaterialTheme.shapes.largeIncreased,
                colors = OutlinedTextFieldDefaults.colors().copy(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                )
            )

            Button(
                onClick = {
                    context.sendInquiryEmail(
                        title = uiState.title,
                        description = uiState.description
                    )
                },
                shape = MaterialTheme.shapes.largeIncreased,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = uiState.title.isNotBlank() && uiState.description.isNotBlank()
            ) {
                Text(stringResource(R.string.send))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InquiryScreenPreview() {
    val mockAppSettingsRepository = object : AppSettingsRepository {
        override fun get(): Flow<AppSettings> = flowOf(AppSettings())
        override suspend fun getAppSettings(): AppSettings = AppSettings()
        override suspend fun saveLocaleLanguage(language: AppSettings.Language) {}
        override suspend fun saveTheme(themeType: ThemeType) {}
        override suspend fun saveThemeColor(themeTypeColor: ThemeTypeColor) {}
        override suspend fun saveNotificationSetting(notificationSetting: AppSettings.NotificationSetting) {}
        override suspend fun saveShowPairedDevicesSetting(showPairedDevices: Boolean) {}
        override suspend fun saveBondedDeviceSetting(macAddress: String, showInWidget: Boolean) {}
        override suspend fun saveEnableBlackDark(enabled: Boolean) {}
        override suspend fun saveTemperatureUnit(temperatureUnit: MyDevice.Temperature.TemperatureUnit) {}
        override fun getBondedDeviceSettings(): Flow<BonnedDeviceSettings> = flowOf(BonnedDeviceSettings())
    }

    AppTheme(appSettingsRepository = mockAppSettingsRepository) {
        InquiryScreen(
            uiState = InquiryUiState(
                title = "Sample Inquiry",
                description = "This is a sample description for the inquiry."
            ),
            onNavigationIconClick = {},
            onTitleChange = {},
            onDescriptionChange = {}
        )
    }
}
