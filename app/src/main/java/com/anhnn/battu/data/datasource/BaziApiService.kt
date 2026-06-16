package com.anhnn.battu.data.datasource

import com.anhnn.battu.data.models.ChartRequestDto
import com.anhnn.battu.data.models.FeedbackRequestDto
import com.anhnn.battu.data.models.NgayInfoDto
import com.anhnn.battu.data.models.ThangLichDto
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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

    @POST("api/v1/feedback")
    suspend fun sendFeedback(@Body request: FeedbackRequestDto)

    /** Lấy toàn bộ lịch của một tháng dương lịch. */
    @GET("api/v1/lich/{nam}/{thang}")
    suspend fun getLichThang(
        @Path("nam") nam: Int,
        @Path("thang") thang: Int,
    ): ThangLichDto

    /** Lấy thông tin đầy đủ của một ngày dương lịch. */
    @GET("api/v1/lich/{nam}/{thang}/{ngay}")
    suspend fun getNgayInfo(
        @Path("nam") nam: Int,
        @Path("thang") thang: Int,
        @Path("ngay") ngay: Int,
    ): NgayInfoDto

    /** Thông tin lịch của ngày hôm nay. */
    @GET("api/v1/lich/hom-nay")
    suspend fun getHomNay(): NgayInfoDto
}
