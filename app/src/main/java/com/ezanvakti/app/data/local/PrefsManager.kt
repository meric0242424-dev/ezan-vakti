package com.ezanvakti.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.ezanvakti.app.data.model.LocationInfo
import com.ezanvakti.app.data.model.PrayerDay
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Persists settings, last known location and cached prayer time data so the
 * app keeps working fully offline once a month has been downloaded.
 */
class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ezan_vakti_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // ---- Ezan (notification) toggles per prayer ----
    fun isPrayerEnabled(prayerKey: String): Boolean =
        prefs.getBoolean("ezan_$prayerKey", true)

    fun setPrayerEnabled(prayerKey: String, enabled: Boolean) {
        prefs.edit().putBoolean("ezan_$prayerKey", enabled).apply()
    }

    // ---- General notification settings ----
    var showNotification: Boolean
        get() = prefs.getBoolean("show_notification", true)
        set(value) = prefs.edit().putBoolean("show_notification", value).apply()

    var vibrationEnabled: Boolean
        get() = prefs.getBoolean("vibration_enabled", true)
        set(value) = prefs.edit().putBoolean("vibration_enabled", value).apply()

    var autoLocationEnabled: Boolean
        get() = prefs.getBoolean("auto_location", true)
        set(value) = prefs.edit().putBoolean("auto_location", value).apply()

    var lastUpdateTimestamp: Long
        get() = prefs.getLong("last_update", 0L)
        set(value) = prefs.edit().putLong("last_update", value).apply()

    // ---- Location ----
    fun saveLocation(location: LocationInfo) {
        prefs.edit().putString("location", gson.toJson(location)).apply()
    }

    fun getLocation(): LocationInfo? {
        val json = prefs.getString("location", null) ?: return null
        return gson.fromJson(json, LocationInfo::class.java)
    }

    // ---- Offline cache: whole month of prayer times keyed by "yyyy-MM" ----
    fun saveMonth(yearMonthKey: String, days: List<PrayerDay>) {
        prefs.edit().putString("month_$yearMonthKey", gson.toJson(days)).apply()
    }

    fun getMonth(yearMonthKey: String): List<PrayerDay>? {
        val json = prefs.getString("month_$yearMonthKey", null) ?: return null
        val type = object : TypeToken<List<PrayerDay>>() {}.type
        return gson.fromJson(json, type)
    }
}
