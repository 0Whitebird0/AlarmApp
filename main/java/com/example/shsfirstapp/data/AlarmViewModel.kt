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
    private val dbHelper: AlarmDbHelper
) : AndroidViewModel(application) {
    private val _alarms = MutableLiveData<List<Alarm>>()
    val alarms: LiveData<List<Alarm>> get() = _alarms

    init {
        loadAlarms()
    }

    fun insertAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            // ✅ DB 삽입 후 생성된 ID를 alarm에 반영
            val newId = dbHelper.insertAlarm(alarm)
            alarm.id = newId
            AlarmScheduler.scheduleAlarm(getApplication(), alarm)
            loadAlarms()
        }
    }

    private fun loadAlarms() {
        viewModelScope.launch(Dispatchers.IO) {
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
