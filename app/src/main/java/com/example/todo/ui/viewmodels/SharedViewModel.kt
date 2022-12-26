package com.example.todo.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.models.ToDoTask
import com.example.todo.data.repositories.ToDoRepository
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

    private val _allTasks =
        MutableStateFlow<List<ToDoTask>>(emptyList())
    val allTasks: StateFlow<List<ToDoTask>> = _allTasks

    fun getAllTasks() {
        viewModelScope.launch {
            repository.getAllTasks.collect {
                _allTasks.value = it
            }
        }
    }
}