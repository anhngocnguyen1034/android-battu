package com.anhnn.battu.domain.repository

import com.anhnn.battu.domain.models.ChartResult
import com.anhnn.battu.domain.models.Gender

interface ChartRepository {

    /**
     * Calculate a full bazi chart from a solar birth datetime.
     *
     * @param datetimeStr format "YYYY-MM-DD HH:MM"
     */
    suspend fun createChart(datetimeStr: String, gender: Gender): Result<ChartResult>
}
