package com.example.shsfirstapp.ui.alarm

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.shsfirstapp.data.Alarm
import com.example.shsfirstapp.data.AlarmDbHelper
import com.example.shsfirstapp.data.AlarmViewModel
import com.example.shsfirstapp.data.AlarmViewModelFactory
import com.example.shsfirstapp.data.Mp3
import com.example.shsfirstapp.databinding.FragmentAlarmTabBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AlarmTabFragment : Fragment() {

    private var _binding: FragmentAlarmTabBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: AlarmDbHelper
    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedTime: Calendar = Calendar.getInstance()
    private lateinit var viewModel: AlarmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = AlarmDbHelper.getInstance(requireContext())
        val factory = AlarmViewModelFactory(requireActivity().application, dbHelper)
        viewModel = ViewModelProvider(requireActivity(), factory).get(AlarmViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlarmTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMp3Spinner()
        setupDatePicker()
        setupTimePicker()
        setupSaveButton()
    }

    private fun setupMp3Spinner() {
        lifecycleScope.launch {
            val mp3List: List<Mp3> = withContext(Dispatchers.IO) {
                dbHelper.getAllMp3s()
            }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                mp3List.map { it.name }
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            binding.spinnerMp3.adapter = adapter
        }
    }

    private fun setupDatePicker() {
        binding.btnDatePicker.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    selectedDate.set(year, month, day)
                    binding.textSelectedDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupTimePicker() {
        binding.editTime.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hour, minute ->
                    selectedTime.set(Calendar.HOUR_OF_DAY, hour)
                    selectedTime.set(Calendar.MINUTE, minute)
                    binding.editTime.setText(SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedTime.time))
                },
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            if (validateInput()) {
                // DB에 직접 저장하지 말고 ViewModel을 통해 저장
                val mp3Name = binding.spinnerMp3.selectedItem.toString()
                lifecycleScope.launch {
                    val mp3Path = withContext(Dispatchers.IO) {
                        dbHelper.getAllMp3s().find { it.name == mp3Name }?.path
                    }
                    val alarm = Alarm(
                        time = binding.editTime.text.toString(),
                        date = binding.textSelectedDate.text.toString(),
                        event = binding.editEvent.text.toString(),
                        days = "",
                        mp3Path = mp3Path,
                        enabled = true
                    )
                    // ViewModel을 통해 알람 추가
                    viewModel.insertAlarm(alarm)

                    Toast.makeText(context, "알람이 저장되었습니다", Toast.LENGTH_SHORT).show()
                    clearInputs()
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        return when {
            binding.textSelectedDate.text.isEmpty() -> {
                Toast.makeText(context, "날짜를 선택해주세요", Toast.LENGTH_SHORT).show()
                false
            }
            binding.editTime.text.isNullOrEmpty() -> {
                Toast.makeText(context, "시간을 입력해주세요", Toast.LENGTH_SHORT).show()
                false
            }
            binding.editEvent.text.isNullOrEmpty() -> {
                Toast.makeText(context, "일정 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun clearInputs() {
        binding.textSelectedDate.text = ""
        binding.editTime.text?.clear()
        binding.editEvent.text?.clear()
        binding.spinnerMp3.setSelection(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
