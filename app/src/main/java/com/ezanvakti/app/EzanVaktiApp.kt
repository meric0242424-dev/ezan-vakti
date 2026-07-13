package com.ezanvakti.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ezanvakti.app.notification.DailyScheduleWorker
import java.util.concurrent.TimeUnit

class EzanVaktiApp : Application() {

    companion object {
        const val CHANNEL_ID = "ezan_vakti_channel_v2"
        private const val DAILY_WORK_NAME = "daily_ezan_schedule"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        scheduleDailyBackgroundWork()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = Uri.parse(
                "${ContentResolver.SCHEME_ANDROID_RESOURCE}://$packageName/${R.raw.ezan}"
            )
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Ezan Vakti Bildirimleri",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Namaz vakti girdiğinde gösterilen bildirimler"
                enableVibration(true)
                setSound(soundUri, audioAttributes)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun scheduleDailyBackgroundWork() {
        val periodicRequest = PeriodicWorkRequestBuilder<DailyScheduleWorker>(6, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DAILY_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )

        val immediateRequest = OneTimeWorkRequestBuilder<DailyScheduleWorker>().build()
        WorkManager.getInstance(this).enqueue(immediateRequest)
    }
}
