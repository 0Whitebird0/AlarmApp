package com.example.shsfirstapp.data

data class Alarm(
    var id: Long = 0,
    val time: String,
    val date: String,
    val event: String,
    val days: String,
    val mp3Path: String?,
    val enabled: Boolean
)

