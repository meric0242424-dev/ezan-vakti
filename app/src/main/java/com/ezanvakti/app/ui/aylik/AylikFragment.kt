package com.ezanvakti.app.ui.aylik

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezanvakti.app.databinding.FragmentAylikBinding
import com.ezanvakti.app.ui.adapter.MonthlyAdapter

class AylikFragment : Fragment() {

    private var _binding: FragmentAylikBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AylikViewModel by viewModels()
    private val adapter = MonthlyAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAylikBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerMonth.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMonth.adapter = adapter

        binding.btnPrevMonth.setOnClickListener { viewModel.changeMonth(-1) }
        binding.btnNextMonth.setOnClickListener { viewModel.changeMonth(1) }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.textMonthTitle.text = state.monthTitle
            adapter.submitList(state.days)
            state.error?.let { binding.textMonthTitle.text = "${state.monthTitle} — $it" }
        }
        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
