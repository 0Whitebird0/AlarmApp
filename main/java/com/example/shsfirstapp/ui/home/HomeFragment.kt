package com.example.shsfirstapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shsfirstapp.MainActivity
import com.example.shsfirstapp.data.AlarmDbHelper
import com.example.shsfirstapp.data.AlarmViewModel
import com.example.shsfirstapp.data.AlarmViewModelFactory
import com.example.shsfirstapp.data.WeekScheduleItem
import com.example.shsfirstapp.data.WeekTracker
import com.example.shsfirstapp.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AlarmViewModel
    private lateinit var weekTracker: WeekTracker
    private lateinit var adapter: WeekScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val dbHelper = AlarmDbHelper.getInstance(requireContext())
        // ✅ Application과 dbHelper 모두 전달
        val factory = AlarmViewModelFactory(requireActivity().application, dbHelper)
        viewModel = ViewModelProvider(requireActivity(), factory).get(AlarmViewModel::class.java)
        weekTracker = ViewModelProvider(requireActivity()).get(WeekTracker::class.java)

        adapter = WeekScheduleAdapter { alarm ->
            viewModel.deleteAlarm(alarm.id)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 주간 이동 버튼 리스너
        binding.btnPrevWeek.setOnClickListener {
            weekTracker.moveToPreviousWeek()
        }
        binding.btnNextWeek.setOnClickListener {
            weekTracker.moveToNextWeek()
        }

        // 주간 날짜 범위 표시 및 스케줄표 자동 갱신
        weekTracker.currentWeek.observe(viewLifecycleOwner) { (start, end) ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.textWeekRange.text = "${sdf.format(Date(start))} ~ ${sdf.format(Date(end))}"

            // 오늘 0시 0분 0초의 타임스탬프 구하기
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val todayStart = today.timeInMillis

            viewModel.alarms.observe(viewLifecycleOwner) { alarms ->
                val filteredAlarms = alarms.filter { alarm ->
                    val alarmTime = parseDateTime(alarm.date, alarm.time).timeInMillis
                    // 1. 이번주 범위에 포함
                    // 2. 오늘 이후(오늘 포함)
                    alarmTime in start..end && alarmTime >= todayStart
                }
                val grouped = filteredAlarms.groupBy { getDayAbbreviation(it.date) }
                val weekSchedule = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN").map { day ->
                    WeekScheduleItem(day, grouped[day] ?: emptyList())
                }
                adapter.submitList(weekSchedule)
            }
        }

    }

    private fun parseDateTime(dateStr: String, timeStr: String): Calendar {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return Calendar.getInstance().apply {
            time = sdf.parse("$dateStr $timeStr") ?: Date()
        }
    }

    private fun getDayAbbreviation(dateStr: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateStr) ?: return ""
        return SimpleDateFormat("EEE", Locale.ENGLISH).format(date).uppercase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
