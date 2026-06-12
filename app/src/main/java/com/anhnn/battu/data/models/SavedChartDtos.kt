package com.anhnn.battu.data.models

import kotlinx.serialization.Serializable

/** Saved-chart record persisted locally (DataStore). */
@Serializable
data class SavedChartEntryDto(
    val id: String,
    /** "YYYY-MM-DD HH:MM" solar birth time. */
    val datetimeStr: String,
    /** [com.anhnn.battu.domain.models.Gender] enum name ("MALE"/"FEMALE"). */
    val gender: String,
    val savedAtMillis: Long,
    /** Full API response JSON, kept verbatim to rebuild the chart offline. */
    val responseJson: String
)
