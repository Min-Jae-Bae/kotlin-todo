package com.example.todo.navigation.destinations

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.todo.ui.screens.task.TaskScreen
import com.example.todo.ui.viewmodels.SharedViewModel
import com.example.todo.util.Action
import com.example.todo.util.Constants.TASK_ARGUMENT_KEY
import com.example.todo.util.Constants.TASK_SCREEN

/*NavGraphBuilder
* taskComposable 관련 Composable 분할
* navigateToListScreen - List Screen 인자를 받음*/
fun NavGraphBuilder.taskComposable(
    sharedViewModel: SharedViewModel,
    navigateToListScreen: (Action) -> Unit,
) {
    /*composable - argument 통해 이동
    * TASK_SCREEN 길에서 해당 taskId 받아 이동
    *
    * NavBackStackEntry
    * - navBackStackEntry에서 arguments목록을 가져온 다음 필요한 인수를 검색하고 가져와서 컴포저블 화면으로 전달.*/
    composable(
        route = TASK_SCREEN,
        arguments = listOf(navArgument(TASK_ARGUMENT_KEY) {
            type = NavType.IntType
        })
    ) { navBackStackEntry ->
        /*현재 경로에 백 스택에 있는 작업 아이디를 검색하고 가져옴*/
        val taskId = navBackStackEntry.arguments!!.getInt(TASK_ARGUMENT_KEY)
        sharedViewModel.getSelectedTask(taskId = taskId)
        val selectedTask by sharedViewModel.selectedTask.collectAsState()

        LaunchedEffect(key1 = taskId) {
            sharedViewModel.updateTaskFields(selectedTask = selectedTask)
        }
        TaskScreen(
            selectedTask = selectedTask,
            sharedViewModel = sharedViewModel,
            navigateToListScreen = navigateToListScreen
        )
    }
}