package com.anhnn.battu.domain.models

import androidx.compose.runtime.Immutable

/** Thập Nhị Trực của một ngày. */
@Immutable
data class TrucInfo(
    val ten: String,
    val tot: Boolean,
)

/** Sao Lục Nhâm của một ngày (hoàng đạo / hắc đạo). */
@Immutable
data class LucNham(
    val ten: String,
    val hoangDao: Boolean,
)

/** Thông tin lịch đầy đủ của một ngày dương lịch. */
@Immutable
data class CalendarDay(
    val ngayDuong: Int,
    val thangDuong: Int,
    val namDuong: Int,
    val thu: String,
    val jd: Int,
    val ngayAm: Int,
    val thangAm: Int,
    val namAm: Int,
    val thangNhuan: Boolean,
    val amLichText: String,
    val canChiNgay: String,
    val canChiThang: String,
    val canChiNam: String,
    val leDuongLich: String? = null,
    val leAmLich: String? = null,
    val truc: TrucInfo? = null,
    val gioHoangDao: List<String> = emptyList(),
    val lucNham: LucNham? = null,
)

/** Lịch của cả một tháng dương lịch. */
@Immutable
data class CalendarMonth(
    val thang: Int,
    val nam: Int,
    val canChiThang: String,
    val canChiNam: String,
    val soNgay: Int,
    val ngay: List<CalendarDay>,
)
