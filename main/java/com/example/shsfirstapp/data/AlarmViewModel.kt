package com.example.shsfirstapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shsfirstapp.util.AlarmScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel

class AlarmViewModel(
    application: Application, // Application 컨텍스트 추가
    // viewmodel의 라이프사이클은 activity와 fragment와 달리 앱 실행 중 긴 비중을 차지, application컨텍스트를 사용하여 앱 전체에서 공유할 수 있음 
    private val dbHelper: AlarmDbHelper
) : AndroidViewModel(application) {
    private val _alarms = MutableLiveData<List<Alarm>>()
    //읽고쓸수있는 private변수 
    val alarms: LiveData<List<Alarm>> get() = _alarms
    // 읽기전용으로 캡슐화 기능

    init {
        loadAlarms()
    }

    fun insertAlarm(alarm: Alarm) {
        // viewmodelscope부분은 viewmodel이 살아있는동안 dispatchers부분은 백그라운드 환경에서 실행한다는 뜻
        viewModelScope.launch(Dispatchers.IO) {
            // DB 삽입 후 생성된 ID를 스케줄에 등록
            val newId = dbHelper.insertAlarm(alarm)
            alarm.id = newId
            AlarmScheduler.scheduleAlarm(getApplication(), alarm)
            //loadalarms를 통하여 _alarms최신화
            loadAlarms()
        }
    }

    private fun loadAlarms() {
        viewModelScope.launch(Dispatchers.IO) {
            // db에서 모든 알람을 가져와 최신화하는 부분
            _alarms.postValue(dbHelper.getAllAlarms())
        }
    }

    fun deleteAlarm(alarmId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dbHelper.deleteAlarm(alarmId)
            loadAlarms() // LiveData 강제 갱신
        }
    }

    fun toggleAllAlarms(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dbHelper.updateAllAlarmsEnabled(enabled)
            loadAlarms()
        }
    }
    fun toggleAlarmEnabled(alarmId: Long, enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dbHelper.updateAlarmEnabled(alarmId, enabled)
            loadAlarms()
        }
    }


}

