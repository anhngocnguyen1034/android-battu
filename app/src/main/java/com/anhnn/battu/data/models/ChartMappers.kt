package com.anhnn.battu.data.models

import com.anhnn.battu.domain.models.BaziChart
import com.anhnn.battu.domain.models.ChartResult
import com.anhnn.battu.domain.models.Dayun
import com.anhnn.battu.domain.models.Geju
import com.anhnn.battu.domain.models.WuxingPower
import com.anhnn.battu.domain.models.XingChong

fun ChartResponseDto.toDomain(rawChartJson: String, rawResponseJson: String): ChartResult =
    ChartResult(
        chart = chart.toDomain(rawChartJson),
        wuxingPower = wuxingPower?.toDomain(),
        geju = geju?.toDomain(),
        rawResponseJson = rawResponseJson
    )

fun BaziChartDto.toDomain(rawChartJson: String): BaziChart = BaziChart(
    gender = gender,
    pillars = pillars,
    dayMaster = dayMaster,
    tgGan = tgGan,
    tgZhi = tgZhi,
    nayin = nayin,
    wuxing = wuxing,
    wuxingStr = wuxingStr,
    dayun = dayun.map { Dayun(it.startAge, it.startYear, it.ganzhi) },
    minggong = minggong,
    taiyuan = taiyuan,
    taixi = taixi,
    shengong = shengong,
    dishi = dishi,
    xunkong = xunkong,
    xingchong = xingchong?.toDomain(),
    shensha = shensha,
    shenshaDetail = shenshaDetail.mapNotNull { (k, v) -> k.toIntOrNull()?.let { it to v } }.toMap(),
    rawChartJson = rawChartJson
)

fun XingChongDto.toDomain(): XingChong = XingChong(
    chong = chong,
    he = he,
    xing = xing,
    hai = hai,
    po = po,
    sanHe = sanHe,
    sanHui = sanHui,
    banSanHe = banSanHe
)

fun WuxingPowerDto.toDomain(): WuxingPower = WuxingPower(
    power = power,
    strong = strong,
    weak = weak,
    balanced = balanced,
    context = context
)

fun GejuDto.toDomain(): Geju = Geju(
    type = gejuType,
    name = gejuName,
    monthZhi = monthZhi,
    strength = strength,
    dmRatio = dmRatio,
    context = context
)
