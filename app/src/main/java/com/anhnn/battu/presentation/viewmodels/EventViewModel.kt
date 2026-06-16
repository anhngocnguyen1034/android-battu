package com.anhnn.battu.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhnn.battu.domain.models.CalendarEvent
import com.anhnn.battu.domain.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
) : ViewModel() {

    private val _thang = MutableStateFlow(0)
    private val _nam = MutableStateFlow(0)

    /** Sự kiện của tháng đang hiển thị trong lịch. */
    val eventsThisMonth = combine(_thang, _nam) { thang, nam -> thang to nam }
        .flatMapLatest { (thang, nam) ->
            if (thang == 0) flowOf(emptyList()) else repository.eventsForMonth(thang, nam)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setThang(thang: Int, nam: Int) {
        _thang.value = thang
        _nam.value = nam
    }

    fun eventsForDay(day: Int, month: Int, year: Int): Flow<List<CalendarEvent>> =
        repository.eventsForDay(day, month, year)

    fun addEvent(tieuDe: String, ghiChu: String, ngay: Int, thang: Int, nam: Int, alarmEpoch: Long) =
        viewModelScope.launch {
            repository.addEvent(
                CalendarEvent(
                    tieuDe = tieuDe.trim(),
                    ghiChu = ghiChu.trim(),
                    ngayDuong = ngay,
                    thangDuong = thang,
                    namDuong = nam,
                    alarmEpoch = alarmEpoch,
                )
            )
        }

    fun deleteEvent(event: CalendarEvent) = viewModelScope.launch {
        repository.deleteEvent(event)
    }
}
