package com.anhnn.battu.data.repository

import com.anhnn.battu.data.datasource.SavedChartDataSource
import com.anhnn.battu.data.models.ChartResponseDto
import com.anhnn.battu.data.models.SavedChartEntryDto
import com.anhnn.battu.data.models.toDomain
import com.anhnn.battu.domain.models.ChartResult
import com.anhnn.battu.domain.models.Gender
import com.anhnn.battu.domain.models.SavedChart
import com.anhnn.battu.domain.repository.SavedChartRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedChartRepositoryImpl @Inject constructor(
    private val dataSource: SavedChartDataSource,
    private val json: Json
) : SavedChartRepository {

    override val savedCharts: Flow<List<SavedChart>> = dataSource.entries
        .map { entries ->
            entries
                .map { it.toDomain() }
                .sortedByDescending { it.savedAtMillis }
        }

    override suspend fun saveChart(
        datetimeStr: String,
        gender: Gender,
        result: ChartResult
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dataSource.upsert(
                SavedChartEntryDto(
                    id = UUID.randomUUID().toString(),
                    datetimeStr = datetimeStr,
                    gender = gender.name,
                    savedAtMillis = System.currentTimeMillis(),
                    responseJson = result.rawResponseJson
                )
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getChart(id: String): Result<ChartResult> =
        withContext(Dispatchers.IO) {
            try {
                val entry = dataSource.entries.first().firstOrNull { it.id == id }
                    ?: return@withContext Result.failure(
                        NoSuchElementException("Saved chart not found: $id")
                    )
                val response = json.decodeFromString<JsonObject>(entry.responseJson)
                val dto = json.decodeFromJsonElement<ChartResponseDto>(response)
                val rawChartJson = response["chart"]?.toString().orEmpty()
                Result.success(dto.toDomain(rawChartJson, entry.responseJson))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deleteChart(id: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                dataSource.delete(id)
                Result.success(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

private fun SavedChartEntryDto.toDomain() = SavedChart(
    id = id,
    datetimeStr = datetimeStr,
    gender = Gender.entries.firstOrNull { it.name == gender } ?: Gender.MALE,
    savedAtMillis = savedAtMillis
)
