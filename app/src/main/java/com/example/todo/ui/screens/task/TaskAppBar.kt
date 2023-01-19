package com.example.todo.ui.screens.task

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.todo.ui.theme.topAppBarBackgroundColor
import com.example.todo.ui.theme.topAppBarContentColor
import com.example.todo.util.Action
import com.example.todo.R
import com.example.todo.components.DisplayAlertDialog
import com.example.todo.data.models.Priority
import com.example.todo.data.models.ToDoTask

/*TaskAppBae - selectedTask(작업 선택 - ? = null 존재, 선택하지 않을 수 있음을 의미), navigateToListScreen(목록 추가 행동)
* 작업을 선택하지 않는다면 NewTaskAppBar를 보여주고
* 작업을 선택한다면 확장된 ExistingTaskAppBar를 보여주세요 */
@Composable
fun TaskAppBar(
    selectedTask: ToDoTask?,
    navigateToListScreen: (Action) -> Unit,
) {
    if (selectedTask == null) {
        NewTaskAppBar(navigateToListScreen = navigateToListScreen)
    } else {
        ExistingTaskAppBar(
            selectedTask = selectedTask,
            navigateToListScreen = navigateToListScreen
        )
    }
}

/*NewTaskAppBae
* TopAppBar(상단 기능)
* - navigationIcon - 앞 아이콘( 리스트 화면으로 이등 클릭)
* - actions - 행동 기능(추가 버튼)*/
@Composable
fun NewTaskAppBar(
    navigateToListScreen: (Action) -> Unit,
) {
    TopAppBar(
        navigationIcon = {
            BackAction(onBackClicked = navigateToListScreen)
        },
        title = {
            Text(
                text = stringResource(id = R.string.add_task),
                color = MaterialTheme.colors.topAppBarContentColor
            )
        },
        backgroundColor = MaterialTheme.colors.topAppBarBackgroundColor,
        actions = {
            AddAction(onAddClicked = navigateToListScreen)
        }
    )
}

/*BackAction
* 뒤로 행동
* IconButton - 버튼 틀 ( 백 버튼 클릭 행동 )
* Icon - 아이콘 부여*/
@Composable
fun BackAction(
    onBackClicked: (Action) -> Unit,
) {
    IconButton(onClick = { onBackClicked(Action.NO_ACTION) }) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = stringResource(id = R.string.back_arrow),
            tint = MaterialTheme.colors.topAppBarContentColor
        )
    }
}

/*AddAction
* 추가 행동*/
@Composable
fun AddAction(
    onAddClicked: (Action) -> Unit,
) {
    IconButton(onClick = { onAddClicked(Action.ADD) }) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = stringResource(id = R.string.add_task),
            tint = MaterialTheme.colors.topAppBarContentColor
        )
    }
}

/*ExistingTaskAppBar ( 작업 선택, 목록 스크린 이동 )
* 확장 작업 바
* TopAppBar(상단 기능)
* - navigationIcon - 앞 아이콘(리스트 화면으로 이등 클릭)
* - actions - 행동 기능(삭제, 업데이트 버튼)*/
@Composable
fun ExistingTaskAppBar(
    selectedTask: ToDoTask,
    navigateToListScreen: (Action) -> Unit,
) {
    TopAppBar(
        navigationIcon = {
            CloseAction(onCloseClicked = navigateToListScreen)
        },
        title = {
            Text(
                text = selectedTask.title,
                color = MaterialTheme.colors.topAppBarContentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        backgroundColor = MaterialTheme.colors.topAppBarBackgroundColor,
        actions = {
            ExistingTaskAppBarActions(
                selectedTask = selectedTask,
                navigateToListScreen = navigateToListScreen
            )
        }
    )
}

/*CloseAction
* 종료 행동
* IconButton - 버튼 틀 ( 백 버튼 클릭 행동 )
* Icon - 아이콘 부여*/
@Composable
fun CloseAction(
    onCloseClicked: (Action) -> Unit,
) {
    IconButton(onClick = { onCloseClicked(Action.NO_ACTION) }) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = stringResource(id = R.string.close_icon),
            tint = MaterialTheme.colors.topAppBarContentColor
        )
    }
}

/*ExistingTaskAppBarActions - (선택된 작업, List 화면 이동)
* 기존 작업 바 행동들 보여주는 UI
*
* openDialog 상태를 닫혔있는 상태(false)로 초기화하고 기억한다
* DisplayAlertDialog - openDialog(true), closeDialog(false), onYesClicked(삭제를 수행하고 List UI 이동)
* DeleteAction - Delete 아이콘 클릭시 Dialog 오픈
* UpdateAction - Update 아이콘 클릭시 List UI로 이동
* */
@Composable
fun ExistingTaskAppBarActions(
    selectedTask: ToDoTask,
    navigateToListScreen: (Action) -> Unit,
) {
    var openDialog by remember { mutableStateOf(false) }

    DisplayAlertDialog(
        title = stringResource(
            id = R.string.delete_task,
            selectedTask.title
        ),
        message = stringResource(
            id = R.string.delete_task_confirmation,
            selectedTask.title
        ),
        openDialog = openDialog,
        closeDialog = { openDialog = false },
        onYesClicked = { navigateToListScreen(Action.DELETE) }
    )
    DeleteAction(onDeleteClicked = {
        openDialog = true
    })
    UpdateAction(onUpdateClicked = navigateToListScreen)
}

/*DeleteAction
* 삭제 행동
* IconButton - 버튼 틀 ( 백 버튼 클릭 행동 )
* Icon - 아이콘 부여*/
@Composable
fun DeleteAction(
    onDeleteClicked: () -> Unit,
) {
    IconButton(onClick = { onDeleteClicked() }) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(id = R.string.delete_icon),
            tint = MaterialTheme.colors.topAppBarContentColor
        )
    }
}

/*UpdateAction
* 업데이트 행동
* IconButton - 버튼 틀 ( 백 버튼 클릭 행동 )
* Icon - 아이콘 부여*/
@Composable
fun UpdateAction(
    onUpdateClicked: (Action) -> Unit,
) {
    IconButton(onClick = { onUpdateClicked(Action.UPDATE) }) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = stringResource(id = R.string.update_icon),
            tint = MaterialTheme.colors.topAppBarContentColor
        )
    }
}

@Composable
@Preview
fun NewTaskAppBarPreview() {
    NewTaskAppBar(
        navigateToListScreen = {}
    )
}

@Composable
@Preview
fun ExistingTaskAppBarPreview() {
    ExistingTaskAppBar(
        selectedTask = ToDoTask(
            id = 0,
            title = "Kotlin",
            description = "Study",
            priority = Priority.MEDIUM
        ),
        navigateToListScreen = {}
    )
}


