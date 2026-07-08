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

        binding.rowEzanSesi.setOnClickListener {
            openChannelSoundSettings()
        }

        binding.rowTestEzan.setOnClickListener {
            testEzanSound()
        }

        updateLastUpdateText()
        binding.rowUpdateNow.setOnClickListener {
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

    private var testMediaPlayer: android.media.MediaPlayer? = null

    private fun testEzanSound() {
        val context = requireContext()

        try {
            testMediaPlayer?.release()
            testMediaPlayer = android.media.MediaPlayer.create(context, R.raw.ezan)
            testMediaPlayer?.setOnCompletionListener { it.release() }
            testMediaPlayer?.start()
        } catch (e: Exception) {
            android.widget.Toast.makeText(context, "Ses dosyası çalınamadı: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
        }

        try {
            val builder = androidx.core.app.NotificationCompat.Builder(context, com.ezanvakti.app.EzanVaktiApp.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Test Bildirimi")
                .setContentText("Bu bir test bildirimidir — ezan sesini duyman gerekiyor")
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                androidx.core.app.NotificationManagerCompat.from(context).notify(9999, builder.build())
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun openChannelSoundSettings() {
        val context = requireContext()
        try {
            val intent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                android.content.Intent(android.provider.Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                    putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                    putExtra(android.provider.Settings.EXTRA_CHANNEL_ID, com.ezanvakti.app.EzanVaktiApp.CHANNEL_ID)
                }
            } else {
                android.content.Intent(
                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    android.net.Uri.parse("package:${context.packageName}")
                )
            }
            startActivity(intent)
        } catch (e: Exception) {
            android.widget.Toast.makeText(
                context,
                "Ses ayarı ekranı açılamadı: ${e.message}",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        testMediaPlayer?.release()
        testMediaPlayer = null
        _binding = null
    }
}
