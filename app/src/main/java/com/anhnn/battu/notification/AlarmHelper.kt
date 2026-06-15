package com.anhnn.battu.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object AlarmHelper {

    /**
     * Lên lịch báo thức cho một sự kiện. Nếu [alarmEpoch] == 0 hoặc đã qua → không đặt.
     */
    fun schedule(context: Context, id: Long, tieuDe: String, ghiChu: String, alarmEpoch: Long) {
        if (alarmEpoch <= 0 || alarmEpoch <= System.currentTimeMillis()) return
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = buildPendingIntent(context, id, tieuDe, ghiChu)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            // Fallback: dùng inexact alarm khi chưa được cấp quyền báo thức chính xác
            am.set(AlarmManager.RTC_WAKEUP, alarmEpoch, pi)
            return
        }
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmEpoch, pi)
    }

    /** Hủy báo thức đã đặt cho sự kiện. */
    fun cancel(context: Context, id: Long, tieuDe: String, ghiChu: String) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(buildPendingIntent(context, id, tieuDe, ghiChu))
    }

    private fun buildPendingIntent(
        context: Context,
        id: Long,
        tieuDe: String,
        ghiChu: String,
    ): PendingIntent {
        val intent = Intent(context, SuKienReceiver::class.java).apply {
            putExtra(SuKienReceiver.EXTRA_ID, id)
            putExtra(SuKienReceiver.EXTRA_TIEU_DE, tieuDe)
            putExtra(SuKienReceiver.EXTRA_GHI_CHU, ghiChu)
        }
        return PendingIntent.getBroadcast(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
