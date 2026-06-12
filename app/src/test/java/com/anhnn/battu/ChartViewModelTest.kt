package com.anhnn.battu

import com.anhnn.battu.domain.models.BaziChart
import com.anhnn.battu.domain.models.ChartResult
import com.anhnn.battu.domain.models.Gender
import com.anhnn.battu.domain.models.SavedChart
import com.anhnn.battu.domain.repository.ChartRepository
import com.anhnn.battu.domain.repository.SavedChartRepository
import com.anhnn.battu.domain.usecases.CreateChartUseCase
import com.anhnn.battu.presentation.viewmodels.ChartState
import com.anhnn.battu.presentation.viewmodels.ChartViewModel
import com.anhnn.battu.presentation.viewmodels.SaveState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ChartViewModelTest {

    private fun fakeResult() = ChartResult(
        chart = BaziChart(
            gender = Gender.MALE.apiValue,
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
        rawResponseJson = """{"chart":{}}"""
    )

    private inner class FakeChartRepository : ChartRepository {
        override suspend fun createChart(
            datetimeStr: String,
            gender: Gender
        ): Result<ChartResult> = Result.success(fakeResult())
    }

    private class FakeSavedChartRepository(
        private val saveResult: Result<Unit> = Result.success(Unit)
    ) : SavedChartRepository {
        var savedDatetime: String? = null
        var savedGender: Gender? = null
        var savedRawJson: String? = null

        override val savedCharts: Flow<List<SavedChart>> = MutableStateFlow(emptyList())

        override suspend fun saveChart(
            datetimeStr: String,
            gender: Gender,
            result: ChartResult
        ): Result<Unit> {
            if (saveResult.isSuccess) {
                savedDatetime = datetimeStr
                savedGender = gender
                savedRawJson = result.rawResponseJson
            }
            return saveResult
        }

        override suspend fun getChart(id: String): Result<ChartResult> =
            Result.failure(NoSuchElementException(id))

        override suspend fun deleteChart(id: String): Result<Unit> = Result.success(Unit)
    }

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(savedRepo: SavedChartRepository) =
        ChartViewModel(CreateChartUseCase(FakeChartRepository()), savedRepo)

    @Test
    fun `should_save_chart_with_request_datetime_and_gender`() = runTest(dispatcher.scheduler) {
        val savedRepo = FakeSavedChartRepository()
        val vm = viewModel(savedRepo)

        // 1990-05-15 UTC midnight
        vm.onDateSelected(642_729_600_000L)
        vm.onTimeSelected(14, 30)
        vm.onGenderSelected(Gender.FEMALE)
        vm.onCreateChart()
        dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.uiState.value.chartState is ChartState.Success)

        vm.onSaveChart()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(SaveState.Saved, vm.uiState.value.saveState)
        assertEquals("1990-05-15 14:30", savedRepo.savedDatetime)
        assertEquals(Gender.FEMALE, savedRepo.savedGender)
        assertEquals("""{"chart":{}}""", savedRepo.savedRawJson)
    }

    @Test
    fun `should_not_save_when_no_chart_created`() = runTest(dispatcher.scheduler) {
        val savedRepo = FakeSavedChartRepository()
        val vm = viewModel(savedRepo)

        vm.onSaveChart()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(SaveState.NotSaved, vm.uiState.value.saveState)
        assertEquals(null, savedRepo.savedDatetime)
    }

    @Test
    fun `should_reset_save_state_when_save_fails`() = runTest(dispatcher.scheduler) {
        val savedRepo = FakeSavedChartRepository(Result.failure(IOException("disk full")))
        val vm = viewModel(savedRepo)

        vm.onDateSelected(642_729_600_000L)
        vm.onCreateChart()
        dispatcher.scheduler.advanceUntilIdle()

        vm.onSaveChart()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(SaveState.NotSaved, vm.uiState.value.saveState)
    }

    @Test
    fun `should_reset_save_state_when_new_chart_created`() = runTest(dispatcher.scheduler) {
        val savedRepo = FakeSavedChartRepository()
        val vm = viewModel(savedRepo)

        vm.onDateSelected(642_729_600_000L)
        vm.onCreateChart()
        dispatcher.scheduler.advanceUntilIdle()
        vm.onSaveChart()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(SaveState.Saved, vm.uiState.value.saveState)

        vm.onCreateChart()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(SaveState.NotSaved, vm.uiState.value.saveState)
    }
}
