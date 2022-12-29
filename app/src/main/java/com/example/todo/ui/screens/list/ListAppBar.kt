package com.example.todo.ui.screens.list

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.todo.ui.theme.topAppBarBackgroundColor
import com.example.todo.ui.theme.topAppBarContentColor
import com.example.todo.R
import com.example.todo.components.PriorityItem
import com.example.todo.data.models.Priority
import com.example.todo.ui.theme.LARGE_PADDING
import com.example.todo.ui.theme.Typography

/*ListAppBar
* 상단 작업 목록 바
* - 작업 검색
* - 작업 정렬
* - 작업 삭제*/
@Composable
fun ListAppBar() {
    DefaultListAppBar(
        onSearchClicked = {},
        onSortClicked = {},
        onDeleteClicked = {}
    )
}

/*DefaultListAppBar
* 상단 작업 검색 목록 기본 바
* - 검색, 정렬, 삭제 행동 기능
*
* TopAppBar - 제목, 행동들(사용자 - 클릭시: @Composable), 배경색 제공*/
@Composable
fun DefaultListAppBar(
    onSearchClicked: () -> Unit,
    onSortClicked: (Priority) -> Unit,
    onDeleteClicked: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = "Tasks",
                color = MaterialTheme.colors.topAppBarContentColor
            )
        },
        actions = {
            ListAppBarActions(
                onSearchClicked = onSearchClicked,
                onSortClicked = onSortClicked,
                onDeleteClicked = onDeleteClicked
            )
        },
        backgroundColor = MaterialTheme.colors.topAppBarBackgroundColor
    )
}

/*ListAppBarActions
* 사용자 행동 실행 기능 (검색, 정렬, 전체 삭제 아이콘 클릭 행동 실행 기능)*/
@Composable
fun ListAppBarActions(
    onSearchClicked: () -> Unit,
    onSortClicked: (Priority) -> Unit,
    onDeleteClicked: () -> Unit,
) {
    SearchAction(onSearchClicked = onSearchClicked)
    SortAction(onSortClicked = onSortClicked)
    DeleteAllAction(onDeleteClicked = onDeleteClicked)
}

/*SearchAction
* 검색 행동(검색 아이콘 클릭)
* 아이콘 버튼을 만들고(버튼 클릭 지정) 검색 아이콘(이미지, 설명, 색깔)을 넣는다*/
@Composable
fun SearchAction(
    onSearchClicked: () -> Unit,
) {
    IconButton(
        onClick = { onSearchClicked() }
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = stringResource(id = R.string.search_action),
            tint = MaterialTheme.colors.topAppBarContentColor
        )
    }
}

/*SortAction
* 정렬 행동(정렬 아이콘 클릭) - HIGH, LOW, NONE
* expanded - 버튼 클릭시 확장(true), 비확장(false) 이전 상태를 기억
*
* 아이콘 버튼 틀을 생성 (클릭시 - 확장) 후 정렬 아이콘(이미지, 설명, 색깔)을 넣는다
* DropdownMenu(기본 클릭시 확장 - true, 취소시 비확장 - false)
* DropdownMenuItem(속성 - 클릭시 비확장, 작업 우선순위 선택) = 우선순위 아이템(3개)*/
@Composable
fun SortAction(
    onSortClicked: (Priority) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }

    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_filter_list),
            contentDescription = stringResource(id = R.string.sort_action),
            tint = MaterialTheme.colors.topAppBarContentColor
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onSortClicked(Priority.HIGH)
                }
            ) {
                PriorityItem(priority = Priority.HIGH)
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onSortClicked(Priority.LOW)
                }
            ) {
                PriorityItem(priority = Priority.LOW)
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onSortClicked(Priority.NONE)
                }
            ) {
                PriorityItem(priority = Priority.NONE)
            }
        }
    }
}

/*DeleteAllAction
* 삭제 행동(삭제 아이콘 클릭)
*
* expanded - 이전 상태 기억( 확장 - true, 비확장 - false)
* 아이콘 버튼 틀을 생성(클릭시 - 확장 true) 후 아이콘(이미지, 설명, 색상) 넣기
* 하단 메뉴 생성(클릭 속성: 확장 - true, 확장 취소 - false) - 삭제 Text 클릭시(비확장, 삭제 클릭)*/
@Composable
fun DeleteAllAction(
    onDeleteClicked: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }

    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_vertical_menu),
            contentDescription = stringResource(id = R.string.delete_all_action),
            tint = MaterialTheme.colors.topAppBarContentColor
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onDeleteClicked()
                }
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = LARGE_PADDING),
                    text = stringResource(id = R.string.delete_all_action),
                    style = Typography.subtitle2
                )
            }
        }
    }
}

@Composable
@Preview
fun DefaultListAppBarPreview() {
    DefaultListAppBar(
        onSearchClicked = {},
        onSortClicked = {},
        onDeleteClicked = {}
    )
}