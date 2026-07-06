package com.ezanvakti.app.util

import com.ezanvakti.app.data.model.PrayerDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object PrayerTimeUtils {

    data class NextPrayer(val name: String, val timeMillis: Long, val timeText: String, val index: Int)

    /**
     * Finds the next upcoming prayer for today. If every prayer for today has
     * already passed, it falls back to tomorrow's İmsak (approximated using
     * today's İmsak time, since the shift day-to-day is only a minute or two).
     */
    fun findNextPrayer(day: PrayerDay): NextPrayer {
        val ordered = listOf(
            "İmsak" to day.imsak,
            "Güneş" to day.gunes,
            "Öğle" to day.ogle,
            "İkindi" to day.ikindi,
            "Akşam" to day.aksam,
            "Yatsı" to day.yatsi
        )
        val now = System.currentTimeMillis()
        ordered.forEachIndexed { index, (name, time) ->
            val millis = timeTodayMillis(time)
            if (millis != null && millis > now) {
                return NextPrayer(name, millis, time, index)
            }
        }
        // all passed -> tomorrow's İmsak
        val tomorrowImsak = timeTodayMillis(day.imsak)?.plus(24L * 60 * 60 * 1000)
            ?: (now + 60 * 60 * 1000)
        return NextPrayer("İmsak", tomorrowImsak, day.imsak, 0)
    }

    fun activeIndex(day: PrayerDay): Int {
        val next = findNextPrayer(day)
        return if (next.index == 0 && timeTodayMillis(day.yatsi)?.let { it < System.currentTimeMillis() } == true) {
            5 // Yatsı still "active" overnight until tomorrow's İmsak
        } else {
            (next.index - 1).coerceAtLeast(0)
        }
    }

    fun timeTodayMillis(hhmm: String): Long? {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val parsed = sdf.parse(hhmm) ?: return null
            val parsedCal = Calendar.getInstance().apply { time = parsed }
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, parsedCal.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, parsedCal.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            today.timeInMillis
        } catch (e: Exception) {
            null
        }
    }

    fun formatCountdown(millisRemaining: Long): String {
        val totalSeconds = (millisRemaining / 1000).coerceAtLeast(0)
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
    }
}
