package com.example.shsfirstapp.ui.home

import ScheduleItemAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shsfirstapp.data.Alarm
import com.example.shsfirstapp.data.WeekScheduleItem
import com.example.shsfirstapp.databinding.ItemWeekScheduleBinding

class WeekScheduleAdapter(
    private val onDeleteClick: (Alarm) -> Unit
) : ListAdapter<WeekScheduleItem, WeekScheduleAdapter.WeekScheduleViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekScheduleViewHolder {
        val binding = ItemWeekScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeekScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeekScheduleViewHolder, position: Int) {
        holder.bind(getItem(position), onDeleteClick)
    }

    class WeekScheduleViewHolder(private val binding: ItemWeekScheduleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WeekScheduleItem, onDeleteClick: (Alarm) -> Unit) {
            binding.textDay.text = item.day
            binding.recyclerSchedules.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = ScheduleItemAdapter(item.alarms, onDeleteClick)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<WeekScheduleItem>() {
            override fun areItemsTheSame(oldItem: WeekScheduleItem, newItem: WeekScheduleItem): Boolean {
                return oldItem.day == newItem.day
            }

            override fun areContentsTheSame(oldItem: WeekScheduleItem, newItem: WeekScheduleItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
