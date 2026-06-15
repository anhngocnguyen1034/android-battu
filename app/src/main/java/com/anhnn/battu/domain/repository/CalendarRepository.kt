package com.anhnn.battu.domain.repository

import com.anhnn.battu.domain.models.CalendarMonth

interface CalendarRepository {
    /** Lấy toàn bộ lịch của một tháng dương lịch. */
    suspend fun getMonth(year: Int, month: Int): Result<CalendarMonth>
}
