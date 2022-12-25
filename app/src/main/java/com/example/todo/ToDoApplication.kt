package com.example.todo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/*HiltAndroidApp
* 해당 Application 안에 Hilt 코드 자동 생성 Annotation*/
@HiltAndroidApp
class ToDoApplication: Application()