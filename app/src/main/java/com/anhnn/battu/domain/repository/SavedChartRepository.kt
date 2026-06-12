package com.anhnn.battu.domain.repository

import com.anhnn.battu.domain.models.ChartResult
import com.anhnn.battu.domain.models.Gender
import com.anhnn.battu.domain.models.SavedChart
import kotlinx.coroutines.flow.Flow

interface SavedChartRepository {

    /** Saved charts, newest first. */
    val savedCharts: Flow<List<SavedChart>>

    /**
     * Persists a created chart locally. Saving the same birth time + gender
     * again replaces the previous entry.
     */
    suspend fun saveChart(datetimeStr: String, gender: Gender, result: ChartResult): Result<Unit>

    /** Rebuilds the full [ChartResult] of a saved chart (works offline). */
    suspend fun getChart(id: String): Result<ChartResult>

    suspend fun deleteChart(id: String): Result<Unit>
}
