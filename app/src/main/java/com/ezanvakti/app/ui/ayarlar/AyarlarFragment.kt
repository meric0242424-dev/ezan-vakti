package com.ezanvakti.app.ui.ayarlar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ezanvakti.app.R
import com.ezanvakti.app.data.local.PrefsManager
import com.ezanvakti.app.databinding.FragmentAyarlarBinding
import com.ezanvakti.app.databinding.RowSettingSwitchBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AyarlarFragment : Fragment() {

    private var _binding: FragmentAyarlarBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefs: PrefsManager

    private val prayerLabels = listOf(
        "imsak" to "İmsak Ezanı",
        "gunes" to "Güneş Ezanı",
        "ogle" to "Öğle Ezanı",
        "ikindi" to "İkindi Ezanı",
        "aksam" to "Akşam Ezanı",
        "yatsi" to "Yatsı Ezanı"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAyarlarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PrefsManager(requireContext())

        setupPrayerToggles()

        binding.rowShowNotification.rowTitle.text = getString(R.string.bildirim_goster)
        binding.rowShowNotification.rowSubtitle.text = getString(R.string.bildirim_aciklama)
        binding.rowShowNotification.rowSubtitle.visibility = View.VISIBLE
        binding.rowShowNotification.rowSwitch.isChecked = prefs.showNotification
        binding.rowShowNotification.rowSwitch.setOnCheckedChangeListener { _, checked ->
            prefs.showNotification = checked
        }

        binding.rowVibration.rowTitle.text = getString(R.string.titresim)
        binding.rowVibration.rowSubtitle.text = getString(R.string.titresim_aciklama)
        binding.rowVibration.rowSubtitle.visibility = View.VISIBLE
        binding.rowVibration.rowSwitch.isChecked = prefs.vibrationEnabled
        binding.rowVibration.rowSwitch.setOnCheckedChangeListener { _, checked ->
            prefs.vibrationEnabled = checked
        }

        binding.rowAutoLocation.rowTitle.text = getString(R.string.otomatik_konum)
        binding.rowAutoLocation.rowSubtitle.text = getString(R.string.otomatik_konum_aciklama)
        binding.rowAutoLocation.rowSubtitle.visibility = View.VISIBLE
        binding.rowAutoLocation.rowSwitch.isChecked = prefs.autoLocationEnabled
        binding.rowAutoLocation.rowSwitch.setOnCheckedChangeListener { _, checked ->
            prefs.autoLocationEnabled = checked
        }

        updateLastUpdateText()
        binding.rowUpdateNow.setOnClickListener {
            // A simple, explicit way to force a refresh: user re-opens Ana Sayfa
            // which always re-syncs; here we just refresh the timestamp label.
            updateLastUpdateText()
        }
    }

    private fun setupPrayerToggles() {
        binding.prayerTogglesContainer.removeAllViews()
        prayerLabels.forEach { (key, label) ->
            val rowBinding = RowSettingSwitchBinding.inflate(layoutInflater, binding.prayerTogglesContainer, false)
            rowBinding.rowTitle.text = label
            rowBinding.rowSwitch.isChecked = prefs.isPrayerEnabled(key)
            rowBinding.rowSwitch.setOnCheckedChangeListener { _, checked ->
                prefs.setPrayerEnabled(key, checked)
            }
            binding.prayerTogglesContainer.addView(rowBinding.root)
        }
    }

    private fun updateLastUpdateText() {
        val ts = prefs.lastUpdateTimestamp
        binding.textLastUpdate.text = if (ts == 0L) {
            "Henüz güncellenmedi"
        } else {
            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            "Son güncelleme: ${sdf.format(Date(ts))}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
