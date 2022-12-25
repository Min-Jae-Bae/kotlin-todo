package com.example.todo.di

import android.content.Context
import androidx.room.Room
import com.example.todo.data.ToDoDatabase
import com.example.todo.util.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/* Hilt Injection (DI - Dependency Injection)
* Module - constructor inject 할 수 없는 경우 (Interface or abstract class 구현시 사용) Hilt가 알 수 있게 Module 임을 지정한다.
* InstallIn(~::class) - 어떤 Android class 사용할지 지정 (Hilt가 알 수 있게)한다.
* SingletonComponent - @Singleton(Scope 생성) 사용한다.
*
* @Provides - Room, Retrofit과 같은 외부 라이브러리에서 제공되는 클래스이므로 프로젝트 내에서 소유할 수 없는 경우 또는
* Builder 패턴 등을 통해 인스턴스를 생성해야 하는 경우에 사용한다.
* */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /*DB 제공
    * function Parameter - Application Name
    * function return - Application DB(Room) build */
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        ToDoDatabase::class.java,
        DATABASE_NAME
    ).build()

    // DB 접근 제공
    @Singleton
    @Provides
    fun provideDao(database: ToDoDatabase) = database.toDoDao()
}