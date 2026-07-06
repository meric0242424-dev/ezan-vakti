package com.ezanvakti.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ezanvakti.app.R
import com.ezanvakti.app.data.model.PrayerDay
import com.ezanvakti.app.databinding.ItemMonthRowBinding
import java.util.Calendar

class MonthlyAdapter(private var days: List<PrayerDay> = emptyList()) :
    RecyclerView.Adapter<MonthlyAdapter.MonthRowViewHolder>() {

    fun submitList(newDays: List<PrayerDay>) {
        days = newDays
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthRowViewHolder {
        val binding = ItemMonthRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MonthRowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonthRowViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount(): Int = days.size

    inner class MonthRowViewHolder(private val binding: ItemMonthRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(day: PrayerDay) {
            // gregorianDate comes back as dd-MM-yyyy from Aladhan
            val shortDate = day.gregorianDate.split("-").let { parts ->
                if (parts.size == 3) "${parts[0]}/${parts[1]}" else day.gregorianDate
            }
            binding.colDate.text = shortDate
            binding.colImsak.text = day.imsak
            binding.colGunes.text = day.gunes
            binding.colOgle.text = day.ogle
            binding.colIkindi.text = day.ikindi
            binding.colAksam.text = day.aksam
            binding.colYatsi.text = day.yatsi

            val isToday = isSameDayAsToday(day.gregorianDate)
            binding.rowRoot.setBackgroundResource(
                if (isToday) R.drawable.bg_row_active else R.drawable.bg_row_default
            )
        }

        private fun isSameDayAsToday(gregorianDate: String): Boolean {
            val parts = gregorianDate.split("-")
            if (parts.size != 3) return false
            val cal = Calendar.getInstance()
            val today = cal.get(Calendar.DAY_OF_MONTH)
            val month = cal.get(Calendar.MONTH) + 1
            val year = cal.get(Calendar.YEAR)
            return parts[0].toIntOrNull() == today &&
                parts[1].toIntOrNull() == month &&
                parts[2].toIntOrNull() == year
        }
    }
}
