package com.example.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import com.example.todo.navigation.SetupNavigation
import com.example.todo.ui.theme.TodoTheme
import com.example.todo.ui.viewmodels.SharedViewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint

/*AndroidEntryPoint
* 해당 클래스의 Dependency Container(비유: 물류센터) 생성하는 Annotation
* 즉 Hilt Component 의존성들을 지정 클래스(AndroidEntryPoint 클래스)로 보낼 수 있는 집합 저장소(Container)라고 볼 수 있다.
* */
@ExperimentalAnimationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //Host 나중 초기화

    /* viewModel
    *  UI 관련 데이터를 저장하고 관리하는 역할
    *
    * by
    * -구성을 활용한 패턴, 모든 동작들을 소유하고 있는 객체에게 모두 위임하는 형식*/
    private lateinit var navController: NavHostController
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            TodoTheme {
                // rememberNavController - 이 메서드는 NavController를 만든다
                // SetupNavigation - NavController 와 NavHost 연결
                navController = rememberAnimatedNavController()
                SetupNavigation(
                    navController = navController,
                    sharedViewModel = sharedViewModel
                )
            }
        }
    }
}