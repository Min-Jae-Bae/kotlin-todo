package com.example.todo.ui.screens.task

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.example.todo.data.models.Priority
import com.example.todo.data.models.ToDoTask
import com.example.todo.ui.viewmodels.SharedViewModel
import com.example.todo.util.Action

/*TaskScreen - 작업 화면
* Scaffold - 여러가지 기능을 통해 레이아웃 제작 제공
* topBar - TaskAppBar(선택한 작업과, 이동 화면 속성을 제공함)*/
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TaskScreen(
    selectedTask: ToDoTask?,
    sharedViewModel: SharedViewModel,
    navigateToListScreen: (Action) -> Unit,
) {
    val title: String by sharedViewModel.title
    val description: String by sharedViewModel.description
    val priority: Priority by sharedViewModel.priority

    Scaffold(
        topBar = {
            TaskAppBar(
                selectedTask = selectedTask,
                navigateToListScreen = navigateToListScreen
            )
        },
        content = {
            TaskContent(
                title = "",
                onTitleChange = {
                    sharedViewModel.title.value = it
                },
                description = "",
                onDescriptionChange = {
                    sharedViewModel.description.value = it
                },
                priority = Priority.LOW,
                onPrioritySelected = {
                    sharedViewModel.priority.value = it
                }
            )
        }
    )
}