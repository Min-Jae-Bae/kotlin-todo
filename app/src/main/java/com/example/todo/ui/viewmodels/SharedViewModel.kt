package com.example.todo.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.models.Priority
import com.example.todo.data.models.ToDoTask
import com.example.todo.data.repositories.DataStoreRepository
import com.example.todo.data.repositories.ToDoRepository
import com.example.todo.util.Action
import com.example.todo.util.Constants.MAX_TITLE_LENGTH
import com.example.todo.util.RequestState
import com.example.todo.util.SearchAppBarState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/*HiltViewModel
* Hilt에게 ShareViewModel이 ViewModel임을 알려줌
*
* ShareViewModel
* - 생성하기 위해 ToDORepository 필요함을 알려주고 사용함
* - 내부 저장된 데이터의 값만 변경(Mutable)하기 위해 _allTasks 변수 생성
* - 받은 값(_allTasks)을 변경할 수 없게 allTasks 변수 생성
*
* viewModelScope.launch 사용하여 ViewModel이 OnClear시 Coroutine도 모두 취소하는 역할
* repository 발생 데이터를 순차적으로(collect) 받아 suspend fun을 수행하고 데이터에 접근한다.
* */
@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: ToDoRepository,
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    //action 상태 NO_ACTION 초기화
    val action: MutableState<Action> = mutableStateOf(Action.NO_ACTION)

    val id: MutableState<Int> = mutableStateOf(0)
    val title: MutableState<String> = mutableStateOf("")
    val description: MutableState<String> = mutableStateOf("")
    val priority: MutableState<Priority> = mutableStateOf(Priority.LOW)

    val searchAppBarState: MutableState<SearchAppBarState> =
        mutableStateOf(SearchAppBarState.CLOSED)
    val searchTextState: MutableState<String> = mutableStateOf("")

    /*_searchedTasks 와 searchedTasks
    * _searchedTasks는 변경 가능한 상태 흐름 요청 상태의 작업 목록을 Idle(쉬고 있는 상태)로 초기화한다.
    * searchedTasks는 shareViewModel 내부에서 사용하기 위해 변경 불가능한
    * 변수로 _searchedTasks를 지정해서 상태를 사용한다.*/
    private val _searchedTasks =
        MutableStateFlow<RequestState<List<ToDoTask>>>(RequestState.Idle)
    val searchedTasks: StateFlow<RequestState<List<ToDoTask>>> = _searchedTasks

    /*searchDatabase - (검색할 내용)
    * DB 검색 기능
    * 변경 가능한 검색 작업 상태를 (진행중인)
    *
    * 발생하는 오류를 잡을거야 viewModelScope을 생성하여 viewModel과 코루틴이 함께 종류할 수 있게 실행할거야
    * repository DB 검색 기능은 검색 쿼리를 받아 (변수 검색을 하기 위해서는 "%(이 사이에 변수를 지정해)%")
    * 그리고 검색 DB 관련 검색 작업 들을 모을거야 그 작업들은 작업 검색 값에 요청 상태 성공(검색 작업 이름)을 넣을거야
    * 예외가 발생한다면 작업 검색 값에 요청 상태(오류)를 넣을거야
    * 마지막으로 검색 바의 상태 값을 -> 전환으로 바꿨어*/
    fun searchDatabase(searchQuery: String) {
        _searchedTasks.value = RequestState.Loading
        try {
            viewModelScope.launch {
                repository.searchDatabase(searchQuery = "%$searchQuery%")
                    .collect { searchedTasks ->
                        _searchedTasks.value = RequestState.Success(searchedTasks)
                    }
            }
        } catch (e: Exception) {
            _searchedTasks.value = RequestState.Error(e)
        }
        searchAppBarState.value = SearchAppBarState.TRIGGERED
    }

    val lowPriorityTasks: StateFlow<List<ToDoTask>> =
        repository.sortByLowPriority.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            emptyList()
        )

    val highPriorityTasks: StateFlow<List<ToDoTask>> =
        repository.sortByHighPriority.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            emptyList()
        )

    private val _sortState =
        MutableStateFlow<RequestState<Priority>>(RequestState.Idle)
    val sortState: StateFlow<RequestState<Priority>> = _sortState

    fun readSortState() {
        _sortState.value = RequestState.Loading
        try {
            viewModelScope.launch {
                dataStoreRepository.readSortState
                    .map { Priority.valueOf(it) }
                    .collect {
                        _sortState.value = RequestState.Success(it)
                    }
            }
        } catch (e: Exception) {
            _sortState.value = RequestState.Error(e)
        }
    }
    fun persisSortState(priority: Priority) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistSortState(priority = priority)
        }
    }

    /* 언더바(_) 쓰임
    * 외부에서 상태를 변경하지 못하게 하고, 내부(ShareViewModel)에서는 변경이 가능하게 하기 위한 목적*/
    // _allTasks = 할일 작업 목록의 요청 상태 변경 가능 상태 흐름을 쉬고 있는 상태로 초기화
    // allTasks = 할일 작업 목록 요청 상태 흐름을 외부에서 변경할 수 없게 만듬
    private val _allTasks =
        MutableStateFlow<RequestState<List<ToDoTask>>>(RequestState.Idle)
    val allTasks: StateFlow<RequestState<List<ToDoTask>>> = _allTasks

    /*getAllTasks - 모든 작업을 가져오는 기능
    * 모든 작업의 상태 값을 - Loading으로 변경
    * coroutine 관 viewModel를 같이 control 할 수 있게 실행하고, ToDoRepository에서 모든 작업을 모으고,
    * 모은 작업의 값을 모두 요청 성공 상태로 변경한 값을 넣는다.
    * 하는데 예외가 발생한다면 모든 작업에 대한 값을 에러 상태로 변경한다.*/
    fun getAllTasks() {
        _allTasks.value = RequestState.Loading
        try {
            viewModelScope.launch {
                repository.getAllTasks.collect {
                    _allTasks.value = RequestState.Success(it)
                }
            }
        } catch (e: Exception) {
            _allTasks.value = RequestState.Error(e)
        }
    }

    // _selectedTask = 할일 작업 목록(작업이 없을 수 있음) 상태, 즉 아무것도 선택하지 않은(null)로 초기화
    // selectedTask = 할일 작업 목록(작업이 없을 수 있음) 상태 흐름 값을 외부에서 변경 할 수 없게 제작
    private val _selectedTask: MutableStateFlow<ToDoTask?> = MutableStateFlow(null)
    val selectedTask: StateFlow<ToDoTask?> = _selectedTask

    /*getSelectedTask - 작업을 선택하는 기능 (선택하기 위해서는 작업 아이디가 필요함)
    * coroutine과 viewModel를 같이 control 할 수 있게 실행하고, ToDoRepository에서 작업을 선택한 것들을 모으고,
    * 각 선택한 작업들은 할일 작업 목록 상태에 값을 넣는다. */
    fun getSelectedTask(taskId: Int) {
        viewModelScope.launch {
            repository.getSelectedTask(taskId = taskId).collect { task ->
                _selectedTask.value = task
            }
        }
    }

    /*Scope
    * GlobalScope - 별도 생명 주기 관리가 필요 없으며 앱 시작 ~ 종류 긴 시간 실행되는 코루틴에 적합
    * CoroutineScope - 버튼을 눌러 다운로드하거나 서버와 통신할 때만 시작, 완료되면 종료하는 용도로 사용
    * viewModelScope - Jetpack 아키텍처의 뷰모델 컴포넌트를 사용할 때 viewModel에서 사용하기 위해 제공되는 Scope
    * 이 코루틴은 viewModel이 destroy될 때 자동으로 취소한다.
    * */

    /*addTask
    * viewModel 관리 Coroutine Scope를 코투린 실행, 상태 관리하는(launch - IO 작업)를 실행시킨다.
    * IO는 이미지 다운로드, 파일 입출력, 네트워킹 , DB작업을 할 때 사용합니다.
    * 추가하고 싶은 작업을 변수에 담는다.
    * repository에 존재하는 addTask를 불러 작업을 넣는다.*/
    private fun addTask() {
        viewModelScope.launch(Dispatchers.IO) {
            val toDoTask = ToDoTask(
                title = title.value,
                description = description.value,
                priority = priority.value
            )
            repository.addTask(toDoTask = toDoTask)
        }
        // 작업을 추가하면 검색 바를 종료 상태로 만들어라
        searchAppBarState.value = SearchAppBarState.CLOSED
    }

    /*updateTask
    * 추가 되어있는 작업을 갱신
    * repository에 존재하는 updateTask를 수행한다.*/
    private fun updateTask() {
        viewModelScope.launch(Dispatchers.IO) {
            val toDoTask = ToDoTask(
                id = id.value,
                title = title.value,
                description = description.value,
                priority = priority.value
            )
            repository.updateTask(toDoTask = toDoTask)
        }
    }

    /*deleteTask
    * 작업을 삭제하는 역할
    * repositoru에 존재하는 deleteTask를 수행한다.*/
    private fun deleteTask() {
        viewModelScope.launch(Dispatchers.IO) {
            val toDoTask = ToDoTask(
                id = id.value,
                title = title.value,
                description = description.value,
                priority = priority.value
            )
            repository.deleteTask(toDoTask = toDoTask)
        }
    }

    /*deleteAllTasks
    * 모든 작업 삭제
    * vieModelScope으로 ViewModel 관리 Coroutine Scope 코투린 실행 시킨다
    * 이렇게 실행 시키는 이유는 Compose가 비동기 UI이기 때문에 컴포넌트를 사용할 때
    * ViewModel이 destroy될 때 자동으로 같이 destroy 되기 위함이다.
    * 상태 관리 IO - DB 작업 용도로 사용했습니다.
    * repository에 존재하는 deleteAllTasks를 실행시킨다.*/
    private fun deleteAllTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllTasks()
        }
    }

    /*handleDatabaseAction
    * 사용자 행동을 조작
    * Action에 해당하는 Database 작업을 수행한다.*/
    fun handleDatabaseAction(action: Action) {
        when (action) {
            Action.ADD -> {
                addTask()
            }
            Action.UPDATE -> {
                updateTask()
            }
            Action.DELETE -> {
                deleteTask()
            }
            Action.DELETE_ALL -> {
                deleteAllTasks()
            }
            Action.UNDO -> {
                addTask()
            }
            else -> {

            }
        }
        this.action.value = Action.NO_ACTION
    }

    /*updateTaskFields - 할일을 선택(선택안할 수 도 있음)
    * 선택되었을 때 각 값에 선택한 정보를 넣음
    * 선택되지 않았을 때 기본 값으로 설정*/
    fun updateTaskFields(selectedTask: ToDoTask?) {
        if (selectedTask != null) {
            id.value = selectedTask.id
            title.value = selectedTask.title
            description.value = selectedTask.description
            priority.value = selectedTask.priority
        } else {
            id.value = 0
            title.value = ""
            description.value = ""
            priority.value = Priority.LOW
        }
    }

    /*updateTitle - 제목을 업데이트
    * 길이가 20까지만 입력 가능*/
    fun updateTitle(newTitle: String) {
        if (newTitle.length < MAX_TITLE_LENGTH) {
            title.value = newTitle
        }
    }

    /*validateFields
    * 제목과 설명이 둘 중에 하나만 비어 있어도 false 반환*/
    fun validateFields(): Boolean {
        return title.value.isNotEmpty() && description.value.isNotEmpty()

    }
}