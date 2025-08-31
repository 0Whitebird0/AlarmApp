package com.example.shsfirstapp.data

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
// alarmviewmodel을 생성하기 위한 부분, factory를 통하여 provider에게 생성하라고 시켜야함, 재사용성과 가독성용이함
class AlarmViewModelFactory(
    private val application: Application,
    private val dbHelper: AlarmDbHelper
) : ViewModelProvider.Factory {
    // T는 ViewModel 하위 타입으로 제한된 제네릭
    // 제네릭 선언시 런타임으로 타입을 사용하려면 Class<T>이런식으로 타입을 알려줘야함
    //modelclass는 단순히 타입만 알려주고, 정보는 factory에 있음
    override fun <T : ViewModel> create(modelClass: Class<T>): T { // 제네릭타입 선언
        return AlarmViewModel(application, dbHelper) as T
    }
}

