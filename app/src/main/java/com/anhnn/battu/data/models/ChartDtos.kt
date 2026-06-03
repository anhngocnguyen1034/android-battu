package com.anhnn.battu.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTOs mirroring FOR-BAZI backend schemas (backend/schemas/chart.py, common.py).
 */

@Serializable
data class ChartRequestDto(
    // Format "YYYY-MM-DD HH:MM" (or with seconds), max 25 chars
    @SerialName("datetime_str") val datetimeStr: String,
    // Must be exactly "乾造 (Male)" or "坤造 (Female)"
    val gender: String
)

@Serializable
data class ChartResponseDto(
    val chart: BaziChartDto,
    @SerialName("wuxing_power") val wuxingPower: WuxingPowerDto? = null,
    val geju: GejuDto? = null
)

@Serializable
data class BaziChartDto(
    val gender: String = "",
    val pillars: List<String> = emptyList(),
    @SerialName("tg_gan") val tgGan: List<String> = emptyList(),
    @SerialName("tg_zhi") val tgZhi: List<String> = emptyList(),
    val nayin: List<String> = emptyList(),
    val shensha: List<String> = emptyList(),
    @SerialName("shensha_detail") val shenshaDetail: Map<String, List<String>> = emptyMap(),
    val wuxing: Map<String, Int> = emptyMap(),
    val dayun: List<DayunItemDto> = emptyList(),
    val minggong: String = "",
    val taiyuan: String = "",
    val taixi: String = "",
    val shengong: String = "",
    val dishi: List<String> = emptyList(),
    val xunkong: List<String> = emptyList(),
    val xingchong: XingChongDto? = null,
    @SerialName("wuxing_str") val wuxingStr: String = "",
    @SerialName("day_master") val dayMaster: String = ""
)

@Serializable
data class DayunItemDto(
    @SerialName("start_age") val startAge: Int = 0,
    @SerialName("start_year") val startYear: Int = 0,
    val ganzhi: String = ""
)

@Serializable
data class XingChongDto(
    @SerialName("冲") val chong: List<String> = emptyList(),
    @SerialName("合") val he: List<String> = emptyList(),
    @SerialName("刑") val xing: List<String> = emptyList(),
    @SerialName("害") val hai: List<String> = emptyList(),
    @SerialName("破") val po: List<String> = emptyList(),
    @SerialName("三合") val sanHe: List<String> = emptyList(),
    @SerialName("三会") val sanHui: List<String> = emptyList(),
    @SerialName("半三合") val banSanHe: List<String> = emptyList()
)

@Serializable
data class WuxingPowerDto(
    val power: Map<String, Double> = emptyMap(),
    val strong: List<String> = emptyList(),
    val weak: List<String> = emptyList(),
    val balanced: Boolean = false,
    val context: String = ""
)

@Serializable
data class GejuDto(
    @SerialName("格局类型") val gejuType: String = "",
    @SerialName("格局名称") val gejuName: String = "",
    @SerialName("月令") val monthZhi: String = "",
    @SerialName("月令主气") val monthMainQi: String = "",
    @SerialName("月干透干") val isTougan: Boolean = false,
    @SerialName("透干位置") val touganPosition: String = "",
    @SerialName("日主强弱") val strength: String = "",
    @SerialName("日主力量占比") val dmRatio: Double = 0.0,
    val context: String = ""
)
