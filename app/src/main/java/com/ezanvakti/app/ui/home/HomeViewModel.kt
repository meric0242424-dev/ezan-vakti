package com.ezanvakti.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezanvakti.app.data.local.PrefsManager
import com.ezanvakti.app.data.model.LocationInfo
import com.ezanvakti.app.data.model.PrayerDay
import com.ezanvakti.app.data.repository.PrayerTimesRepository
import com.ezanvakti.app.location.LocationHelper
import com.ezanvakti.app.notification.NotificationScheduler
import kotlinx.coroutines.launch

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val location: LocationInfo, val day: PrayerDay) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsManager = PrefsManager(application)
    private val repository = PrayerTimesRepository(prefsManager)
    private val locationHelper = LocationHelper(application)

    private val _state = MutableLiveData<HomeState>(HomeState.Loading)
    val state: LiveData<HomeState> = _state

    fun load(forceLocationRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.value = HomeState.Loading
            val location = resolveLocation(forceLocationRefresh)
            if (location == null) {
                _state.value = HomeState.Error("Konum alınamadı")
                return@launch
            }
            repository.getToday(location).fold(
                onSuccess = { day ->
                    _state.value = HomeState.Success(location, day)
                    NotificationScheduler.scheduleForToday(getApplication(), day)
                },
                onFailure = {
                    _state.value = HomeState.Error(it.message ?: "Bilinmeyen hata")
                }
            )
        }
    }

    private suspend fun resolveLocation(forceRefresh: Boolean): LocationInfo? {
        val cached = prefsManager.getLocation()
        if (cached != null && !forceRefresh) return cached
        if (!prefsManager.autoLocationEnabled && cached != null) return cached

        val fresh = locationHelper.getCurrentLocation()
        return if (fresh != null) {
            prefsManager.saveLocation(fresh)
            fresh
        } else {
            cached // fall back to last known location so app still works offline
        }
    }
}
