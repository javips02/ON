package com.javips02.on

import FunctionalitiesScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.javips02.on.persistence.AppDatabase

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

@Composable
fun NavigationWrapper(database: AppDatabase) {
    val navController = rememberNavController()
    Navigation(navController = navController, database = database) // Pass both
}

@Composable
fun Navigation(navController: NavHostController, database: AppDatabase) { // Receive navController and database
    NavHost(navController = navController, startDestination = Screen.Mainscreen.route) {
        composable(route = Screen.Mainscreen.route) { SurfaceContent(database = database, navController = navController) } // Pass both
        composable(
            route = Screen.Functionscreen.route +"/{name}",
            arguments = listOf(navArgument ("name"){ /* ... */ })
        ) { entry ->
            FunctionalitiesScreen(name = entry.arguments?.getString("name"))
        }
    }
}