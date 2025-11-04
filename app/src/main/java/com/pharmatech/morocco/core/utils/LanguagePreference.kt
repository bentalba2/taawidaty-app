package com.pharmatech.morocco.core.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Locale

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "language_preferences")

class LanguagePreferenceManager(private val context: Context) {

    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("app_language")

        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_FRENCH = "fr"
        const val LANGUAGE_ARABIC = "ar"

        private const val DEFAULT_LANGUAGE = LANGUAGE_FRENCH // Default to French as per requirements
    }

    val languageFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: getSystemLanguage()
    }

    suspend fun setLanguage(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }

    suspend fun getLanguage(): String {
        return context.dataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY] ?: getSystemLanguage()
        }.first()
    }

    private fun getSystemLanguage(): String {
        val systemLang = Locale.getDefault().language
        return when (systemLang) {
            "fr" -> LANGUAGE_FRENCH
            "ar" -> LANGUAGE_ARABIC
            "en" -> LANGUAGE_ENGLISH
            else -> DEFAULT_LANGUAGE // Fallback to French for unsupported languages
        }
    }

    fun getLocaleFromLanguageCode(languageCode: String): Locale {
        return when (languageCode) {
            LANGUAGE_FRENCH -> Locale.FRENCH
            LANGUAGE_ARABIC -> Locale("ar")
            LANGUAGE_ENGLISH -> Locale.ENGLISH
            else -> Locale.FRENCH
        }
    }
}
