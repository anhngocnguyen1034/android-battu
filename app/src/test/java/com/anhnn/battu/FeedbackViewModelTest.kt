package com.anhnn.battu

import com.anhnn.battu.domain.repository.FeedbackRepository
import com.anhnn.battu.presentation.viewmodels.FeedbackState
import com.anhnn.battu.presentation.viewmodels.FeedbackViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class FeedbackViewModelTest {

    private class FakeFeedbackRepository(
        var result: Result<Unit> = Result.success(Unit)
    ) : FeedbackRepository {
        var lastRating: Int? = null
        var lastMessage: String? = null

        override suspend fun sendFeedback(rating: Int?, message: String): Result<Unit> {
            lastRating = rating
            lastMessage = message
            return result
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
    fun `should not send when message is blank`() = runTest(dispatcher.scheduler) {
        val repository = FakeFeedbackRepository()
        val viewModel = FeedbackViewModel(repository)

        assertFalse(viewModel.uiState.value.canSend)
        viewModel.onSend()
        dispatcher.scheduler.runCurrent()

        assertNull(repository.lastMessage)
        assertEquals(FeedbackState.Idle, viewModel.uiState.value.sendState)
    }

    @Test
    fun `should go Sending then Sent with rating on success`() = runTest(dispatcher.scheduler) {
        val repository = FakeFeedbackRepository()
        val viewModel = FeedbackViewModel(repository)

        viewModel.onRatingSelected(5)
        viewModel.onMessageChanged("App rất hay")
        viewModel.onSend()
        assertEquals(FeedbackState.Sending, viewModel.uiState.value.sendState)

        dispatcher.scheduler.runCurrent()

        assertEquals(FeedbackState.Sent(5), viewModel.uiState.value.sendState)
        assertEquals(5, repository.lastRating)
        assertEquals("App rất hay", repository.lastMessage)
    }

    @Test
    fun `should show error when backend fails`() = runTest(dispatcher.scheduler) {
        val repository = FakeFeedbackRepository(Result.failure(IOException("offline")))
        val viewModel = FeedbackViewModel(repository)

        viewModel.onMessageChanged("góp ý")
        viewModel.onSend()
        dispatcher.scheduler.runCurrent()

        assertTrue(viewModel.uiState.value.sendState is FeedbackState.Error)
    }

    @Test
    fun `should clear rating when same star tapped twice`() {
        val viewModel = FeedbackViewModel(FakeFeedbackRepository())

        viewModel.onRatingSelected(3)
        assertEquals(3, viewModel.uiState.value.rating)
        viewModel.onRatingSelected(3)
        assertNull(viewModel.uiState.value.rating)
    }

    @Test
    fun `should reset state on dismiss`() {
        val viewModel = FeedbackViewModel(FakeFeedbackRepository())

        viewModel.onRatingSelected(2)
        viewModel.onMessageChanged("abc")
        viewModel.onDismissed()

        assertNull(viewModel.uiState.value.rating)
        assertEquals("", viewModel.uiState.value.message)
        assertEquals(FeedbackState.Idle, viewModel.uiState.value.sendState)
    }
}
