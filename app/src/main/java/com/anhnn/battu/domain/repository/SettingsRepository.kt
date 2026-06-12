package com.anhnn.battu.domain.repository

import com.anhnn.battu.domain.models.ThemeMode
import kotlinx.coroutines.flow.Flow

/** App settings persistence (theme mode, …). */
interface SettingsRepository {

    /** Currently selected theme mode, defaults to [ThemeMode.SYSTEM]. */
    val themeMode: Flow<ThemeMode>

    suspend fun setThemeMode(mode: ThemeMode)
}
