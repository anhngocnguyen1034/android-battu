package com.anhnn.battu.data.datasource

import com.anhnn.battu.data.models.ChartRequestDto
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Remote datasource for the FOR-BAZI backend.
 *
 * Returns the raw [JsonObject] so the original `chart` block can be kept
 * verbatim — it must be sent back unchanged as `chart_data` when calling
 * POST /api/v1/chat/stream later (see docs/bug.md).
 */
interface BaziApiService {

    @POST("api/v1/chart")
    suspend fun createChart(@Body request: ChartRequestDto): JsonObject
}
