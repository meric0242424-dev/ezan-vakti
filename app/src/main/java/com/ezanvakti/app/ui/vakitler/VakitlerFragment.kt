package com.ezanvakti.app.ui.vakitler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezanvakti.app.R
import com.ezanvakti.app.data.model.PrayerDay
import com.ezanvakti.app.databinding.FragmentVakitlerBinding
import com.ezanvakti.app.ui.adapter.PrayerRow
import com.ezanvakti.app.ui.adapter.PrayerTimeAdapter
import com.ezanvakti.app.util.PrayerTimeUtils

class VakitlerFragment : Fragment() {

    private var _binding: FragmentVakitlerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VakitlerViewModel by viewModels()
    private val adapter = PrayerTimeAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVakitlerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerTimes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTimes.adapter = adapter

        binding.btnPrevDay.setOnClickListener { viewModel.loadOffset(-1) }
        binding.btnNextDay.setOnClickListener { viewModel.loadOffset(1) }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            state.day?.let { bind(it) }
            state.error?.let { binding.textDate.text = it }
        }
        viewModel.load()
    }

    private fun bind(day: PrayerDay) {
        binding.textDate.text = day.dateReadable
        binding.textHijriDate.text = "${day.hijriDay} ${day.hijriMonth} ${day.hijriYear}"

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
