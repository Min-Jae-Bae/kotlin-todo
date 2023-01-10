package com.example.todo.ui.screens.task

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import com.example.todo.data.models.ToDoTask
import com.example.todo.util.Action

/*TaskScreen - 작업 화면
* Scaffold - 여러가지 기능을 통해 레이아웃 제작 제공
* topBar - TaskAppBar(선택한 작업과, 이동 화면 속성을 제공함)*/
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TaskScreen(
    selectedTask: ToDoTask?,
    navigateToListScreen: (Action) -> Unit,
) {
    Scaffold(
        topBar = {
            TaskAppBar(
                selectedTask = selectedTask,
                navigateToListScreen = navigateToListScreen
            )
        },
        content = {

        }
    )
}