package com.ezanvakti.app.data.repository

import com.ezanvakti.app.data.local.PrefsManager
import com.ezanvakti.app.data.model.LocationInfo
import com.ezanvakti.app.data.model.PrayerDay
import com.ezanvakti.app.data.model.TimingsData
import com.ezanvakti.app.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PrayerTimesRepository(private val prefsManager: PrefsManager) {

    private val api = RetrofitClient.api

    /**
     * Returns the full current month (downloading it once, then always reading
     * from the offline cache afterwards) so the whole app works without internet
     * after the first sync, exactly like "İnternetsiz Çalışma" requires.
     */
    suspend fun getMonth(location: LocationInfo, year: Int, month: Int, forceRefresh: Boolean = false): Result<List<PrayerDay>> =
        withContext(Dispatchers.IO) {
            val key = "%d-%02d".format(year, month)
            if (!forceRefresh) {
                prefsManager.getMonth(key)?.let { return@withContext Result.success(it) }
            }
            try {
                val response = api.getMonthlyCalendar(year, month, location.latitude, location.longitude)
                val days = response.data.map { it.toPrayerDay() }
                prefsManager.saveMonth(key, days)
                prefsManager.lastUpdateTimestamp = System.currentTimeMillis()
                Result.success(days)
            } catch (e: Exception) {
                // fall back to whatever is cached, even if stale, so the app stays usable offline
                prefsManager.getMonth(key)?.let { return@withContext Result.success(it) }
                Result.failure(e)
            }
        }

    suspend fun getToday(location: LocationInfo): Result<PrayerDay> {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val monthResult = getMonth(location, year, month)
        return monthResult.mapCatching { days ->
            days.getOrNull(day - 1) ?: days.first()
        }
    }

    private fun TimingsData.toPrayerDay(): PrayerDay {
        return PrayerDay(
            dateReadable = date.readable,
            hijriDay = date.hijri.day,
            hijriMonth = date.hijri.month.en,
            hijriMonthNumber = date.hijri.month.number,
            hijriYear = date.hijri.year,
            gregorianDate = date.gregorian.date,
            imsak = timings.fajr.clean(),
            gunes = timings.sunrise.clean(),
            ogle = timings.dhuhr.clean(),
            ikindi = timings.asr.clean(),
            aksam = timings.maghrib.clean(),
            yatsi = timings.isha.clean()
        )
    }

    // Aladhan returns times like "04:03 (+03)" — strip any suffix after the time.
    private fun String.clean(): String = this.trim().substringBefore(" ")
}
