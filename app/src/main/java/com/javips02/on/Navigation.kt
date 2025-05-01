package com.javips02.on

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Mainscreen.route) {
        composable(route = Screen.Mainscreen.route) { MainActivity() }
        composable(
            route = Screen.Functionscreen.route +"/{name}",
            arguments = listOf(
                navArgument ("name"){
                    type = NavType.StringType
                    defaultValue = "John Doe"
                    nullable = true
                }
            )
        ) { entry ->
            val receivedName = entry.arguments?.getString("name")
            FunctionalitiesScreen(name = receivedName) // Correctly calling the Composable
        }
    }
}