package com.anhnn.battu.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhnn.battu.domain.models.SavedChart
import com.anhnn.battu.domain.repository.SavedChartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedChartsViewModel @Inject constructor(
    private val repository: SavedChartRepository
) : ViewModel() {

    val savedCharts: StateFlow<List<SavedChart>> = repository.savedCharts
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun onDeleteChart(id: String) {
        viewModelScope.launch {
            repository.deleteChart(id)
        }
    }
}
