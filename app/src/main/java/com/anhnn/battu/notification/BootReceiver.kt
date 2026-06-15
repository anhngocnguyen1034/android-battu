package com.anhnn.battu.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.anhnn.battu.data.local.BatTuDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Sau khi điện thoại khởi động lại, AlarmManager bị xóa.
 * Receiver này lấy tất cả sự kiện còn hiệu lực và đặt lại báo thức.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            val now = System.currentTimeMillis()
            BatTuDatabase.getInstance(appContext).suKienDao().getAll().first()
                .filter { it.alarmEpoch > now }
                .forEach {
                    AlarmHelper.schedule(appContext, it.id, it.tieuDe, it.ghiChu, it.alarmEpoch)
                }
        }
    }
}
