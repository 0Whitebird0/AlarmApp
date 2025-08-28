package com.example.shsfirstapp.ui.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shsfirstapp.data.AlarmDbHelper
import com.example.shsfirstapp.data.AlarmViewModel
import com.example.shsfirstapp.data.AlarmViewModelFactory
import com.example.shsfirstapp.databinding.FragmentScheduleTabBinding
import java.text.SimpleDateFormat
import java.util.*

class ScheduleTabFragment : Fragment() {
    private lateinit var viewModel: AlarmViewModel
    private var _binding: FragmentScheduleTabBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ScheduleTabAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleTabBinding.inflate(inflater, container, false)
        val dbHelper = AlarmDbHelper.getInstance(requireContext())
        val factory = AlarmViewModelFactory(requireActivity().application, dbHelper)
        viewModel = ViewModelProvider(requireActivity(), factory).get(AlarmViewModel::class.java)
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = ScheduleTabAdapter(viewModel) { alarm -> viewModel.deleteAlarm(alarm.id) }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.alarms.observe(viewLifecycleOwner) { alarms ->
            val currentTime = Calendar.getInstance().timeInMillis
            val filteredSortedList = alarms
                .filter { alarm ->
                    val calendar = parseDateTime(alarm.date, alarm.time)
                    calendar.timeInMillis > currentTime
                }
                .sortedBy { parseDateTime(it.date, it.time).timeInMillis }

            adapter.submitList(filteredSortedList) // 어댑터에 데이터만 갱신
        }
    }

    private fun parseDateTime(date: String, time: String): Calendar {
        return Calendar.getInstance().apply {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val parsedTime = sdf.parse("$date $time")
                this.time = parsedTime ?: Date()
            } catch (e: Exception) {
                e.printStackTrace()
                this.time = Date()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}