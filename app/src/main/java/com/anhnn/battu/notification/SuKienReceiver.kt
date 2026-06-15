package com.anhnn.battu.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.anhnn.battu.MainActivity
import com.anhnn.battu.R

class SuKienReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val tieuDe = intent.getStringExtra(EXTRA_TIEU_DE)
            ?: context.getString(R.string.notif_event_default_title)
        val ghiChu = intent.getStringExtra(EXTRA_GHI_CHU) ?: ""
        val id = intent.getLongExtra(EXTRA_ID, 0L).toInt()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureChannel(context, nm)

        val tap = PendingIntent.getActivity(
            context, id,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val body = ghiChu.ifBlank { context.getString(R.string.notif_event_default_text) }
        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(tieuDe)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(tap)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        nm.notify(id, notif)
    }

    private fun ensureChannel(context: Context, nm: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            nm.getNotificationChannel(CHANNEL_ID) == null
        ) {
            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notif_channel_name),
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply { description = context.getString(R.string.notif_channel_desc) }
            )
        }
    }

    companion object {
        const val CHANNEL_ID = "su_kien_channel"
        const val EXTRA_ID = "extra_id"
        const val EXTRA_TIEU_DE = "extra_tieu_de"
        const val EXTRA_GHI_CHU = "extra_ghi_chu"
    }
}
