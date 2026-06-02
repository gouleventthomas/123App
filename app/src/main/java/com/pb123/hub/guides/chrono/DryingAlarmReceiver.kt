package com.pb123.hub.guides.chrono

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pb123.hub.MainActivity
import com.pb123.hub.R

/** Reçoit l'alarme de fin de séchage et publie la notification "véhicule roulable". */
class DryingAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val label = intent.getStringExtra(ChronoScheduler.EXTRA_LABEL).orEmpty()
        val text = if (label.isBlank()) {
            "Le collage est sec, le véhicule peut rouler en toute sécurité."
        } else {
            "$label — le collage est sec, le véhicule peut rouler."
        }

        var openFlags = PendingIntent.FLAG_UPDATE_CURRENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            openFlags = openFlags or PendingIntent.FLAG_IMMUTABLE
        }
        val openApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            openFlags,
        )

        val notification = NotificationCompat.Builder(context, ChronoScheduler.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_timer)
            .setContentTitle("Véhicule roulable ✅")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(openApp)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(ChronoScheduler.NOTIF_ID, notification)
        } catch (e: SecurityException) {
            // Permission notifications refusée : on ignore silencieusement.
        }
    }
}
