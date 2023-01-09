package com.example.todo.navigation.destinations

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
    navigateToListScreen: (Action) -> Unit,
) {
    /*composable - argument 통해 이동
    * TASK_SCREEN 길에서 해당 taskId 받아 이동 */
    composable(
        route = TASK_SCREEN,
        arguments = listOf(navArgument(TASK_ARGUMENT_KEY) {
            type = NavType.IntType
        })
    ) { navBackStackEntry ->
        val taskId = navBackStackEntry.arguments!!.getInt(TASK_ARGUMENT_KEY)

        TaskScreen(navigateToListScreen = navigateToListScreen)
    }
}