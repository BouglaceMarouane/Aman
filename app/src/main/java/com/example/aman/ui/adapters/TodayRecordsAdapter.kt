package com.example.aman.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aman.data.entities.WaterIntake
import com.example.aman.databinding.ItemWaterRecordBinding
import java.text.SimpleDateFormat
import java.util.*

class TodayRecordsAdapter(
    private val onDeleteClick: (WaterIntake) -> Unit
) : ListAdapter<WaterIntake, TodayRecordsAdapter.RecordViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = ItemWaterRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = getItem(position)
        holder.bind(record)

        // Hide timeline line for last item
        if (position == itemCount - 1) {
            holder.binding.timelineLine.visibility = View.INVISIBLE
        } else {
            holder.binding.timelineLine.visibility = View.VISIBLE
        }
    }

    inner class RecordViewHolder(
        val binding: ItemWaterRecordBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(record: WaterIntake) {
            binding.tvAmount.text = "${record.amountMl} ml"
            binding.tvTime.text = formatTime(record.timestamp)

            binding.btnDelete.setOnClickListener {
                onDeleteClick(record)
            }
        }

        private fun formatTime(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<WaterIntake>() {
        override fun areItemsTheSame(oldItem: WaterIntake, newItem: WaterIntake): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WaterIntake, newItem: WaterIntake): Boolean {
            return oldItem == newItem
        }
    }
}