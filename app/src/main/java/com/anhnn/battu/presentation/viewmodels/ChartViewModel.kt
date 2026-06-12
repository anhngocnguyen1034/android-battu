package com.anhnn.battu.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhnn.battu.domain.models.ChartResult
import com.anhnn.battu.domain.models.Gender
import com.anhnn.battu.domain.repository.SavedChartRepository
import com.anhnn.battu.domain.usecases.CreateChartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

/** Result area of the screen, modeled as a sealed state. */
sealed interface ChartState {
    data object Idle : ChartState
    data object Loading : ChartState
    data class Success(val result: ChartResult) : ChartState
    data class Error(val message: String) : ChartState
}

/** Save-to-device status of the currently displayed chart. */
enum class SaveState { NotSaved, Saving, Saved }

/** Single UI State object for the Chart screen (UDF). */
data class ChartUiState(
    val birthDateMillis: Long? = null,
    val hour: Int = 12,
    val minute: Int = 0,
    val gender: Gender = Gender.MALE,
    val chartState: ChartState = ChartState.Idle,
    val saveState: SaveState = SaveState.NotSaved
) {
    val canSubmit: Boolean
        get() = birthDateMillis != null && chartState != ChartState.Loading
}

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val createChart: CreateChartUseCase,
    private val savedChartRepository: SavedChartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChartUiState())
    val uiState: StateFlow<ChartUiState> = _uiState.asStateFlow()

    fun onDateSelected(millis: Long?) = _uiState.update { it.copy(birthDateMillis = millis) }

    fun onTimeSelected(hour: Int, minute: Int) =
        _uiState.update { it.copy(hour = hour, minute = minute) }

    fun onGenderSelected(gender: Gender) = _uiState.update { it.copy(gender = gender) }

    fun onCreateChart() {
        val state = _uiState.value
        val dateMillis = state.birthDateMillis ?: return
        if (state.chartState == ChartState.Loading) return

        _uiState.update { it.copy(chartState = ChartState.Loading, saveState = SaveState.NotSaved) }
        viewModelScope.launch {
            val datetimeStr = formatDatetime(dateMillis, state.hour, state.minute)
            createChart(datetimeStr, state.gender)
                .onSuccess { result ->
                    _uiState.update { it.copy(chartState = ChartState.Success(result)) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(chartState = ChartState.Error(error.message ?: "Unknown error"))
                    }
                }
        }
    }

    fun onSaveChart() {
        val state = _uiState.value
        val result = (state.chartState as? ChartState.Success)?.result ?: return
        if (state.saveState != SaveState.NotSaved) return
        val dateMillis = state.birthDateMillis ?: return

        _uiState.update { it.copy(saveState = SaveState.Saving) }
        viewModelScope.launch {
            val datetimeStr = formatDatetime(dateMillis, state.hour, state.minute)
            savedChartRepository.saveChart(datetimeStr, state.gender, result)
                .onSuccess {
                    _uiState.update { it.copy(saveState = SaveState.Saved) }
                }
                .onFailure {
                    _uiState.update { it.copy(saveState = SaveState.NotSaved) }
                }
        }
    }

    private fun formatDatetime(dateMillis: Long, hour: Int, minute: Int): String {
        // DatePicker returns UTC-midnight millis -> format in UTC to keep the picked day
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val date = dateFormat.format(Date(dateMillis))
        return "%s %02d:%02d".format(Locale.US, date, hour, minute)
    }
}
