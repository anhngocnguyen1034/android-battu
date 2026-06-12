package com.anhnn.battu.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.anhnn.battu.data.models.SavedChartEntryDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.savedChartsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "battu_saved_charts"
)

/** Local saved-charts storage: a JSON list persisted in Preferences DataStore. */
@Singleton
class SavedChartDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) {

    private val chartsKey = stringPreferencesKey("saved_charts")

    val entries: Flow<List<SavedChartEntryDto>> = context.savedChartsDataStore.data
        .map { prefs -> decode(prefs[chartsKey]) }

    suspend fun upsert(entry: SavedChartEntryDto) {
        context.savedChartsDataStore.edit { prefs ->
            val current = decode(prefs[chartsKey])
            // Same birth time + gender replaces the previous entry
            val others = current.filterNot {
                it.datetimeStr == entry.datetimeStr && it.gender == entry.gender
            }
            prefs[chartsKey] = json.encodeToString(listOf(entry) + others)
        }
    }

    suspend fun delete(id: String) {
        context.savedChartsDataStore.edit { prefs ->
            val current = decode(prefs[chartsKey])
            prefs[chartsKey] = json.encodeToString(current.filterNot { it.id == id })
        }
    }

    private fun decode(raw: String?): List<SavedChartEntryDto> =
        if (raw.isNullOrBlank()) emptyList() else json.decodeFromString(raw)
}
