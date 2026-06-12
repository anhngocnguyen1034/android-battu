package com.anhnn.battu.data.repository

import com.anhnn.battu.data.datasource.BaziApiService
import com.anhnn.battu.data.models.ChartRequestDto
import com.anhnn.battu.data.models.ChartResponseDto
import com.anhnn.battu.data.models.toDomain
import com.anhnn.battu.domain.models.ChartResult
import com.anhnn.battu.domain.models.Gender
import com.anhnn.battu.domain.repository.ChartRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChartRepositoryImpl @Inject constructor(
    private val api: BaziApiService,
    private val json: Json
) : ChartRepository {

    override suspend fun createChart(
        datetimeStr: String,
        gender: Gender
    ): Result<ChartResult> = withContext(Dispatchers.IO) {
        try {
            val response = api.createChart(ChartRequestDto(datetimeStr, gender.apiValue))
            val dto = json.decodeFromJsonElement<ChartResponseDto>(response)
            // Keep the raw `chart` block verbatim for the future AI chat feature
            val rawChartJson = response["chart"]?.toString().orEmpty()
            Result.success(dto.toDomain(rawChartJson, response.toString()))
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            Result.failure(IOException("Server error ${e.code()}: ${e.message()}", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
