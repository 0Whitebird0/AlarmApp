package com.example.shsfirstapp.ui.mp3

import androidx.recyclerview.widget.ListAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shsfirstapp.data.Mp3

class Mp3Adapter : ListAdapter<Mp3, Mp3Adapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(mp3: Mp3) {
            itemView.findViewById<TextView>(android.R.id.text1).text = mp3.name
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Mp3>() {
        override fun areItemsTheSame(oldItem: Mp3, newItem: Mp3) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Mp3, newItem: Mp3) = oldItem == newItem
    }
}
