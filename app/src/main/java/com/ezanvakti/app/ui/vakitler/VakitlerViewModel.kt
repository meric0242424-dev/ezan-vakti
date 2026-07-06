package com.ezanvakti.app.ui.vakitler

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

data class VakitlerUiState(
    val day: PrayerDay? = null,
    val loading: Boolean = true,
    val error: String? = null
)

class VakitlerViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsManager = PrefsManager(application)
    private val repository = PrayerTimesRepository(prefsManager)

    private var dayOffset = 0

    private val _uiState = MutableLiveData(VakitlerUiState())
    val uiState: LiveData<VakitlerUiState> = _uiState

    fun loadOffset(offsetDelta: Int) {
        dayOffset += offsetDelta
        load()
    }

    fun load() {
        val location = prefsManager.getLocation()
        if (location == null) {
            _uiState.value = VakitlerUiState(loading = false, error = "Önce Ana Sayfa'da konum belirleyin")
            return
        }
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, dayOffset) }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)

        _uiState.value = VakitlerUiState(loading = true)
        viewModelScope.launch {
            repository.getMonth(location, year, month).fold(
                onSuccess = { days ->
                    val day = days.getOrNull(dayOfMonth - 1)
                    _uiState.value = VakitlerUiState(day = day, loading = false)
                },
                onFailure = {
                    _uiState.value = VakitlerUiState(loading = false, error = it.message ?: "Hata")
                }
            )
        }
    }
}
