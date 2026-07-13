package com.ezanvakti.app.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ezanvakti.app.data.local.PrefsManager
import com.ezanvakti.app.data.repository.PrayerTimesRepository

class DailyScheduleWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val prefs = PrefsManager(applicationContext)
        val location = prefs.getLocation() ?: return Result.success()

        val repository = PrayerTimesRepository(prefs)

        repository.getForOffset(location, 0).onSuccess { today ->
            NotificationScheduler.scheduleForOffset(applicationContext, today, 0)
        }
        repository.getForOffset(location, 1).onSuccess { tomorrow ->
            NotificationScheduler.scheduleForOffset(applicationContext, tomorrow, 1)
        }

        return Result.success()
    }
}
