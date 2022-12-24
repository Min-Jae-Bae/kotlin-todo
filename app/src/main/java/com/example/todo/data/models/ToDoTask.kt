package com.example.todo.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todo.util.Constants.DATABASE_TABLE

/*DB 테이블과 1대1 매칭 (PK 필요)
* 작업 데이터
* PK-자동 생성
* id(순서), title, description, priority(중요성-컬러)
* */
@Entity(tableName = DATABASE_TABLE)
data class ToDoTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val priority: Priority
)
