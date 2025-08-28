package com.example.shsfirstapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class Mp3ViewModelFactory(private val dbHelper: AlarmDbHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(Mp3ViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return Mp3ViewModel(dbHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
