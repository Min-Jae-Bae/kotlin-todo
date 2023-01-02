package com.example.todo

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.todo.data.ToDoDao
import com.example.todo.data.ToDoDatabase
import com.example.todo.data.models.Priority
import com.example.todo.data.models.ToDoTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
@SmallTest
class ToDoDaoTest {

    private lateinit var toDoDatabase: ToDoDatabase
    private lateinit var toDoDao: ToDoDao

    @Before
    fun setUpDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        toDoDatabase = Room.inMemoryDatabaseBuilder(
            context,
            ToDoDatabase::class.java
        ).allowMainThreadQueries().build()
        toDoDao = toDoDatabase.toDoDao()
    }

    @After
    fun closeDatabase() {
        toDoDatabase.close()
    }


    @Test
    fun addTask_returnsTrue() = runBlocking {
        val toDoTask =
            ToDoTask(id = 0, title = "Study", description = "Coding", priority = Priority.HIGH)
        toDoDao.addTask(toDoTask = toDoTask)

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            toDoDao.getAllTasks().collect {
                assertEquals(it, toDoTask)
            }
        }
        latch.await()
        job.cancelAndJoin()
    }

/*    @Test
    fun updateWord_returnsTrue() = runBlocking {
        val firstTask =
            ToDoTask(id = 0, title = "Study", description = "Coding", priority = Priority.HIGH)
        toDoDao.addTask(firstTask)

        val secondTask =
            ToDoTask(id = 0, title = "Rest", description = "Watch TV", priority = Priority.LOW)
        toDoDao.updateTask(secondTask)

        val result = toDoDao.getSelectedTask(secondTask.id)
        assertEquals(result, secondTask)
    }*/
}