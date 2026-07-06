package com.ezanvakti.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * On boot we can't fetch fresh network data reliably right away, so we simply
 * rely on the app being opened again (MainActivity re-schedules on launch) or,
 * if a cached "today" exists, we could re-derive it here in a future version.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Rescheduling happens automatically the next time MainActivity is opened.
        }
    }
}
