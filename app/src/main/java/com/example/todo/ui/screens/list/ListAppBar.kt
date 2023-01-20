package com.example.todo.ui.screens.list

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.example.todo.R
import com.example.todo.components.DisplayAlertDialog
import com.example.todo.components.PriorityItem
import com.example.todo.data.models.Priority
import com.example.todo.ui.theme.*
import com.example.todo.ui.viewmodels.SharedViewModel
import com.example.todo.util.Action
import com.example.todo.util.SearchAppBarState
import com.example.todo.util.TrailingIconState

/*ListAppBar (UI 데이터, 검색 바 상태, 검색 문자)
* 상단 작업 목록 바
* - 작업 검색
* - 작업 정렬
* - 작업 삭제 */
@Composable
fun ListAppBar(
    sharedViewModel: SharedViewModel,
    searchAppBarState: SearchAppBarState,
    searchTextState: String,
) {
    /*검색 바 상태
    *닫혀 있을 때
    * - 기본바: 검색 아이콘 - 클릭시 검색바 상태는 열림
    *열려 있을 때
    * - 검색바 : 현재 텍스트, 텍스트 변경시 - ViewModel에 텍스트 대입, 닫는 아이콘 - 클릭시 닫음 상태 변경 후 문자 삭제
    * */
    when (searchAppBarState) {
        SearchAppBarState.CLOSED -> {
            DefaultListAppBar(
                onSearchClicked = {
                    sharedViewModel.searchAppBarState.value =
                        SearchAppBarState.OPENED
                },
                onSortClicked = {},
                onDeleteAllConfirmed = {
                    // UI 관련 데이터 Action 값이 DELETE_ALL
                    sharedViewModel.action.value = Action.DELETE_ALL
                }
            )
        }
        else -> {
            SearchAppBar(
                text = searchTextState,
                onTextChange = { newText ->
                    sharedViewModel.searchTextState.value = newText
                },
                onCloseClicked = {
                    sharedViewModel.searchAppBarState.value =
                        SearchAppBarState.CLOSED
                    sharedViewModel.searchTextState.value = ""
                },
                onSearchClicked = {
                    // UI 관련 데이터 DB 검색 기능을 실행 한다
                    sharedViewModel.searchDatabase(searchQuery = it)
                }
            )
        }
    }
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
    onDeleteAllConfirmed: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.list_screen_title),
                color = MaterialTheme.colors.topAppBarContentColor
            )
        },
        actions = {
            ListAppBarActions(
                onSearchClicked = onSearchClicked,
                onSortClicked = onSortClicked,
                onDeleteAllConfirmed = onDeleteAllConfirmed
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
    onDeleteAllConfirmed: () -> Unit,
) {
    var openDialog by remember { mutableStateOf(false) }

    DisplayAlertDialog(
        title = stringResource(id = R.string.delete_all_tasks),
        message = stringResource(id = R.string.delete_all_tasks_confirmation),
        openDialog = openDialog,
        closeDialog = { openDialog = false },
        onYesClicked = { onDeleteAllConfirmed() }
    )
    SearchAction(onSearchClicked = onSearchClicked)
    SortAction(onSortClicked = onSortClicked)
    DeleteAllAction(onDeleteAllConfirmed = { openDialog = true })
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
    onDeleteAllConfirmed: () -> Unit,
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
                    onDeleteAllConfirmed()
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

/*SearchAppBar (받은 문자, 바꾼 문자, 닫았을 때, 문자 검색 아이콘 클릭시)
* */
@Composable
fun SearchAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    // 아이콘 초기 상태 기억 - 안에 내용 삭제 준비
    var trailingIconState by remember {
        mutableStateOf(TrailingIconState.READY_TO_DELETE)
    }
    // AppBar 배경(Surface)를 만듬, elevation - 그림자 있는 배경 생성
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(TOP_APP_BAR_HEIGHT),
        elevation = AppBarDefaults.TopAppBarElevation,
        color = MaterialTheme.colors.topAppBarBackgroundColor
    ) {
        /*TextField
        * 문자를 넣을 수 있는 Field 생성
        * value - 넣는 값, onValueChange - 변경 값, placeholder - 짧은 도움말 생성
        * alpha - 내용 중요성 강조 할 수 있음
        * singleLine - 한줄만 사용 할건지 선택
        * leadingIcon - TextField 앞 아이콘 , trailingIcon - TextField 뒤 아이콘
        * */
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = text,
            onValueChange = {
                onTextChange(it)
            },
            placeholder = {
                Text(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium),
                    text = stringResource(id = R.string.search_placeholder),
                    color = Color.White
                )
            },
            textStyle = TextStyle(
                color = MaterialTheme.colors.topAppBarContentColor,
                fontSize = MaterialTheme.typography.subtitle1.fontSize
            ),
            singleLine = true,
            leadingIcon = {
                IconButton(
                    modifier = Modifier
                        .alpha(ContentAlpha.disabled),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(id = R.string.search_icon),
                        tint = MaterialTheme.colors.topAppBarContentColor
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        /*삭제 준비일 때 - 텍스트 삭제 및 닫을 준비
                        * 닫을 준비일 때 - 텍스트 존재시 다시 삭제, 텍스트 없을시 닫고 원래 삭제 준비 상태로*/
                        when (trailingIconState) {
                            TrailingIconState.READY_TO_DELETE -> {
                                onTextChange("")
                                trailingIconState = TrailingIconState.READY_TO_CLOSE
                            }
                            TrailingIconState.READY_TO_CLOSE -> {
                                if (text.isNotEmpty()) {
                                    onTextChange("")
                                } else {
                                    onCloseClicked()
                                    trailingIconState = TrailingIconState.READY_TO_DELETE
                                }
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(id = R.string.close_icon),
                        tint = MaterialTheme.colors.topAppBarContentColor
                    )
                }
            },
            // 키보드 검색 아이콘 추가
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            // 키보드 문자 검색 행동 추가
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                }
            ),
            /*TextField Color
            * 커서 색상, 테두리 강조, 사용하지 않을 때, 미강조, 미강조 라벨, 배경 색상*/
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = MaterialTheme.colors.topAppBarContentColor,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedLabelColor = Color.Transparent,
                backgroundColor = Color.Transparent
            )
        )
    }
}


@Composable
@Preview
fun DefaultListAppBarPreview() {
    DefaultListAppBar(
        onSearchClicked = {},
        onSortClicked = {},
        onDeleteAllConfirmed = {}
    )
}

@Composable
@Preview
private fun SearchAppBarPreview() {
    SearchAppBar(
        text = "Search",
        onTextChange = {},
        onCloseClicked = {},
        onSearchClicked = {}
    )
}