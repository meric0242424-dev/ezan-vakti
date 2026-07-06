package com.ezanvakti.app.ui.aylik

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezanvakti.app.data.local.PrefsManager
import com.ezanvakti.app.data.model.PrayerDay
import com.ezanvakti.app.data.repository.PrayerTimesRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

data class AylikUiState(
    val days: List<PrayerDay> = emptyList(),
    val monthTitle: String = "",
    val loading: Boolean = true,
    val error: String? = null
)

class AylikViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsManager = PrefsManager(application)
    private val repository = PrayerTimesRepository(prefsManager)

    private var monthOffset = 0

    private val _uiState = MutableLiveData(AylikUiState())
    val uiState: LiveData<AylikUiState> = _uiState

    private val monthNames = listOf(
        "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
        "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
    )

    fun changeMonth(delta: Int) {
        monthOffset += delta
        load()
    }

    fun load() {
        val location = prefsManager.getLocation()
        if (location == null) {
            _uiState.value = AylikUiState(loading = false, error = "Önce Ana Sayfa'da konum belirleyin")
            return
        }
        val cal = Calendar.getInstance().apply { add(Calendar.MONTH, monthOffset) }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val title = "${monthNames[month - 1]} $year"

        _uiState.value = AylikUiState(loading = true, monthTitle = title)
        viewModelScope.launch {
            repository.getMonth(location, year, month).fold(
                onSuccess = { days ->
                    _uiState.value = AylikUiState(days = days, monthTitle = title, loading = false)
                },
                onFailure = {
                    _uiState.value = AylikUiState(loading = false, monthTitle = title, error = it.message ?: "Hata")
                }
            )
        }
    }
}
