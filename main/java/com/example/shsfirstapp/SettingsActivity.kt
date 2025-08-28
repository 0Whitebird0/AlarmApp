package com.example.shsfirstapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.shsfirstapp.data.AlarmDbHelper
import com.example.shsfirstapp.data.AlarmViewModel
import com.example.shsfirstapp.data.AlarmViewModelFactory
import com.example.shsfirstapp.databinding.ActivitySettingsBinding
import java.text.SimpleDateFormat
import java.util.*

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: AlarmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarSettings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "설정"

        val dbHelper = AlarmDbHelper.getInstance(this)
        val factory = AlarmViewModelFactory(application, dbHelper)
        viewModel = ViewModelProvider(this, factory).get(AlarmViewModel::class.java)

        // 전체 알람 스위치 설정
        binding.switchAlarm.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleAllAlarms(isChecked)
        }

        // 알람 목록 관찰
        viewModel.alarms.observe(this) { alarms ->
            val nextAlarm = alarms.filter { it.enabled }
                .minByOrNull { parseDateTime(it.date, it.time).timeInMillis }
            binding.textNextTask.text = "다음에 해야할 일: ${nextAlarm?.event ?: "없음"}"
            binding.textTimeLeft.text = "남은 시간: ${nextAlarm?.time ?: "-"}"
        }
    }

    private fun parseDateTime(date: String, time: String): Calendar {
        return Calendar.getInstance().apply {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                this.time = sdf.parse("$date $time") ?: Date()
            } catch (e: Exception) {
                this.time = Date()
            }
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navigateToMain()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }
}
