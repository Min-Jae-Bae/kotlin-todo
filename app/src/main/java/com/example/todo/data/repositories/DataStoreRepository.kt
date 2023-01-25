package com.example.todo.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.todo.data.models.Priority
import com.example.todo.util.Constants.PREFERENCE_KEY
import com.example.todo.util.Constants.PREFERENCE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

/*dataStore 인스턴스르 만들기 위해 preferencesDataStore 위임을 사용하여 수신기로 Context를 사용
* DataStore 인스턴스가 하나만 있음을 보장한다.*/
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)

/*ViewModelScoped
* 뷰 모델 범위 지정
* DataStoreRepository의 부 생성자를 주입(어플리케이션 이름을 받아야 함)*/
@ViewModelScoped
class DataStoreRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    /*PreferenceKeys
    * Preferences DataStore에서 데이터를 읽기 위해서는 정렬 키를 정의해야 한다.*/
    private object PreferenceKeys {
        val sortKey = stringPreferencesKey(name = PREFERENCE_KEY)
    }

    /*dataStore은 해당 앱의 dataStore이다*/
    private val dataStore = context.dataStore

    /*persistSortState - DataStorePreference의 priority 속성 정렬 상태 유지를 할 수 있는 정지 함수
    * Preferences DataStore에 데이터 쓰기
    * dataStore.edit를 호출하고 새 값을 설정한다.
    * preference 정렬 키를 각각 우선순위 이름으로 새 값을 설정
    * */
    suspend fun persistSortState(priority: Priority) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.sortKey] = priority.name
        }
    }

    /*readSortState
    * 데이터를 읽는 동안 예외 처리
    * DataStore가 파일에서 데이터를 읽는 동안 오류가 발생하면
    * IOException가 발생한다. IOException인 경우 비어있는 Preference를 내보내고
    * 나머지는 예외를 반환
    *
    * 정렬 상태를 리스트로 만들건데 각 정렬상태마다 기본 값은 이름 없음, 나머지는 정렬 키로 지정한다.
    * */
    val readSortState: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sortState = preferences[PreferenceKeys.sortKey] ?: Priority.NONE.name
            sortState
        }
}