package com.anhnn.battu.data.repository

import com.anhnn.battu.data.datasource.BaziApiService
import com.anhnn.battu.data.models.toDomain
import com.anhnn.battu.domain.models.CalendarMonth
import com.anhnn.battu.domain.repository.CalendarRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarRepositoryImpl @Inject constructor(
    private val api: BaziApiService,
) : CalendarRepository {

    override suspend fun getMonth(year: Int, month: Int): Result<CalendarMonth> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(api.getLichThang(year, month).toDomain())
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                Result.failure(IOException("Server error ${e.code()}: ${e.message()}", e))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
