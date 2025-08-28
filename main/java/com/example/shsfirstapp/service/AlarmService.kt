package com.example.shsfirstapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.shsfirstapp.R

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val mp3Path = intent?.getStringExtra("MP3_PATH")
        Log.d("AlarmTest", "AlarmService onStartCommand: $mp3Path")

        // 포그라운드 알림 채널 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "alarm_service_channel"
            val channel = NotificationChannel(
                channelId,
                "알람 서비스 채널",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)

            val notification: Notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("알람 실행 중")
                .setContentText("알람 음악이 재생되고 있습니다.")
                .setSmallIcon(R.drawable.ic_alarm)
                .build()
            startForeground(1002, notification)
        }

        if (!mp3Path.isNullOrEmpty()) {
            try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(mp3Path)
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    setOnCompletionListener {
                        stopSelf()
                    }
                    prepare()
                    start()
                }
                Log.d("AlarmTest", "MP3 재생 시작: $mp3Path")
            } catch (e: Exception) {
                Log.e("AlarmTest", "MP3 재생 오류: ${e.message}")
                stopSelf()
            }
        } else {
            Log.w("AlarmTest", "MP3 경로가 비어있음")
            stopSelf()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
