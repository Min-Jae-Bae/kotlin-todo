package com.example.todo.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.example.todo.navigation.destinations.listComposable
import com.example.todo.navigation.destinations.taskComposable
import com.example.todo.ui.viewmodels.SharedViewModel
import com.example.todo.util.Constants.LIST_SCREEN
import com.example.todo.util.Constants.SPLASH_SCREEN
import com.google.accompanist.navigation.animation.AnimatedNavHost

/*SetupNavigation
* - Navigation 초기 설정 기능 (Host - NavController 연결)
* - NavHost 속 이동할 모든 Composable 존재 (list, task, splash)
* - shareViewModel - UI 관련 데이터를 받음*/
@ExperimentalAnimationApi
@Composable
fun SetupNavigation(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
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
    AnimatedNavHost(
        navController = navController,
        startDestination = LIST_SCREEN
    ) {
        listComposable(
            navigateToTaskScreen = screen.list,
            sharedViewModel = sharedViewModel
        )
        taskComposable(
            navigateToListScreen = screen.task,
            sharedViewModel = sharedViewModel
        )
    }
}