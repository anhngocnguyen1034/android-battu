package com.anhnn.battu.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhnn.battu.domain.repository.FeedbackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Send state of the feedback dialog. */
sealed interface FeedbackState {
    data object Idle : FeedbackState
    data object Sending : FeedbackState
    /** Sent OK — [rating] kept so the caller can chain an in-app review ask. */
    data class Sent(val rating: Int?) : FeedbackState
    data class Error(val message: String) : FeedbackState
}

data class FeedbackUiState(
    val rating: Int? = null,
    val message: String = "",
    val sendState: FeedbackState = FeedbackState.Idle
) {
    val canSend: Boolean
        get() = message.isNotBlank() && sendState != FeedbackState.Sending
}

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val feedbackRepository: FeedbackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState: StateFlow<FeedbackUiState> = _uiState.asStateFlow()

    fun onRatingSelected(rating: Int) = _uiState.update {
        // Tapping the same star again clears the rating
        it.copy(rating = if (it.rating == rating) null else rating)
    }

    fun onMessageChanged(message: String) = _uiState.update { it.copy(message = message) }

    fun onSend() {
        val state = _uiState.value
        if (!state.canSend) return

        _uiState.update { it.copy(sendState = FeedbackState.Sending) }
        viewModelScope.launch {
            feedbackRepository.sendFeedback(state.rating, state.message.trim())
                .onSuccess {
                    _uiState.update { it.copy(sendState = FeedbackState.Sent(state.rating)) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(sendState = FeedbackState.Error(error.message ?: "Unknown error"))
                    }
                }
        }
    }

    /** Reset everything when the dialog is dismissed. */
    fun onDismissed() {
        _uiState.value = FeedbackUiState()
    }
}
