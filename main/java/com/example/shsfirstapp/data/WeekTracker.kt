package com.example.shsfirstapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class WeekTracker : ViewModel() {
    private val _currentWeek = MutableLiveData<Pair<Long, Long>>()
    val currentWeek: LiveData<Pair<Long, Long>> get() = _currentWeek

    init {
        updateCurrentWeek()
    }

    // 이번 주로 이동
    fun updateCurrentWeek() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val start = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val end = calendar.timeInMillis
        _currentWeek.value = Pair(start, end)
    }

    // 이전 주로 이동
    fun moveToPreviousWeek() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = _currentWeek.value?.first ?: System.currentTimeMillis()
            add(Calendar.DAY_OF_YEAR, -7)
        }
        setWeekRange(calendar)
    }

    // 다음 주로 이동
    fun moveToNextWeek() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = _currentWeek.value?.first ?: System.currentTimeMillis()
            add(Calendar.DAY_OF_YEAR, 7)
        }
        setWeekRange(calendar)
    }

    // 주간 범위 설정
    private fun setWeekRange(calendar: Calendar) {
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val start = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val end = calendar.timeInMillis
        _currentWeek.value = Pair(start, end)
    }
}
