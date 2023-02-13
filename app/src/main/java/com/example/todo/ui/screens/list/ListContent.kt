package com.example.todo.ui.screens.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.todo.R
import com.example.todo.data.models.Priority
import com.example.todo.data.models.ToDoTask
import com.example.todo.ui.theme.*
import com.example.todo.util.Action
import com.example.todo.util.RequestState
import com.example.todo.util.SearchAppBarState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*ListContent - (모든 작업 상태 목록, 검색 작업 상태 목록, 검색 바 상태, 옆으로 해서 작업을 삭제, 작업 스크린 이동)
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
    onSwipeToDelete: (Action, ToDoTask) -> Unit,
    navigateToTaskScreen: (taskId: Int) -> Unit,
) {
    if (sortState is RequestState.Success) {
        when {
            searchAppBarState == SearchAppBarState.TRIGGERED -> {
                if (searchedTasks is RequestState.Success) {
                    HandleListContent(
                        tasks = searchedTasks.data,
                        onSwipeToDelete = onSwipeToDelete,
                        navigateToTaskScreen = navigateToTaskScreen
                    )
                }
            }
            /*정렬 상태 데이터가 없을 때 그리고 모든 작업 상태가 성공 요청 타입일 때
            * List 내용을 바꾼다 (모든 작업 값, 작업 스크린 이동 지원)*/
            sortState.data == Priority.NONE -> {
                if (allTasks is RequestState.Success) {
                    HandleListContent(
                        tasks = allTasks.data,
                        onSwipeToDelete = onSwipeToDelete,
                        navigateToTaskScreen = navigateToTaskScreen
                    )
                }
            }
            /*정렬 상태 데이터가 LOW일 때
            * List 내용을 바꾼다 (낮은 우선순위 작업, 작업 스크린 이동 지원)*/
            sortState.data == Priority.LOW -> {
                HandleListContent(
                    tasks = lowPriorityTasks,
                    onSwipeToDelete = onSwipeToDelete,
                    navigateToTaskScreen = navigateToTaskScreen
                )
            }
            /*정렬 상태 데이터가 HIGH일 때
            * List 내용을 바꾼다 (높은 우선순위 작업, 작업 스크린 이동 지원)*/
            sortState.data == Priority.HIGH -> {
                HandleListContent(
                    tasks = highPriorityTasks,
                    onSwipeToDelete = onSwipeToDelete,
                    navigateToTaskScreen = navigateToTaskScreen
                )
            }
        }
    }
}


/*HandleListContent - (작업, 옆으로 해서 작업을 삭제,작업 스크린 이동)
* List 내용 조작 UI
* 만약 tasks가 없을 때는 비어 있는 UI를 보여주고
* tasks가 존재할 때는 모든 작업과 작업 스크린 이동 가능한 작업 목록 UI를 보여준다.*/
@Composable
fun HandleListContent(
    tasks: List<ToDoTask>,
    onSwipeToDelete: (Action, ToDoTask) -> Unit,
    navigateToTaskScreen: (taskId: Int) -> Unit,
) {
    if (tasks.isEmpty()) {
        EmptyContent()
    } else {
        DisplayTasks(
            tasks = tasks,
            onSwipeToDelete = onSwipeToDelete,
            navigateToTaskScreen = navigateToTaskScreen
        )
    }
}

/*DisplayTasks
* 모든 작업 목록, 옆으로 해서 작업을 삭제, 작업 번호 순서*/
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DisplayTasks(
    tasks: List<ToDoTask>,
    onSwipeToDelete: (Action, ToDoTask) -> Unit,
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
            /*
            * rememberDismissState - 취소한 상태를 기억
            * dismissDirection - 밀은 상태의 방향
            * isDismissed - 밀어진(밀은 상태의 끝에서 시작점으로 밀음)
            * 만약 밀어진 상태와 밀어진 방향이 끝에서 시작점이라면
            *
            * rememberCoroutineScope - 사용자 이벤트가 발생할 때 애니메이션 취소 하기 위해 사용
            * SideEffect - Compose 상태를 Compose에서 관리하지 않는 객체와 공유하기 위해 리컴포지션 성공 시마다 호출
            * lauch - 실행 (300초, 옆으로 삭제시, 작업)*/
            val dismissState = rememberDismissState()
            val dismissDirection = dismissState.dismissDirection
            val isDismissed = dismissState.isDismissed(DismissDirection.EndToStart)
            if (isDismissed && dismissDirection == DismissDirection.EndToStart) {
                val scope = rememberCoroutineScope()
                SideEffect {
                    scope.launch {
                        delay(300)
                        onSwipeToDelete(Action.DELETE, task)
                    }
                }
            }

            /*animateFloatAsState - 단일 값을 에니메이션 처리하는 간단한 API
            * 만약 미는 상태 타겟 값이 기본값이라면 - 0f, 그 외에는 -45f */
            val degrees by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0f else -45f
            )

            /* LaunchedEffect - 키로 재구성되면 기존 코루틴을 취소하고 새 코루틴에서 새 정지 함수로 싱행
            * 아이템 상태를 나타내는 기억 나타나있지 않은 상태로 초기화
            * 컴포지션이 true로 리컴포지션이 되면 아이템 상태를 true로 리컴포지션*/
            var itemAppeared by remember { mutableStateOf(false) }
            LaunchedEffect(key1 = true) {
                itemAppeared = true
            }

            /*AnimatedVisibility - 나타남과 사라짐을 애니메이션으로 처리
            * visible - true일 때 enter 애니메이션, false일 때 exit 애니메이션
            * enter - expandVertically -> 수직 하단으로 나타남
            * exit - shrinkVertically -> 위 상단으로 사라지는*/
            AnimatedVisibility(
                visible = itemAppeared && !isDismissed,
                enter = expandVertically(
                    animationSpec = tween(
                        durationMillis = 300
                    )
                ),
                exit = shrinkVertically(
                    animationSpec = tween(
                        durationMillis = 300
                    )
                )
            ) {
                /*SwipeToDismiss - 옆으로 미는 기능
                * state - 밀은 상태
                * directions - 끝에서 앞 부분 방향
                * dismissThresholds - 어느 한 지점에서 임계값을 지정
                * background - 배경의 색, 아이콘 배정
                * dismissContent - 밀은 곳의 내용 (할일, 작업 스크린)*/
                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart),
                    dismissThresholds = { FractionalThreshold(fraction = 0.7f) },
                    background = { RedBackground(degrees = degrees) },
                    dismissContent = {
                        TaskItem(
                            toDoTask = task,
                            navigateToTaskScreen = navigateToTaskScreen
                        )
                    }
                )
            }
        }
    }
}

/*RedBackground - 위치
* 빨간색 삭제 배경
* 박스를 만들건데 박스는 최대 사이즈, 배경 색상, 수직 높이를 지정한 속성을 가졌어
* 그 박스 안에는 아이콘이 있는데 아이콘은 위도에 따른 아이콘이 돌아가
* 아이콘 이미지는 삭제 아이콘으로 채웠고, 아이콘 문자열 내용 설명은 삭제 아이콘이라는 이름을 지정했어
* 마지막으로 아이콘 색깔은 흰색으로 할게 !*/
@Composable
fun RedBackground(degrees: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HighPriorityColor)
            .padding(horizontal = LARGEST_PADDING)
    ) {
        Icon(
            modifier = Modifier.rotate(degrees = degrees),
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(id = R.string.delete_icon),
            tint = Color.White
        )
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