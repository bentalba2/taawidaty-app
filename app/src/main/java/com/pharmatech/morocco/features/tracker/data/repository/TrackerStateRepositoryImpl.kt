package com.pharmatech.morocco.features.tracker.data.repository

import com.pharmatech.morocco.features.tracker.data.local.TrackerPreferencesDataSource
import com.pharmatech.morocco.features.tracker.domain.model.MedicationSchedule
import com.pharmatech.morocco.features.tracker.domain.repository.TrackerStateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackerStateRepositoryImpl @Inject constructor(
    private val preferencesDataSource: TrackerPreferencesDataSource
) : TrackerStateRepository {

    override val scheduleFlow: Flow<List<MedicationSchedule>>
        get() = preferencesDataSource.schedulesFlow

    override suspend fun saveSchedules(schedules: List<MedicationSchedule>) {
        preferencesDataSource.saveSchedules(schedules)
    }

    override suspend fun clear() {
        preferencesDataSource.clear()
    }
}
