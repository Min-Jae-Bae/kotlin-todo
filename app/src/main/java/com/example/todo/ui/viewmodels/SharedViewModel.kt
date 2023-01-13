package com.example.todo.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.models.Priority
import com.example.todo.data.models.ToDoTask
import com.example.todo.data.repositories.ToDoRepository
import com.example.todo.util.Constants.MAX_TITLE_LENGTH
import com.example.todo.util.RequestState
import com.example.todo.util.SearchAppBarState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
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
) : ViewModel() {

    val id: MutableState<Int> = mutableStateOf(0)
    val title: MutableState<String> = mutableStateOf("")
    val description: MutableState<String> = mutableStateOf("")
    val priority: MutableState<Priority> = mutableStateOf(Priority.LOW)

    val searchAppBarState: MutableState<SearchAppBarState> =
        mutableStateOf(SearchAppBarState.CLOSED)
    val searchTextState: MutableState<String> = mutableStateOf("")

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