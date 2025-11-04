package com.pharmatech.morocco.features.tracker.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pharmatech.morocco.features.tracker.domain.model.MedicationSchedule
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private const val TRACKER_PREFERENCES_NAME = "tracker_preferences"
private val SCHEDULES_KEY = stringPreferencesKey("daily_schedule")

private val Context.trackerDataStore: DataStore<Preferences> by preferencesDataStore(
    name = TRACKER_PREFERENCES_NAME
)

@Singleton
class TrackerPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) {

    val schedulesFlow: Flow<List<MedicationSchedule>> = context.trackerDataStore.data.map { preferences ->
        preferences[SCHEDULES_KEY]?.let { storedValue ->
            runCatching { json.decodeFromString<List<MedicationSchedule>>(storedValue) }
                .getOrElse { emptyList() }
        } ?: emptyList()
    }

    suspend fun saveSchedules(schedules: List<MedicationSchedule>) {
        context.trackerDataStore.edit { preferences ->
            preferences[SCHEDULES_KEY] = json.encodeToString(schedules)
        }
    }

    suspend fun clear() {
        context.trackerDataStore.edit { preferences ->
            preferences.remove(SCHEDULES_KEY)
        }
    }
}
