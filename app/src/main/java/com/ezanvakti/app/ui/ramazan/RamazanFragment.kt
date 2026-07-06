package com.ezanvakti.app.ui.ramazan

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ezanvakti.app.data.model.PrayerDay
import com.ezanvakti.app.databinding.FragmentRamazanBinding
import com.ezanvakti.app.ui.home.HomeState
import com.ezanvakti.app.ui.home.HomeViewModel
import com.ezanvakti.app.util.PrayerTimeUtils

private const val RAMADAN_MONTH_NUMBER = 9

class RamazanFragment : Fragment() {

    private var _binding: FragmentRamazanBinding? = null
    private val binding get() = _binding!!

    // Reuse HomeViewModel: it already resolves location + fetches today's data,
    // including the Hijri date needed to detect Ramadan.
    private val viewModel: HomeViewModel by viewModels()
    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRamazanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state is HomeState.Success) bind(state.day)
        }
        viewModel.load()
    }

    private fun bind(day: PrayerDay) {
        binding.textTodayImsak.text = day.imsak
        binding.textTodayIftar.text = day.aksam

        countDownTimer?.cancel()

        if (day.hijriMonthNumber == RAMADAN_MONTH_NUMBER) {
            binding.textHeadline.text = "Ramazan ${day.hijriDay}. Gün"
            val iftarMillis = PrayerTimeUtils.timeTodayMillis(day.aksam)
            val target = if (iftarMillis != null && iftarMillis > System.currentTimeMillis()) {
                iftarMillis
            } else {
                (PrayerTimeUtils.timeTodayMillis(day.imsak) ?: System.currentTimeMillis()) + 24L * 60 * 60 * 1000
            }
            startCountdown(target)
        } else {
            val monthsUntil = ((RAMADAN_MONTH_NUMBER - day.hijriMonthNumber) + 12) % 12
            val hijriDayNum = day.hijriDay.toIntOrNull() ?: 1
            val estimatedDaysRemaining = (monthsUntil * 29.5 - hijriDayNum).toInt().coerceAtLeast(0)
            binding.textHeadline.text = "Ramazan'a yaklaşık $estimatedDaysRemaining gün kaldı"

            val target = System.currentTimeMillis() + estimatedDaysRemaining * 24L * 60 * 60 * 1000
            startCountdown(target)
        }
    }

    private fun startCountdown(targetMillis: Long) {
        val remaining = targetMillis - System.currentTimeMillis()
        if (remaining <= 0) {
            binding.textDays.text = "0"
            binding.textHours.text = "0"
            binding.textMinutes.text = "0"
            binding.textSeconds.text = "0"
            return
        }
        countDownTimer = object : CountDownTimer(remaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val totalSeconds = millisUntilFinished / 1000
                val days = totalSeconds / (24 * 3600)
                val hours = (totalSeconds % (24 * 3600)) / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60
                binding.textDays.text = days.toString()
                binding.textHours.text = hours.toString()
                binding.textMinutes.text = minutes.toString()
                binding.textSeconds.text = seconds.toString()
            }

            override fun onFinish() {
                viewModel.load()
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}
