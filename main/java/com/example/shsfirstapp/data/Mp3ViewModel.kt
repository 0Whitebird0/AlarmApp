package com.example.shsfirstapp.data

import android.content.ContentValues
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Mp3ViewModel(private val dbHelper: AlarmDbHelper) : ViewModel() {

    private val _mp3s = MutableLiveData<List<Mp3>>()   // 내부 LiveData
    val mp3s: LiveData<List<Mp3>> get() = _mp3s        // 외부 노출

    init {
        loadMp3s() // 초기 로드
    }

    fun addMp3(name: String, path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(AlarmDbHelper.COLUMN_MP3_NAME, name)
                put(AlarmDbHelper.COLUMN_MP3_PATH, path)
            }
            db.insert(AlarmDbHelper.TABLE_MP3, null, values)
            db.close()
            loadMp3s() // 추가 후 목록 갱신
        }
    }

    fun loadMp3s() {
        viewModelScope.launch(Dispatchers.IO) {
            val db = dbHelper.readableDatabase
            val cursor = db.query(
                AlarmDbHelper.TABLE_MP3,
                null, null, null, null, null, null
            )
            val mp3s = mutableListOf<Mp3>()
            while (cursor.moveToNext()) {
                mp3s.add(
                    Mp3(
                        cursor.getLong(cursor.getColumnIndexOrThrow(AlarmDbHelper.COLUMN_MP3_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AlarmDbHelper.COLUMN_MP3_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AlarmDbHelper.COLUMN_MP3_PATH))
                    )
                )
            }
            cursor.close()
            db.close()
            _mp3s.postValue(mp3s)
        }
    }
}
