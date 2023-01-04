package com.example.todo.ui.screens.list

import android.annotation.SuppressLint
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.todo.R
import com.example.todo.ui.viewmodels.SharedViewModel
import com.example.todo.util.SearchAppBarState

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
    }
    /*collectAsState
    * UI 데이터 모든 작업 Flow 수집*/
    val allTasks by sharedViewModel.allTasks.collectAsState()
    val searchAppBarState: SearchAppBarState by sharedViewModel.searchAppBarState
    val searchTextState: String by sharedViewModel.searchTextState

    Scaffold(
        topBar = {
            ListAppBar(
                sharedViewModel = sharedViewModel,
                searchAppBarState = searchAppBarState,
                searchTextState = searchTextState
            )
        },
        content = {
            ListContent(
                tasks = allTasks,
                navigateToTaskScreen = navigateToTaskScreen
            )
        },
        floatingActionButton = {
            ListFab(onFabClicked = navigateToTaskScreen)
        }
    )
}

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