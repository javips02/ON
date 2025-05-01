package com.javips02.on

sealed class Screen (val route: String){
    object Mainscreen : Screen("MainActivity") // This can remain if MainActivity's composable content is handled there
    object Functionscreen : Screen("FunctionalitiesScreen") // Updated route to the Composable name

    fun withArgs(vararg args: String): String{
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}