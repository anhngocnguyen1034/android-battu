package com.anhnn.battu.data.models

import com.anhnn.battu.domain.models.CalendarDay
import com.anhnn.battu.domain.models.CalendarMonth
import com.anhnn.battu.domain.models.LucNham
import com.anhnn.battu.domain.models.TrucInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrucInfoDto(
    val ten: String,
    val tot: Boolean,
)

@Serializable
data class LucNhamDto(
    val ten: String,
    @SerialName("hoang_dao") val hoangDao: Boolean,
)

@Serializable
data class NgayInfoDto(
    @SerialName("ngay_duong") val ngayDuong: Int,
    @SerialName("thang_duong") val thangDuong: Int,
    @SerialName("nam_duong") val namDuong: Int,
    val thu: String,
    val jd: Int,
    @SerialName("ngay_am") val ngayAm: Int,
    @SerialName("thang_am") val thangAm: Int,
    @SerialName("nam_am") val namAm: Int,
    @SerialName("thang_nhuan") val thangNhuan: Boolean,
    @SerialName("am_lich_text") val amLichText: String,
    @SerialName("can_chi_ngay") val canChiNgay: String,
    @SerialName("can_chi_thang") val canChiThang: String,
    @SerialName("can_chi_nam") val canChiNam: String,
    @SerialName("le_duong_lich") val leDuongLich: String? = null,
    @SerialName("le_am_lich") val leAmLich: String? = null,
    val truc: TrucInfoDto? = null,
    @SerialName("gio_hoang_dao") val gioHoangDao: List<String>? = null,
    @SerialName("luc_nham") val lucNham: LucNhamDto? = null,
)

@Serializable
data class ThangLichDto(
    val thang: Int,
    val nam: Int,
    @SerialName("can_chi_thang") val canChiThang: String,
    @SerialName("can_chi_nam") val canChiNam: String,
    @SerialName("so_ngay") val soNgay: Int,
    val ngay: List<NgayInfoDto>,
)

// ── Mappers ───────────────────────────────────────────────────────────────

fun TrucInfoDto.toDomain() = TrucInfo(ten = ten, tot = tot)

fun LucNhamDto.toDomain() = LucNham(ten = ten, hoangDao = hoangDao)

fun NgayInfoDto.toDomain() = CalendarDay(
    ngayDuong = ngayDuong,
    thangDuong = thangDuong,
    namDuong = namDuong,
    thu = thu,
    jd = jd,
    ngayAm = ngayAm,
    thangAm = thangAm,
    namAm = namAm,
    thangNhuan = thangNhuan,
    amLichText = amLichText,
    canChiNgay = canChiNgay,
    canChiThang = canChiThang,
    canChiNam = canChiNam,
    leDuongLich = leDuongLich,
    leAmLich = leAmLich,
    truc = truc?.toDomain(),
    gioHoangDao = gioHoangDao.orEmpty(),
    lucNham = lucNham?.toDomain(),
)

fun ThangLichDto.toDomain() = CalendarMonth(
    thang = thang,
    nam = nam,
    canChiThang = canChiThang,
    canChiNam = canChiNam,
    soNgay = soNgay,
    ngay = ngay.map { it.toDomain() },
)
