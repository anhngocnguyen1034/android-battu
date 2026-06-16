package com.anhnn.battu.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhnn.battu.domain.models.CalendarDay
import com.anhnn.battu.domain.models.CalendarMonth
import com.anhnn.battu.domain.repository.CalendarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

sealed interface CalendarState {
    data object Loading : CalendarState
    data class Success(val data: CalendarMonth) : CalendarState
    data class Error(val message: String) : CalendarState
}

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: CalendarRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<CalendarState>(CalendarState.Loading)
    val state: StateFlow<CalendarState> = _state.asStateFlow()

    private val _selectedDay = MutableStateFlow<CalendarDay?>(null)
    val selectedDay: StateFlow<CalendarDay?> = _selectedDay.asStateFlow()

    private val _currentMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH) + 1)
    val currentMonth: StateFlow<Int> = _currentMonth.asStateFlow()

    private val _currentYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val currentYear: StateFlow<Int> = _currentYear.asStateFlow()

    init {
        loadMonth(_currentMonth.value, _currentYear.value)
    }

    fun loadMonth(thang: Int, nam: Int) {
        _currentMonth.value = thang
        _currentYear.value = nam
        _selectedDay.value = null
        _state.value = CalendarState.Loading
        viewModelScope.launch {
            repository.getMonth(nam, thang)
                .onSuccess { data ->
                    _state.value = CalendarState.Success(data)
                    // Tự chọn ngày hôm nay nếu đang ở tháng hiện tại
                    val now = Calendar.getInstance()
                    if (thang == now.get(Calendar.MONTH) + 1 && nam == now.get(Calendar.YEAR)) {
                        _selectedDay.value = data.ngay.getOrNull(now.get(Calendar.DAY_OF_MONTH) - 1)
                    }
                }
                .onFailure { e ->
                    _state.value = CalendarState.Error(e.message ?: "Lỗi không xác định")
                }
        }
    }

    fun prevMonth() {
        val m = _currentMonth.value
        val y = _currentYear.value
        if (m == 1) loadMonth(12, y - 1) else loadMonth(m - 1, y)
    }

    fun nextMonth() {
        val m = _currentMonth.value
        val y = _currentYear.value
        if (m == 12) loadMonth(1, y + 1) else loadMonth(m + 1, y)
    }

    fun selectDay(day: CalendarDay) {
        _selectedDay.value = day
    }
}
