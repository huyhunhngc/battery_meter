package io.github.ifa.glancewidget

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.ifa.glancewidget.glance.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun ConfigApp(
    modifier: Modifier = Modifier,
    startDestination: String
) {
//    val systemUiController = rememberSystemUiController()
//    val isDarkTheme = isSystemInDarkTheme()
//    val navigationBarColor = colorScheme.primaryFixed
//    SideEffect {
//        systemUiController.setNavigationBarColor(
//            color = navigationBarColor, darkIcons = true
//        )
//    }
//
    val navController: NavHostController = rememberNavController()
    AppTheme {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = colorScheme.background,
        ) {
            AppNavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier,
                onBackClick = {
                    navController.popBackStack()
                },
                onBackClickBlockNavController = {
                    popBackStack()
                }
            )
        }
    }
}