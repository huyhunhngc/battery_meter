package io.github.ifa.glancewidget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import io.github.ifa.glancewidget.di.RepositoryProvider
import io.github.ifa.glancewidget.features.main.mainScreenRoute
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var repositoryProvider: RepositoryProvider


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            repositoryProvider.Provide {
                BatteryApp(startDestination = mainScreenRoute)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.startBatteryMonitoring()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopBatteryMonitoring()
    }
}
