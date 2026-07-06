package com.ezanvakti.app.notification

import android.Manifest
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.ezanvakti.app.EzanVaktiApp
import com.ezanvakti.app.MainActivity
import com.ezanvakti.app.R
import com.ezanvakti.app.data.local.PrefsManager

class EzanAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
        const val EXTRA_PRAYER_TIME = "extra_prayer_time"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = PrefsManager(context)
        if (!prefs.showNotification) return

        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "Ezan"
        val prayerTime = intent.getStringExtra(EXTRA_PRAYER_TIME) ?: ""

        val openAppIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = android.app.PendingIntent.getActivity(
            context, 0, openAppIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, EzanVaktiApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("$prayerName Vakti Girdi")
            .setContentText("$prayerName ezanı okunma vakti: $prayerTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(prayerName.hashCode(), notification)
        }

        if (prefs.vibrationEnabled) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(800, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}
