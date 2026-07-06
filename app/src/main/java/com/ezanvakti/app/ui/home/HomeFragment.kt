package com.ezanvakti.app.ui.home

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezanvakti.app.R
import com.ezanvakti.app.data.model.PrayerDay
import com.ezanvakti.app.databinding.FragmentHomeBinding
import com.ezanvakti.app.ui.adapter.PrayerRow
import com.ezanvakti.app.ui.adapter.PrayerTimeAdapter
import com.ezanvakti.app.util.PrayerTimeUtils

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private val adapter = PrayerTimeAdapter()
    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerTimes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTimes.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.load(forceLocationRefresh = true) }
        binding.btnRefresh.setOnClickListener { viewModel.load(forceLocationRefresh = true) }

        binding.cardAylik.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_aylik)
        }
        binding.cardRamazan.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_ramazan)
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.swipeRefresh.isRefreshing = false
            when (state) {
                is HomeState.Loading -> {
                    binding.textLocation.text = getString(R.string.yukleniyor)
                }
                is HomeState.Success -> {
                    val loc = state.location
                    binding.textLocation.text = if (loc.district.isNotBlank())
                        "${loc.city} / ${loc.district}" else loc.city
                    binding.textDate.text = state.day.dateReadable
                    bindTimesList(state.day)
                    startCountdown(state.day)
                }
                is HomeState.Error -> {
                    binding.textLocation.text = state.message
                }
            }
        }

        viewModel.load()
    }

    private fun bindTimesList(day: PrayerDay) {
        val activeIndex = PrayerTimeUtils.activeIndex(day)
        val icons = listOf(
            R.drawable.ic_moon, R.drawable.ic_sunrise, R.drawable.ic_sun,
            R.drawable.ic_sunset, R.drawable.ic_sunset, R.drawable.ic_moon
        )
        val rows = day.asOrderedList().mapIndexed { index, pair ->
            PrayerRow(pair.first, pair.second, icons[index], index == activeIndex)
        }
        adapter.submitList(rows)
    }

    private fun startCountdown(day: PrayerDay) {
        countDownTimer?.cancel()
        val next = PrayerTimeUtils.findNextPrayer(day)
        binding.textNextPrayerLabel.text = "${next.name} Vaktine"
        binding.textNextPrayerTime.text = next.timeText

        val remaining = next.timeMillis - System.currentTimeMillis()
        if (remaining <= 0) {
            binding.textCountdown.text = "00:00:00"
            return
        }

        countDownTimer = object : CountDownTimer(remaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.textCountdown.text = PrayerTimeUtils.formatCountdown(millisUntilFinished)
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
