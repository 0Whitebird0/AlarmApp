package com.example.shsfirstapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.shsfirstapp.data.AlarmDbHelper

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmTest", "AlarmReceiver onReceive() called!")

        val alarmId = intent.getLongExtra("ALARM_ID", -1)
        if (alarmId == -1L) {
            Log.w("AlarmTest", "AlarmReceiver: ALARM_ID가 전달되지 않음")
            return
        }

        // DB에서 알람 활성화 상태 확인
        val dbHelper = AlarmDbHelper.getInstance(context)
        val alarm = dbHelper.getAlarmById(alarmId)

        if (alarm == null) {
            Log.w("AlarmTest", "AlarmReceiver: 해당 ID의 알람이 DB에 없음")
            return
        }

        if (!alarm.enabled) {
            Log.d("AlarmTest", "AlarmReceiver: 알람 ID=$alarmId 는 비활성화 상태이므로 실행하지 않음")
            return
        }

        val mp3Path = alarm.mp3Path
        if (!mp3Path.isNullOrEmpty()) {
            val serviceIntent = Intent(context, com.example.shsfirstapp.service.AlarmService::class.java).apply {
                putExtra("MP3_PATH", mp3Path)
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            Log.d("AlarmTest", "AlarmService 시작: $mp3Path")
        } else {
            Log.w("AlarmTest", "MP3 경로가 비어있음")
        }
    }
}
