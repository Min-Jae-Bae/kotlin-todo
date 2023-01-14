package com.example.todo.ui.screens.task

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
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

    val context = LocalContext.current

    Scaffold(
        topBar = {
            /*TaskAppBar - 작업 선택, ListScreen 이동
            * No_ACTION일 때 - ListScreen
            * 다른 액션일 때 - 제목, 설명이 존재하면 ListScreen으로 이동하고 그렇지 않은 경우 Toast를 보여준다. */
            TaskAppBar(
                selectedTask = selectedTask,
                navigateToListScreen = { action ->
                    if (action == Action.NO_ACTION) {
                        navigateToListScreen(action)
                    } else {
                        if (sharedViewModel.validateFields()) {
                            navigateToListScreen(action)
                        } else {
                            displayToast(context = context)
                        }
                    }
                }
            )
        },
        content = {
            /*TaskContent
            * Default 값을 지정하고
            * 변경되는 값을 지정*/
            TaskContent(
                title = title,
                onTitleChange = {
                    sharedViewModel.updateTitle(it)
                },
                description = description,
                onDescriptionChange = {
                    sharedViewModel.description.value = it
                },
                priority = priority,
                onPrioritySelected = {
                    sharedViewModel.priority.value = it
                }
            )
        }
    )
}

fun displayToast(context: Context) {
    Toast.makeText(
        context,
        "Fields Empty",
        Toast.LENGTH_SHORT
    ).show()
}
