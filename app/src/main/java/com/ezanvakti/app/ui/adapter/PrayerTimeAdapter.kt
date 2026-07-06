package com.ezanvakti.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ezanvakti.app.R
import com.ezanvakti.app.databinding.ItemPrayerTimeBinding

data class PrayerRow(
    val name: String,
    val time: String,
    val iconRes: Int,
    val isActive: Boolean
)

class PrayerTimeAdapter(private var rows: List<PrayerRow> = emptyList()) :
    RecyclerView.Adapter<PrayerTimeAdapter.RowViewHolder>() {

    fun submitList(newRows: List<PrayerRow>) {
        rows = newRows
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val binding = ItemPrayerTimeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        holder.bind(rows[position])
    }

    override fun getItemCount(): Int = rows.size

    inner class RowViewHolder(private val binding: ItemPrayerTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(row: PrayerRow) {
            binding.prayerName.text = row.name
            binding.prayerTime.text = row.time
            binding.icon.setImageResource(row.iconRes)
            if (row.isActive) {
                binding.rootRow.setBackgroundResource(R.drawable.bg_row_active)
                binding.speakerIcon.visibility = View.VISIBLE
            } else {
                binding.rootRow.setBackgroundResource(R.drawable.bg_row_default)
                binding.speakerIcon.visibility = View.INVISIBLE
            }
        }
    }
}
