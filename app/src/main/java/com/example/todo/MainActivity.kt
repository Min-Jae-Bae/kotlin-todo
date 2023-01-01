package com.example.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.todo.navigation.SetupNavigation
import com.example.todo.ui.theme.TodoTheme
import com.example.todo.ui.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

/*AndroidEntryPoint
* 해당 클래스의 Dependency Container(비유: 물류센터) 생성하는 Annotation
* 즉 Hilt Component 의존성들을 지정 클래스(AndroidEntryPoint 클래스)로 보낼 수 있는 집합 저장소(Container)라고 볼 수 있다.
* */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Host 나중 초기화
    private lateinit var navController: NavHostController
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoTheme {
                // rememberNavController - 이 메서드는 NavController를 만든다
                // SetupNavigation - NavController 와 NavHost 연결
                navController = rememberNavController()
                SetupNavigation(
                    navController = navController,
                    sharedViewModel = sharedViewModel
                )
            }
        }
    }
}