package com.example.todo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.todo.navigation.destinations.listComposable
import com.example.todo.navigation.destinations.taskComposable
import com.example.todo.util.Constants.LIST_SCREEN

/*SetupNavigation
* - Navigation 초기 설정 기능 (Host - NavController 연결)
* - NavHost 속 이동할 모든 Composable 존재 (list, task, splash)*/
@Composable
fun SetupNavigation(
    navController: NavHostController
) {
    /*remember
    * Screen에서 Compose의 이전 State(Action, taskId)기억하기 위해 사용*/
    val screen = remember(navController) {
        Screens(navController = navController)
    }

    /*NavHost
    * LIST_SCREEN 시작
    * LIST_SCREEN -> TASK_SCREEN
    * TASK_SCREEN -> LIST_SCREEN */
    NavHost(
        navController = navController,
        startDestination = LIST_SCREEN
    ) {
        listComposable(
            navigateToTaskScreen = screen.task
        )
        taskComposable(
            navigateToListScreen = screen.list
        )
    }
}