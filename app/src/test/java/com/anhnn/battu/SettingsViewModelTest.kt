package com.anhnn.battu

import com.anhnn.battu.domain.models.ThemeMode
import com.anhnn.battu.domain.repository.SettingsRepository
import com.anhnn.battu.presentation.viewmodels.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private class FakeSettingsRepository : SettingsRepository {
        private val state = MutableStateFlow(ThemeMode.SYSTEM)
        override val themeMode: Flow<ThemeMode> = state
        override suspend fun setThemeMode(mode: ThemeMode) {
            state.value = mode
        }
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

    @Test
    fun `should default to SYSTEM theme`() = runTest(dispatcher.scheduler) {
        val viewModel = SettingsViewModel(FakeSettingsRepository())

        val collectJob = launch { viewModel.themeMode.collect {} }
        dispatcher.scheduler.runCurrent()

        assertEquals(ThemeMode.SYSTEM, viewModel.themeMode.value)
        collectJob.cancel()
    }

    @Test
    fun `should switch to DARK when toggled while light`() = runTest(dispatcher.scheduler) {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)

        val collectJob = launch { viewModel.themeMode.collect {} }
        dispatcher.scheduler.runCurrent()

        viewModel.onToggleTheme(isCurrentlyDark = false)
        dispatcher.scheduler.runCurrent()

        assertEquals(ThemeMode.DARK, viewModel.themeMode.value)
        collectJob.cancel()
    }

    @Test
    fun `should switch to LIGHT when toggled while dark`() = runTest(dispatcher.scheduler) {
        val repository = FakeSettingsRepository()
        repository.setThemeMode(ThemeMode.DARK)
        val viewModel = SettingsViewModel(repository)

        val collectJob = launch { viewModel.themeMode.collect {} }
        dispatcher.scheduler.runCurrent()

        viewModel.onToggleTheme(isCurrentlyDark = true)
        dispatcher.scheduler.runCurrent()

        assertEquals(ThemeMode.LIGHT, viewModel.themeMode.value)
        collectJob.cancel()
    }
}
