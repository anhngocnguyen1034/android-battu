package com.anhnn.battu.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhnn.battu.domain.models.ThemeMode
import com.anhnn.battu.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = settingsRepository.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ThemeMode.SYSTEM
        )

    /** Toggle between explicit LIGHT and DARK (resolving SYSTEM via [isCurrentlyDark]). */
    fun onToggleTheme(isCurrentlyDark: Boolean) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(
                if (isCurrentlyDark) ThemeMode.LIGHT else ThemeMode.DARK
            )
        }
    }
}
