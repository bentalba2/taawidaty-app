package com.pharmatech.morocco.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// DataStore instance
private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

/**
 * Theme mode options
 */
enum class ThemeMode {
    SYSTEM,  // Follow system theme
    LIGHT,   // Always light mode
    DARK;    // Always dark mode

    companion object {
        fun fromString(value: String?): ThemeMode {
            return when (value) {
                "LIGHT" -> LIGHT
                "DARK" -> DARK
                "SYSTEM" -> SYSTEM
                else -> SYSTEM // Default to system
            }
        }
    }
}

/**
 * ThemePreferences - Manages theme mode preference using DataStore
 * 
 * Stores user's theme preference (System/Light/Dark) and provides
 * reactive Flow for observing theme changes.
 * 
 * @param context Application context for DataStore access
 */
@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.themeDataStore

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }

    /**
     * Flow of current theme mode preference
     */
    val themeModeFlow: Flow<ThemeMode> = dataStore.data
        .map { preferences ->
            val modeString = preferences[THEME_MODE_KEY]
            ThemeMode.fromString(modeString)
        }

    /**
     * Save theme mode preference
     * 
     * @param mode The theme mode to save
     */
    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }

    /**
     * Get current theme mode (suspend function for one-time read)
     * 
     * @return Current theme mode
     */
    suspend fun getThemeMode(): ThemeMode {
        val preferences = dataStore.data.map { it[THEME_MODE_KEY] }
        var mode: ThemeMode = ThemeMode.SYSTEM
        
        preferences.collect { value ->
            mode = ThemeMode.fromString(value)
        }
        
        return mode
    }
}
