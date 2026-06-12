package com.anhnn.battu.data.repository

import com.anhnn.battu.data.datasource.SettingsDataSource
import com.anhnn.battu.domain.models.ThemeMode
import com.anhnn.battu.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataSource: SettingsDataSource
) : SettingsRepository {

    override val themeMode: Flow<ThemeMode> =
        dataSource.themeMode.map { ThemeMode.fromValue(it) }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataSource.setThemeMode(mode.value)
    }
}
