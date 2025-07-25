package com.composesamples.ui.navigation

sealed class AppRoutes(val route: String) {
    object SamplesScreen : AppRoutes("samples_screen")
    object HelloWorldScreen : AppRoutes("hello_world_screen")
    object InstalledAppsScreen : AppRoutes("installed_apps_screen")
}