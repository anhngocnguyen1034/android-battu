package com.anhnn.battu.domain.models

import androidx.compose.runtime.Immutable

/** Metadata of a chart saved on the device (the full data is stored alongside it). */
@Immutable
data class SavedChart(
    val id: String,
    /** "YYYY-MM-DD HH:MM" solar birth time used to create the chart. */
    val datetimeStr: String,
    val gender: Gender,
    val savedAtMillis: Long
)
