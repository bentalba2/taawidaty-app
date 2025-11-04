package com.pharmatech.morocco.features.tracker.domain.repository

import com.pharmatech.morocco.features.tracker.domain.model.MedicationSchedule
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for persisting and retrieving tracker schedules.
 */
interface TrackerStateRepository {
    val scheduleFlow: Flow<List<MedicationSchedule>>
    suspend fun saveSchedules(schedules: List<MedicationSchedule>)
    suspend fun clear()
}
