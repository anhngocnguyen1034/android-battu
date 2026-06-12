package com.anhnn.battu

import com.anhnn.battu.domain.models.BaziChart
import com.anhnn.battu.domain.models.ChartResult
import com.anhnn.battu.domain.models.Gender
import com.anhnn.battu.domain.repository.ChartRepository
import com.anhnn.battu.domain.usecases.CreateChartUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateChartUseCaseTest {

    private class FakeChartRepository : ChartRepository {
        var lastDatetime: String? = null
        var lastGender: Gender? = null

        override suspend fun createChart(
            datetimeStr: String,
            gender: Gender
        ): Result<ChartResult> {
            lastDatetime = datetimeStr
            lastGender = gender
            return Result.success(
                ChartResult(
                    chart = BaziChart(
                        gender = gender.apiValue,
                        pillars = listOf("庚午", "辛巳", "庚寅", "癸未"),
                        dayMaster = "庚",
                        tgGan = emptyList(), tgZhi = emptyList(), nayin = emptyList(),
                        wuxing = emptyMap(), wuxingStr = "",
                        dayun = emptyList(),
                        minggong = "", taiyuan = "", taixi = "", shengong = "",
                        dishi = emptyList(), xunkong = emptyList(),
                        xingchong = null, shensha = emptyList(),
                        shenshaDetail = emptyMap(),
                        rawChartJson = "{}"
                    ),
                    wuxingPower = null,
                    geju = null,
                    rawResponseJson = "{}"
                )
            )
        }
    }

    @Test
    fun `valid datetime calls repository`() = runTest {
        val repo = FakeChartRepository()
        val useCase = CreateChartUseCase(repo)

        val result = useCase("1990-05-15 14:30", Gender.MALE)

        assertTrue(result.isSuccess)
        assertEquals("1990-05-15 14:30", repo.lastDatetime)
        assertEquals(Gender.MALE, repo.lastGender)
    }

    @Test
    fun `invalid datetime fails without hitting repository`() = runTest {
        val repo = FakeChartRepository()
        val useCase = CreateChartUseCase(repo)

        val result = useCase("15/05/1990 14h30", Gender.FEMALE)

        assertTrue(result.isFailure)
        assertEquals(null, repo.lastDatetime)
    }

    @Test
    fun `gender maps to exact backend strings`() {
        assertEquals("乾造 (Male)", Gender.MALE.apiValue)
        assertEquals("坤造 (Female)", Gender.FEMALE.apiValue)
    }
}
