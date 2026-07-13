package com.ezanvakti.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ezanvakti.app.data.local.PrefsManager
import com.ezanvakti.app.data.model.PrayerDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object NotificationScheduler {

    private val prayerKeys = listOf("imsak", "gunes", "ogle", "ikindi", "aksam", "yatsi")

    fun scheduleForToday(context: Context, day: PrayerDay) = scheduleForOffset(context, day, 0)

    fun scheduleForOffset(context: Context, day: PrayerDay, dayOffset: Int) {
        val prefs = PrefsManager(context)
        val times = listOf(day.imsak, day.gunes, day.ogle, day.ikindi, day.aksam, day.yatsi)
        val labels = listOf("İmsak", "Güneş", "Öğle", "İkindi", "Akşam", "Yatsı")

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        prayerKeys.forEachIndexed { index, key ->
            if (!prefs.isPrayerEnabled(key)) return@forEachIndexed
            if (key == "gunes") return@forEachIndexed

            val timeMillis = parseTimeForOffset(times[index], dayOffset) ?: return@forEachIndexed
            if (timeMillis <= System.currentTimeMillis()) return@forEachIndexed

            val intent = Intent(context, EzanAlarmReceiver::class.java).apply {
                putExtra(EzanAlarmReceiver.EXTRA_PRAYER_NAME, labels[index])
                putExtra(EzanAlarmReceiver.EXTRA_PRAYER_TIME, times[index])
            }
            val requestCode = "$key-$dayOffset".hashCode()
            val pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent)
            }
        }
    }

    private fun parseTimeForOffset(hhmm: String, dayOffset: Int): Long? {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val parsed = sdf.parse(hhmm) ?: return null
            val parsedCal = Calendar.getInstance().apply { time = parsed }
            val target = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, dayOffset)
                set(Calendar.HOUR_OF_DAY, parsedCal.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, parsedCal.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            target.timeInMillis
        } catch (e: Exception) {
            null
        }
    }
}
