package com.pb123.hub.guides.chrono

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

/** Planifie / annule l'alarme de fin de séchage qui déclenche la notification. */
object ChronoScheduler {
    const val CHANNEL_ID = "drying_timer"
    const val NOTIF_ID = 4201
    const val EXTRA_LABEL = "extra_label"
    private const val REQUEST_CODE = 4202

    fun schedule(context: Context, endTimeMillis: Long, label: String) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = pendingIntent(context, label)
        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || am.canScheduleExactAlarms()
        try {
            if (canExact) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endTimeMillis, pi)
            } else {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endTimeMillis, pi)
            }
        } catch (e: SecurityException) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endTimeMillis, pi)
        }
    }

    fun cancel(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pendingIntent(context, ""))
    }

    private fun pendingIntent(context: Context, label: String): PendingIntent {
        val intent = Intent(context, DryingAlarmReceiver::class.java).apply {
            putExtra(EXTRA_LABEL, label)
        }
        var flags = PendingIntent.FLAG_UPDATE_CURRENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = flags or PendingIntent.FLAG_IMMUTABLE
        }
        return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags)
    }
}
