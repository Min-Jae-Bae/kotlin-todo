package com.example.todo.data

import androidx.room.*
import com.example.todo.data.models.ToDoTask
import kotlinx.coroutines.flow.Flow

/* DAO(Dat Access Object)
* Room에서는 SQL을 이용한 직적접인 쿼리 접근 대신 DAO 이용
* DAO는 인터페이스 or 추상클래스로 구현되어야 함
*
* Query - 데이터를 읽거나 사용
* Insert - 데이터 삽입
* Update - 전달받은 매개변수의 PK값에 매칭되는 entity를 찾아 갱신
* Delete - 데이터 삭제
* */
@Dao
interface ToDoDao {

    // id 오름차순 정렬하여 보여줌
    @Query("SELECT * FROM todo_table ORDER BY id ASC")
    fun getAllTasks(): Flow<List<ToDoTask>>

    // 선택한 id를 보여줌
    @Query("SELECT * FROM todo_table WHERE id=:taskId")
    fun getSelectedTask(taskId: Int): Flow<ToDoTask>

    // 데이터 삽입으로 충돌 발생시 기존 데이터 유지하고 입력데이터 버림
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(toDoTask: ToDoTask)

    // 입력한 데이터 갱신
    @Update
    suspend fun updateTask(toDoTask: ToDoTask)

    // 입력한 데이터 삭제
    @Delete
    suspend fun deleteTask(toDoTask: ToDoTask)

    // 모든 데이터 삭제
    @Query("DELETE FROM todo_table")
    suspend fun deleteAllTasks()

    // 제목, 설명에 포함된 검색을 보여줌 (:는 변수 지정)
    @Query("SELECT * FROM todo_table WHERE title LIKE :searchQuery OR description LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): Flow<List<ToDoTask>>

    // 중요성 낮은 순서로 정렬해서 보여줌
    @Query(
        """
        SELECT * FROM todo_table ORDER BY 
    CASE 
        WHEN priority LIKE 'L%' THEN 1
        WHEN priority LIKE 'M%' THEN 2 
        WHEN priority LIKE 'H%' THEN 3 
    END
    """
    )
    fun sortByLowPriority(): Flow<List<ToDoTask>>

    // 중요성 높은 순서로 정렬해서 보여줌
    @Query(
        """SELECT * FROM todo_table ORDER BY 
    CASE
        WHEN priority LIKE 'H%' THEN 1
        WHEN priority LIKE 'M%' THEN 2
        WHEN priority LIKE 'L%' THEN 3
   END
   """
    )
    fun sortByHighPriority(): Flow<List<ToDoTask>>
}