package com.anhnn.battu.domain.models

import androidx.compose.runtime.Immutable

/** Gender mapped to the exact strings the backend requires. */
enum class Gender(val apiValue: String) {
    MALE("乾造 (Male)"),
    FEMALE("坤造 (Female)")
}

@Immutable
data class Dayun(
    val startAge: Int,
    val startYear: Int,
    val ganzhi: String
)

@Immutable
data class XingChong(
    val chong: List<String>,
    val he: List<String>,
    val xing: List<String>,
    val hai: List<String>,
    val po: List<String>,
    val sanHe: List<String>,
    val sanHui: List<String>,
    val banSanHe: List<String>
)

@Immutable
data class BaziChart(
    val gender: String,
    /** Tứ trụ [năm, tháng, ngày, giờ] */
    val pillars: List<String>,
    /** Nhật chủ */
    val dayMaster: String,
    /** Thập thần thiên can */
    val tgGan: List<String>,
    /** Thập thần tàng can địa chi */
    val tgZhi: List<String>,
    /** Nạp âm */
    val nayin: List<String>,
    /** Đếm ngũ hành */
    val wuxing: Map<String, Int>,
    val wuxingStr: String,
    /** Đại vận */
    val dayun: List<Dayun>,
    val minggong: String,
    val taiyuan: String,
    val taixi: String,
    val shengong: String,
    /** Thập nhị trường sinh */
    val dishi: List<String>,
    /** Tuần không */
    val xunkong: List<String>,
    /** Hình-Xung-Hợp-Hại */
    val xingchong: XingChong?,
    /** Thần sát */
    val shensha: List<String>,
    /** Thần sát theo từng trụ (key = index trụ "0".."3") */
    val shenshaDetail: Map<Int, List<String>>,
    /**
     * Raw JSON of the `chart` block, kept verbatim to be sent back as
     * `chart_data` in POST /api/v1/chat/stream (AI chat feature).
     */
    val rawChartJson: String
)

@Immutable
data class WuxingPower(
    val power: Map<String, Double>,
    val strong: List<String>,
    val weak: List<String>,
    val balanced: Boolean,
    val context: String
)

@Immutable
data class Geju(
    val type: String,
    val name: String,
    val monthZhi: String,
    val strength: String,
    val dmRatio: Double,
    val context: String
)

@Immutable
data class ChartResult(
    val chart: BaziChart,
    val wuxingPower: WuxingPower?,
    val geju: Geju?,
    /**
     * Full API response JSON kept verbatim so the chart can be persisted
     * locally and rebuilt offline (saved-charts feature).
     */
    val rawResponseJson: String
)
