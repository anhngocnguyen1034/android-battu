package com.anhnn.battu.data.repository

import android.content.Context
import com.anhnn.battu.data.local.SuKienDao
import com.anhnn.battu.data.local.SuKienEntity
import com.anhnn.battu.domain.models.CalendarEvent
import com.anhnn.battu.domain.repository.EventRepository
import com.anhnn.battu.notification.AlarmHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: SuKienDao,
) : EventRepository {

    override fun eventsForMonth(month: Int, year: Int): Flow<List<CalendarEvent>> =
        dao.getThang(month, year).map { list -> list.map { it.toDomain() } }

    override fun eventsForDay(day: Int, month: Int, year: Int): Flow<List<CalendarEvent>> =
        dao.getNgay(day, month, year).map { list -> list.map { it.toDomain() } }

    override suspend fun addEvent(event: CalendarEvent) {
        val id = dao.insert(event.toEntity())
        if (event.alarmEpoch > 0) {
            AlarmHelper.schedule(context, id, event.tieuDe, event.ghiChu, event.alarmEpoch)
        }
    }

    override suspend fun deleteEvent(event: CalendarEvent) {
        AlarmHelper.cancel(context, event.id, event.tieuDe, event.ghiChu)
        dao.delete(event.toEntity())
    }
}

private fun SuKienEntity.toDomain() = CalendarEvent(
    id = id,
    tieuDe = tieuDe,
    ghiChu = ghiChu,
    ngayDuong = ngayDuong,
    thangDuong = thangDuong,
    namDuong = namDuong,
    alarmEpoch = alarmEpoch,
)

private fun CalendarEvent.toEntity() = SuKienEntity(
    id = id,
    tieuDe = tieuDe,
    ghiChu = ghiChu,
    ngayDuong = ngayDuong,
    thangDuong = thangDuong,
    namDuong = namDuong,
    alarmEpoch = alarmEpoch,
)
