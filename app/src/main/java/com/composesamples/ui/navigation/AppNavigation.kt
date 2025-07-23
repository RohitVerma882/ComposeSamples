package com.composesamples.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.composesamples.AppContainer
import com.composesamples.ui.screen.HelloWorldScreen
import com.composesamples.ui.screen.InstalledAppsScreen
import com.composesamples.ui.screen.SamplesScreen

@Composable
fun AppNavigation(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.SamplesScreen.route,
    ) {
        composable(AppRoutes.SamplesScreen.route) {
            SamplesScreen(
                navController = navController,
                sampleRepository = appContainer.sampleRepository
            )
        }

        composable(AppRoutes.HelloWorldScreen.route) {
            HelloWorldScreen()
        }

        composable(AppRoutes.InstalledAppsScreen.route) {
            InstalledAppsScreen(
                navController = navController,
                appRepository = appContainer.appRepository
            )
        }
    }
}
