package com.example.shsfirstapp.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.shsfirstapp.data.Alarm
import com.example.shsfirstapp.receiver.AlarmReceiver
import java.util.Calendar

object AlarmScheduler {
    fun scheduleAlarm(context: Context, alarm: Alarm) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "ACTION_ALARM_TRIGGER"
            putExtra("ALARM_ID", alarm.id)
            putExtra("MP3_PATH", alarm.mp3Path)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(), // 또는 고유한 값 생성
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 시간 파싱 (HH:mm)
        val timeParts = alarm.time.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()

        val dateParts = alarm.date.split("-")
        val year = dateParts[0].toInt()
        val month = dateParts[1].toInt() - 1
        val day = dateParts[2].toInt()

        val calendar = Calendar.getInstance().apply {
            set(year, month, day, hour, minute, 0)
            set(Calendar.MILLISECOND, 0)
        }
        Log.d("AlarmTest", "알람 예약: ID=${alarm.id}, 시간=${calendar.time}")
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}
