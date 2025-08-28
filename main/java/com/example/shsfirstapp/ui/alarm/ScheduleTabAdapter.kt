package com.example.shsfirstapp.ui.alarm

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shsfirstapp.data.Alarm
import com.example.shsfirstapp.data.AlarmViewModel
import com.example.shsfirstapp.databinding.ItemScheduleBinding

class ScheduleTabAdapter(
    private val viewModel: AlarmViewModel,
    private val onDeleteClick: (Alarm) -> Unit
) : androidx.recyclerview.widget.ListAdapter<Alarm, ScheduleTabAdapter.AlarmViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = getItem(position)
        holder.bind(alarm)
    }

    inner class AlarmViewHolder(private val binding: ItemScheduleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alarm: Alarm) {
            binding.textEvent.text = alarm.event
            binding.textTime.text = alarm.time
            binding.switchEnabled.isChecked = alarm.enabled

            binding.switchEnabled.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                viewModel.toggleAlarmEnabled(alarm.id, isChecked)
            }
            binding.btnDelete.setOnClickListener {
                onDeleteClick(alarm)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Alarm>() {
            override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm) = oldItem == newItem
        }
    }
}
