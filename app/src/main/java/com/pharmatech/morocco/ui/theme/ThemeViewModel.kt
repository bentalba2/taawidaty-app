package com.pharmatech.morocco.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pharmatech.morocco.core.datastore.ThemeMode
import com.pharmatech.morocco.core.datastore.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ThemeViewModel - Manages theme mode state and preferences
 * 
 * Provides reactive theme mode state and functions to update theme.
 * Persists user preference using DataStore.
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themePreferences: ThemePreferences
) : ViewModel() {

    /**
     * Current theme mode as StateFlow
     * Automatically updates UI when theme preference changes
     */
    val themeMode: StateFlow<ThemeMode> = themePreferences.themeModeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    /**
     * Update theme mode preference
     * 
     * @param mode New theme mode to set
     */
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themePreferences.setThemeMode(mode)
        }
    }

    /**
     * Cycle to next theme mode (System -> Light -> Dark -> System)
     */
    fun toggleThemeMode() {
        val nextMode = when (themeMode.value) {
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.SYSTEM
        }
        setThemeMode(nextMode)
    }
}
