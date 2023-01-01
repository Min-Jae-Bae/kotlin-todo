package com.example.todo.data.repositories

import com.example.todo.data.ToDoDao
import com.example.todo.data.models.ToDoTask
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/*Repository Pattern
* 1. Business Logic (Entity)와 Data Access Object(DAO)를 분리 -> 관심사의 분리
* 2. 중앙 집중 처리 방식 - 일관된 Logic 및 Data 제공
* 3. Data encapsulation
*
* ToDoRepository - Repository 생성시 DAO가 필요함을 Hilt에게 알려줌
* @ViewModelScoped
* - Coroutine 관리 단위는 Scope
* - ViewModel 속 관리도 Scope 단위로 지정하기 위해 Hilt에게 알려줌*/
@ViewModelScoped
class ToDoRepository @Inject constructor(private val toDoDao: ToDoDao) {

    val getAllTasks: Flow<List<ToDoTask>> = toDoDao.getAllTasks()
    val sortByLowPriority: Flow<List<ToDoTask>> = toDoDao.sortByLowPriority()
    val sortByHighPriority: Flow<List<ToDoTask>> = toDoDao.sortByHighPriority()

    fun getSelectedTask(taskId: Int): Flow<ToDoTask> {
        return toDoDao.getSelectedTask(taskId = taskId)
    }

    suspend fun addTask(toDoTask: ToDoTask) {
        toDoDao.addTask(toDoTask = toDoTask)
    }

    suspend fun updateTask(toDoTask: ToDoTask) {
        toDoDao.updateTask(toDoTask = toDoTask)
    }

    suspend fun deleteTask(toDoTask: ToDoTask) {
        toDoDao.deleteTask(toDoTask = toDoTask)
    }

    suspend fun deleteAllTasks() {
        toDoDao.deleteAllTasks()
    }

    fun searchDatabase(searchQuery: String): Flow<List<ToDoTask>> {
        return toDoDao.searchDatabase(searchQuery = searchQuery)
    }
}