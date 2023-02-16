package com.example.todo.navigation

import androidx.navigation.NavController
import com.example.todo.util.Action
import com.example.todo.util.Constants.LIST_SCREEN

class Screens(navController: NavController) {
    val list: (Int) -> Unit = { taskId ->
        navController.navigate(route = "task/${taskId}")
    }

    val task: (Action) -> Unit = { action ->
        navController.navigate(route = "list/${action.name}") {
            popUpTo(LIST_SCREEN) { inclusive = true }
        }
    }
}