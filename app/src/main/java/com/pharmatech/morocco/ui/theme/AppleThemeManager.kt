package com.pharmatech.morocco.ui.theme

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pharmatech.morocco.core.datastore.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "apple_theme_preferences")

/**
 * Apple-inspired Theme Manager
 * Handles light/dark mode with system detection
 */
class AppleThemeManager(private val context: Context) {

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("apple_theme_mode")
        private val DYNAMIC_COLORS_KEY = booleanPreferencesKey("apple_dynamic_colors")

        // Theme modes
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"

        // Default theme
        private const val DEFAULT_THEME = THEME_SYSTEM
    }

    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_MODE_KEY] ?: DEFAULT_THEME
    }

    val useDynamicColors: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DYNAMIC_COLORS_KEY] ?: true
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode
        }
    }

    suspend fun setUseDynamicColors(use: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_COLORS_KEY] = use
        }
    }

    suspend fun getThemeMode(): String {
        return themeMode.first()
    }

    suspend fun getUseDynamicColors(): Boolean {
        return useDynamicColors.first()
    }

    fun getEffectiveTheme(isSystemInDarkMode: Boolean): String {
        return runBlocking {
            val savedTheme = getThemeMode()
            when (savedTheme) {
                THEME_LIGHT -> THEME_LIGHT
                THEME_DARK -> THEME_DARK
                THEME_SYSTEM -> if (isSystemInDarkMode) THEME_DARK else THEME_LIGHT
                else -> THEME_SYSTEM
            }
        }
    }

    fun shouldUseDarkMode(isSystemInDarkMode: Boolean): Boolean {
        return getEffectiveTheme(isSystemInDarkMode) == THEME_DARK
    }
}

/**
 * Apple Theme Data class
 */
data class AppleTheme(
    val mode: String = AppleThemeManager.THEME_SYSTEM,
    val useDynamicColors: Boolean = true,
    val isDarkMode: Boolean = false
)

/**
 * Theme state for Compose
 */
class AppleThemeState(
    private val themeManager: AppleThemeManager,
    private val systemDarkMode: () -> Boolean
) {
    var theme by mutableStateOf(
        AppleTheme(
            mode = AppleThemeManager.THEME_SYSTEM,
            useDynamicColors = true,
            isDarkMode = false
        )
        )
        private set

    init {
        // Initialize theme from preferences
        val themeMode = runBlocking { themeManager.getThemeMode() }
        val useDynamicColors = runBlocking { themeManager.getUseDynamicColors() }
        val isDarkMode = themeManager.shouldUseDarkMode(systemDarkMode())

        theme = AppleTheme(themeMode, useDynamicColors, isDarkMode)
    }

    fun updateTheme(mode: String) {
        theme = theme.copy(
            mode = mode,
            isDarkMode = themeManager.shouldUseDarkMode(systemDarkMode())
        )
        // Persist preference
        runBlocking { themeManager.setThemeMode(mode) }
    }

    fun updateDynamicColors(use: Boolean) {
        theme = theme.copy(useDynamicColors = use)
        // Persist preference
        runBlocking { themeManager.setUseDynamicColors(use) }
    }

    fun updateSystemDarkMode(isSystemInDarkMode: Boolean) {
        if (theme.mode == AppleThemeManager.THEME_SYSTEM) {
            theme = theme.copy(isDarkMode = isSystemInDarkMode)
        }
    }

    fun getColorScheme(): ColorScheme {
        return if (theme.isDarkMode) {
            AppleDarkColorScheme
        } else {
            AppleLightColorScheme
        }
    }
}

/**
 * Apple Theme Provider
 */
@Composable
fun rememberAppleThemeState(
    themeManager: AppleThemeManager,
    systemDarkMode: Boolean
): AppleThemeState {
    val themeState = remember(themeManager, systemDarkMode) {
        AppleThemeState(themeManager) { systemDarkMode }
    }

    // Update when system dark mode changes
    LaunchedEffect(systemDarkMode) {
        themeState.updateSystemDarkMode(systemDarkMode)
    }

    return themeState
}