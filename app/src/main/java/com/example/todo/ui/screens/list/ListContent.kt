package com.example.todo.ui.screens.list

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.todo.data.models.Priority
import com.example.todo.data.models.ToDoTask
import com.example.todo.ui.theme.*
import com.example.todo.util.RequestState
import com.example.todo.util.SearchAppBarState

/*ListContent - (모든 작업 상태 목록, 검색 작업 상태 목록, 검색 바 상태, 작업 스크린 이동)
* 검색바 상태가 TRIGGERED(바뀌고)
* 만약 검색 작업들이 요청 상태 Success와 자료형이 일치한다면 List 내용(검색 작업 데이터 조작) UI를 보여주고
* 만약 검색 작업들이 요청 상태 Success와 자료형이 불일치하고 모든 작업이 요청상태 Success와 일치하다면
* List 내용(모든 작업 데이터 조작) UI를 보여주세요
*
* is는 타입이 일치하는지 보여주고, as는 타입을 바꿔주는 역할을 한다.*/
@Composable
fun ListContent(
    allTasks: RequestState<List<ToDoTask>>,
    searchedTasks: RequestState<List<ToDoTask>>,
    lowPriorityTasks: List<ToDoTask>,
    highPriorityTasks: List<ToDoTask>,
    sortState: RequestState<Priority>,
    searchAppBarState: SearchAppBarState,
    navigateToTaskScreen: (taskId: Int) -> Unit,
) {
    if (sortState is RequestState.Success) {
        when {
            searchAppBarState == SearchAppBarState.TRIGGERED -> {
                if (searchedTasks is RequestState.Success) {
                    HandleListContent(
                        tasks = searchedTasks.data,
                        navigateToTaskScreen = navigateToTaskScreen
                    )
                }
            }
            sortState.data == Priority.NONE -> {
                if (allTasks is RequestState.Success) {
                    HandleListContent(
                        tasks = allTasks.data,
                        navigateToTaskScreen = navigateToTaskScreen
                    )
                }
            }
            sortState.data == Priority.LOW -> {
                HandleListContent(
                    tasks = lowPriorityTasks,
                    navigateToTaskScreen = navigateToTaskScreen
                )
            }

            sortState.data == Priority.HIGH -> {
                HandleListContent(
                    tasks = highPriorityTasks,
                    navigateToTaskScreen = navigateToTaskScreen
                )
            }
        }
    }
}


/*HandleListContent - (작업, 작업 스크린 이동)
* List 내용 조작 UI
* 만약 tasks가 없을 때는 비어 있는 UI를 보여주고
* tasks가 존재할 때는 모든 작업과 작업 스크린 이동 가능한 작업 목록 UI를 보여준다.*/
@Composable
fun HandleListContent(
    tasks: List<ToDoTask>,
    navigateToTaskScreen: (taskId: Int) -> Unit,
) {
    if (tasks.isEmpty()) {
        EmptyContent()
    } else {
        DisplayTasks(
            tasks = tasks,
            navigateToTaskScreen = navigateToTaskScreen
        )
    }
}

/*DisplayTasks
* 모든 작업 목록, 작업 번호 순서*/
@Composable
fun DisplayTasks(
    tasks: List<ToDoTask>,
    navigateToTaskScreen: (taskId: Int) -> Unit,
) {
    /*LazyColumn - 세로로 아이템 표시하는 RecyclerView
    * LazyRow - 가로로 아이템 표시하는 RecyclerView
    * items - item 리스트를 추가할 수 있는 기능*/
    LazyColumn {
        /*items
        * 작업들(할일), key 를 작업 아이디로 지정
        * TaskItem - 작업 아이템 리스트 1개 ( 모든 작업, 클릭시 작업 이동 )*/
        items(
            items = tasks,
            key = { task ->
                task.id
            }
        ) { task ->
            TaskItem(
                toDoTask = task,
                navigateToTaskScreen = navigateToTaskScreen
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskItem(
    toDoTask: ToDoTask,
    navigateToTaskScreen: (taskId: Int) -> Unit,
) {
    /*Surface - 배경
    * elevation - 테마 변경시 명암 자동 변경
    * onClick - 클릭시 작업 아이디로 이동 */
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = MaterialTheme.colors.taskItemBackgroundColor,
        shape = RectangleShape,
        elevation = TASK_ITEM_ELEVATION,
        onClick = {
            navigateToTaskScreen(toDoTask.id)
        }
    ) {
        /*Column - 세로 방향 (Padding - 요소 간격)
        * Text, Box (위)
        * Text (아래)
        * */
        Column(
            modifier = Modifier
                .padding(all = LARGE_PADDING)
                .fillMaxWidth()
        ) {
            /*Row - 가로 방향
            * Text(8f - 오른쪽에서 8칸) <-> Box (1f - 오른쪽에서 1칸)*/
            Row {
                /*Text
                * maxLines - 최대 라인 지정
                * overflow - 글자 범위 넘어갈시 어떻게 할지 지정 (Ellipis - ...으로 지정)*/
                Text(
                    modifier = Modifier.weight(8f),
                    text = toDoTask.title,
                    color = MaterialTheme.colors.taskItemTextColor,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                /*Box - 작은 내용, TopEnd - 상단 오른쪽
                * 내용 - Canvas - 정사각형 도화지 생성, drawCircle - 속성에 맞는 컬러 생성*/
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(PRIORITY_INDICATOR_SIZE)
                    ) {
                        drawCircle(
                            color = toDoTask.priority.color
                        )
                    }
                }
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = toDoTask.description,
                color = MaterialTheme.colors.taskItemTextColor,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
@Preview
fun TaskItemPreview() {
    TaskItem(
        toDoTask = ToDoTask(
            id = 0,
            title = "Kotlin",
            description = "Hello World!",
            priority = Priority.MEDIUM
        ),
        navigateToTaskScreen = {}
    )
}