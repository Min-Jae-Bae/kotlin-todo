package com.example.todo.ui.screens.list

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.todo.ui.theme.topAppBarBackgroundColor
import com.example.todo.ui.theme.topAppBarContentColor

/*ListAppBar
* 상단 작업 검색 목록 바*/
@Composable
fun ListAppBar() {
    DefaultListAppBar()
}

/*DefaultListAppBar
* 상단 작업 검색 목록 바
* - 기본 제목 - Tasks, 컬러 지정
* - 배경 컬러 - Theme 따른 색상 지정*/
@Composable
fun DefaultListAppBar() {
    TopAppBar(
        title = {
            Text(
                text = "Tasks",
                color = MaterialTheme.colors.topAppBarContentColor
            )
        },
        backgroundColor = MaterialTheme.colors.topAppBarBackgroundColor
    )
}

@Composable
@Preview
fun DefaultListAppBarPreview() {
    DefaultListAppBar()
}