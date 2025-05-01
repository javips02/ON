package com.javips02.on

sealed class Screen (val route: String){
    object Mainscreen : Screen("MainActivity")
    object Functionscreen : Screen("FunctionalitiesActivity")

    fun withArgs(vararg args: String): String{
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
