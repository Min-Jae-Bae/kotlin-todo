package com.example.todo.navigation

import androidx.navigation.NavController
import com.example.todo.util.Action
import com.example.todo.util.Constants.LIST_SCREEN
import com.example.todo.util.Constants.SPLASH_SCREEN

/*Screens
* NavController - Navigation 하기 위해 화면 상태와 위치를 추적하는 API */
class Screens(navController: NavController) {
    /*task
    * 작업 ID에 따른 대상 이동*/
    val list: (Int) -> Unit = { taskId ->
        navController.navigate(route = "task/${taskId}")
    }

    /*list
    * 사용자 행동에 따른 대상 이동
    * 모든 이동은 LIST_SCREEN 속에만 포함됨*/
    val task: (Action) -> Unit = { action ->
        navController.navigate(route = "list/${action.name}") {
            popUpTo(LIST_SCREEN) { inclusive = true }
        }
    }
}