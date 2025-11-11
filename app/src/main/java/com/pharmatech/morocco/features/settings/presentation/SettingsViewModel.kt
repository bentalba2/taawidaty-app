package com.pharmatech.morocco.features.settings.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pharmatech.morocco.ui.theme.AppleThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Settings View Model
 * Manages app settings and preferences
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeManager: AppleThemeManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val themeMode = themeManager.getThemeMode()
                val useDynamicColors = themeManager.getUseDynamicColors()
                val appVersion = "1.0.0" // Get from BuildConfig in real app

                _uiState.value = SettingsUiState(
                    themeMode = themeMode,
                    useDynamicColors = useDynamicColors,
                    hapticFeedbackEnabled = true, // Default enabled
                    appVersion = appVersion,
                    hasCustomSettings = themeMode != AppleThemeManager.THEME_SYSTEM || !useDynamicColors
                )

                Timber.d("Settings loaded: theme=$themeMode, dynamic=$useDynamicColors")
            } catch (e: Exception) {
                Timber.e(e, "Error loading settings")
                _uiState.value = _uiState.value.copy(
                    appVersion = "1.0.0"
                )
            }
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            try {
                themeManager.setThemeMode(mode)
                _uiState.value = _uiState.value.copy(
                    themeMode = mode,
                    hasCustomSettings = true
                )
                Timber.d("Theme mode changed to: $mode")
            } catch (e: Exception) {
                Timber.e(e, "Error setting theme mode")
            }
        }
    }

    fun setUseDynamicColors(use: Boolean) {
        viewModelScope.launch {
            try {
                themeManager.setUseDynamicColors(use)
                _uiState.value = _uiState.value.copy(
                    useDynamicColors = use,
                    hasCustomSettings = true
                )
                Timber.d("Dynamic colors changed to: $use")
            } catch (e: Exception) {
                Timber.e(e, "Error setting dynamic colors")
            }
        }
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                // In a real app, you might save this to preferences
                _uiState.value = _uiState.value.copy(
                    hapticFeedbackEnabled = enabled,
                    hasCustomSettings = true
                )
                Timber.d("Haptic feedback changed to: $enabled")
            } catch (e: Exception) {
                Timber.e(e, "Error setting haptic feedback")
            }
        }
    }

    fun toggleDynamicColors() {
        setUseDynamicColors(!_uiState.value.useDynamicColors)
    }

    fun toggleHapticFeedback() {
        setHapticFeedbackEnabled(!_uiState.value.hapticFeedbackEnabled)
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            try {
                themeManager.setThemeMode(AppleThemeManager.THEME_SYSTEM)
                themeManager.setUseDynamicColors(true)

                _uiState.value = SettingsUiState(
                    themeMode = AppleThemeManager.THEME_SYSTEM,
                    useDynamicColors = true,
                    hapticFeedbackEnabled = true,
                    appVersion = _uiState.value.appVersion,
                    hasCustomSettings = false
                )

                Timber.d("Settings reset to defaults")
            } catch (e: Exception) {
                Timber.e(e, "Error resetting settings")
            }
        }
    }
}

/**
 * Settings UI State
 */
data class SettingsUiState(
    val themeMode: String = AppleThemeManager.THEME_SYSTEM,
    val useDynamicColors: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val appVersion: String = "1.0.0",
    val hasCustomSettings: Boolean = false
)