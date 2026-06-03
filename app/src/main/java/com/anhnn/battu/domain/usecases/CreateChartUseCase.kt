package com.anhnn.battu.domain.usecases

import com.anhnn.battu.domain.models.ChartResult
import com.anhnn.battu.domain.models.Gender
import com.anhnn.battu.domain.repository.ChartRepository
import javax.inject.Inject

class CreateChartUseCase @Inject constructor(
    private val repository: ChartRepository
) {

    suspend operator fun invoke(datetimeStr: String, gender: Gender): Result<ChartResult> {
        if (!DATETIME_REGEX.matches(datetimeStr)) {
            return Result.failure(
                IllegalArgumentException("datetime_str must be 'YYYY-MM-DD HH:MM[:SS]'")
            )
        }
        return repository.createChart(datetimeStr, gender)
    }

    private companion object {
        val DATETIME_REGEX = Regex("""\d{4}-\d{2}-\d{2} \d{2}:\d{2}(:\d{2})?""")
    }
}
