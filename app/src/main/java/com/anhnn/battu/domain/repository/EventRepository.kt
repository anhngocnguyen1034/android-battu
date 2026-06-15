package com.anhnn.battu.domain.repository

import com.anhnn.battu.domain.models.CalendarEvent
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    /** Các sự kiện trong một tháng dương lịch (để đánh dấu trên lưới lịch). */
    fun eventsForMonth(month: Int, year: Int): Flow<List<CalendarEvent>>

    /** Các sự kiện của một ngày cụ thể. */
    fun eventsForDay(day: Int, month: Int, year: Int): Flow<List<CalendarEvent>>

    /** Thêm sự kiện và đặt báo thức nếu [CalendarEvent.alarmEpoch] > 0. */
    suspend fun addEvent(event: CalendarEvent)

    /** Xóa sự kiện và hủy báo thức tương ứng. */
    suspend fun deleteEvent(event: CalendarEvent)
}
