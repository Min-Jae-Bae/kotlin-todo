package com.example.todo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todo.data.models.ToDoTask

/*Database
* entities = 현재 테이블
* version = 스키마 변경시 version 변경
* exportSchema = DB version history 기록 여부
* */
@Database(entities = [ToDoTask::class], version = 1, exportSchema = false)
abstract class ToDoDatabase: RoomDatabase() {

    abstract fun toDoDao(): ToDoDao

}