package com.anhnn.battu.domain.models

import androidx.compose.runtime.Immutable

/**
 * Sự kiện cá nhân do người dùng tạo trên lịch.
 *
 * [ngayDuong]/[thangDuong]/[namDuong] là ngày dương lịch của sự kiện.
 * [alarmEpoch] = unix-millis lúc cần bắn thông báo (0 = không nhắc).
 */
@Immutable
data class CalendarEvent(
    val id: Long = 0,
    val tieuDe: String,
    val ghiChu: String = "",
    val ngayDuong: Int,
    val thangDuong: Int,
    val namDuong: Int,
    val alarmEpoch: Long = 0L,
)
