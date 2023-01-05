package com.example.todo.util

/*RequestState - <out T> 받은 상태를 RequestTate의 하위 클래스(TodoTask)라고 인식하도록 정의
* T(type) - 여러가지 타입을 받는 다는 표현을 대문자로 표시
* sealed class는 enum class와 다르게 객체를 여러개 생성할 수 있다.
* Nothing: 모든 타입의 서브 클래스, Any: 모든 타입의 상위 클래스
* Idle : 쉬고 있는 상태
* Loading : 로딩 상태
* Success : 어떤 data에 대한 요청을 성공하면 Success return
* Error : 어떤 data에 대한 요청을 실패하면 예외를 던짐*/
sealed class RequestState<out T> {
    object Idle : RequestState<Nothing>()
    object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T) : RequestState<T>()
    data class Error(val error: Throwable) : RequestState<Nothing>()
}

/*즉 App이 상태에 대한 어떤 데이터에 대한 요청을 하고 성공하면 Success,
* 실패하면 Error를 리턴하고 아직 처리가 끝나지 않았다면 Idle, Loading를 리턴한다는
* 시나리오라고 할 수 있다.*/