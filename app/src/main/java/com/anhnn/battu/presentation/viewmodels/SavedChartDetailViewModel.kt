package com.anhnn.battu.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.anhnn.battu.domain.models.ChartResult
import com.anhnn.battu.domain.repository.SavedChartRepository
import com.anhnn.battu.presentation.navigation.SavedChartDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SavedChartDetailState {
    data object Loading : SavedChartDetailState
    data class Success(val result: ChartResult) : SavedChartDetailState
    data class Error(val message: String) : SavedChartDetailState
}

@HiltViewModel
class SavedChartDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SavedChartRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<SavedChartDetailRoute>()

    private val _state =
        MutableStateFlow<SavedChartDetailState>(SavedChartDetailState.Loading)
    val state: StateFlow<SavedChartDetailState> = _state.asStateFlow()

    init {
        loadChart()
    }

    private fun loadChart() {
        viewModelScope.launch {
            repository.getChart(route.id)
                .onSuccess { result ->
                    _state.value = SavedChartDetailState.Success(result)
                }
                .onFailure { error ->
                    _state.value =
                        SavedChartDetailState.Error(error.message ?: "Unknown error")
                }
        }
    }
}
