package com.example.todo.navigation.destinations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.todo.ui.screens.list.ListScreen
import com.example.todo.ui.viewmodels.SharedViewModel
import com.example.todo.util.Constants.LIST_ARGUMENT_KEY
import com.example.todo.util.Constants.LIST_SCREEN

/*NavGraphBuilder
* listComposable 관련 Composable 분할
* listComposable - Task Screen 인자를 받음
* shareViewModel - UI 관련 데이터를 받음*/
fun NavGraphBuilder.listComposable(
    navigateToTaskScreen: (taskId: Int) -> Unit,
    sharedViewModel: SharedViewModel
) {
    /*composable - argument 통해 이동
    * LIST_SCREEN 길에서 해당 Action 받아 이동 */
    composable(
        route = LIST_SCREEN,
        arguments = listOf(navArgument(LIST_ARGUMENT_KEY) {
            type = NavType.StringType
        })
    ) {
        ListScreen(
            navigateToTaskScreen = navigateToTaskScreen,
            sharedViewModel = sharedViewModel
        )
    }
}