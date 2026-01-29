package com.biostar.camtest.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.biostar.camtest.ui.screens.camera.CameraScreen
import com.biostar.camtest.ui.screens.gallery.GalleryScreen
import com.biostar.camtest.ui.screens.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Camera : Screen("camera")
    object Gallery : Screen("gallery")
    object Settings : Screen("settings")
}

@Composable
fun USBCamNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Camera.route
    ) {
        composable(Screen.Camera.route) {
            CameraScreen(
                onNavigateToGallery = {
                    navController.navigate(Screen.Gallery.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Gallery.route) {
            GalleryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
