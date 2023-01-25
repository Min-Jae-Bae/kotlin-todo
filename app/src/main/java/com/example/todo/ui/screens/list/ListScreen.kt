package com.example.todo.ui.screens.list

import android.annotation.SuppressLint
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.todo.R
import com.example.todo.ui.viewmodels.SharedViewModel
import com.example.todo.util.Action
import com.example.todo.util.SearchAppBarState
import kotlinx.coroutines.launch

/*ListFab
* - 할일 목록 추가 버튼 (이미지, 내용, 색깔등 추가 가능)
* - 인덱스가 0 부터 시작하니 클릭 하기 전에는 -1로 지정한것임
*  */
@Composable
fun ListFab(
    onFabClicked: (taskId: Int) -> Unit,
) {
    FloatingActionButton(
        onClick = {
            onFabClicked(-1)
        }
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(id = R.string.add_button),
            tint = Color.White
        )
    }
}

/*ListScreen
* - 작업 화면 이동 가능하게 해줌
* - 작업 스크린 이동
*
* Scaffold - 레이아웃 동작이 보장되는 프로그램 화면 구성 요소 결합을 제공하는 기능
* topBar - 상단 바, content - 레이아웃 내용
* floatingActionButton - 작업 추가 버튼
* content - 모든 작업, 작업 클릭시 이동*/
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ListScreen(
    navigateToTaskScreen: (taskId: Int) -> Unit,
    sharedViewModel: SharedViewModel,
) {
    /*LaunchedEffect
    * 특정 키 값을 바탕으로 리컴포지션 해주는 기능 ( 즉 새로고침 같은 기능이라 할 수 있음 )*/
    LaunchedEffect(key1 = true) {
        sharedViewModel.getAllTasks()
        sharedViewModel.readSortState()
    }
    /*shareViewModel action을 불러오기
    * by를 쓰는 이유 ? - 상속하지 않고 기존 기능을 그대로 사용하면서 새로운 기능을 추가할 때
    * */
    val action by sharedViewModel.action

    /*collectAsState
    * UI 데이터 모든 작업 Flow 수집
    * sortState - 정렬 상태 UI 데이터 작업 Flow 수집
    * lowPriorityTasks - 낮은 우선순위 작업 UI 데이터 작업 Flow 수집
    * highPriorityTasks - 높은 우선순위 작업 UI 데이터 작업 Flow 수집*/
    val allTasks by sharedViewModel.allTasks.collectAsState()
    val searchedTasks by sharedViewModel.searchedTasks.collectAsState()
    val sortState by sharedViewModel.sortState.collectAsState()
    val lowPriorityTasks by sharedViewModel.lowPriorityTasks.collectAsState()
    val highPriorityTasks by sharedViewModel.highPriorityTasks.collectAsState()

    val searchAppBarState: SearchAppBarState by sharedViewModel.searchAppBarState
    val searchTextState: String by sharedViewModel.searchTextState

    /*handleDatabaseAction
    * Action에 해당하는 역할을 수행한다..*/
    sharedViewModel.handleDatabaseAction(action = action)

    /*Snackbar
    * Compose에서 Snackbar의 동작대로 이용하기 위해서는 Scaffold State로 감싸야한다.
    * 감싸지 않으면 보통의 Composable과 똑같이 동작*/
    val scaffoldState = rememberScaffoldState()

    DisplaySnackBar(
        scaffoldState = scaffoldState,
        handleDatabaseActions = { sharedViewModel.handleDatabaseAction(action = action) },
        onUndoClicked = {
            sharedViewModel.action.value = it
        },
        taskTitle = sharedViewModel.title.value,
        action = action
    )

    Scaffold(
        //scaffoldState 넘긴다.
        scaffoldState = scaffoldState,
        topBar = {
            ListAppBar(
                sharedViewModel = sharedViewModel,
                searchAppBarState = searchAppBarState,
                searchTextState = searchTextState
            )
        },
        content = {
            ListContent(
                allTasks = allTasks,
                searchedTasks = searchedTasks,
                lowPriorityTasks = lowPriorityTasks,
                highPriorityTasks = highPriorityTasks,
                sortState = sortState,
                searchAppBarState = searchAppBarState,
                navigateToTaskScreen = navigateToTaskScreen
            )
        },
        floatingActionButton = {
            ListFab(onFabClicked = navigateToTaskScreen)
        }
    )
}

/*DisplaySnackBar - (scaffoldState, action조작, 취소 클릭, 작업 제목, action)
* SnackBar 보여줌
* */
@Composable
fun DisplaySnackBar(
    scaffoldState: ScaffoldState,
    handleDatabaseActions: () -> Unit,
    onUndoClicked: (Action) -> Unit,
    taskTitle: String,
    action: Action,
) {
    handleDatabaseActions()

    /*rememberCoroutineScope
    * Composable 파괴될 때 파괴되는 코루틴을 생성 해야될 때 사용*/
    val scope = rememberCoroutineScope()

    /*side-effect
    * Composition이 완료될 때 side-effect를 처리하는 composable function 지원
    * 즉, composable function을 effect라는 단어로 정의하고 effect란, UI를 방출하지
    * 않는 Composable function 이다.
    * Compose는 비동기로 처리되기 때문에 callback를 사용하지 않고 coroutines로 감싸서 처리한다.*/

    /*LaunchedEffect - action에 따라 Compose 생성 시 launch 되고, Compose가 화면에서 사라지면 cancel
    *
    * NO_ACTION이 아니면 scaffold는 snackbar상태 저장하고 보여준다 (메시지, 라벨)
    *
    * 삭제 취소작업(action, 스넥바 결과, 취소 클릭)*/
    LaunchedEffect(key1 = action) {
        if (action != Action.NO_ACTION) {
            scope.launch {
                val snackBarResult = scaffoldState.snackbarHostState.showSnackbar(
                    message = setMessage(action = action, taskTitle = taskTitle),
                    actionLabel = "Ok"
                )
                undoDeletedTask(
                    action = action,
                    snackBarResult = snackBarResult,
                    onUndoClicked = onUndoClicked
                )
            }
        }
    }
}

/*setMessage - (행동, 작업 제목) -> 문자열 반환
* DELETE_ALL 클릭시 "All Tasks Removed" 반환
* 그 이외 클릭시 행동 이름과 작업 목록을 반환*/
private fun setMessage(action: Action, taskTitle: String): String {
    return when (action) {
        Action.DELETE_ALL -> "All Tasks Removed"
        else -> "${action.name}: $taskTitle"
    }
}

/*setActionLabel - (행동) -> 문자열 반환
* action 이름이 DELETE 일때 "UNDO" 반환
* 그 이외는 "OK" 반환*/
private fun setActionLabel(action: Action): String {
    return if (action.name == "DELETE") {
        "UNDO"
    } else {
        "OK"
    }
}

/*undoDeletedTask - (action, snackBar 결과, 취소 클릭)
* 삭제 취소 작업
*
* snackBarResult 수행 및 DELETE 가 되었을 때 취소를 클릭
* */
private fun undoDeletedTask(
    action: Action,
    snackBarResult: SnackbarResult,
    onUndoClicked: (Action) -> Unit,
) {
    if (snackBarResult == SnackbarResult.ActionPerformed
        && action == Action.DELETE
    ) {
        onUndoClicked(Action.UNDO)
    }

}