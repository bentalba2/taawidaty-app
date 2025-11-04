package com.pharmatech.morocco.features.tracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pharmatech.morocco.features.tracker.domain.model.MedicationSchedule
import com.pharmatech.morocco.features.tracker.domain.repository.TrackerStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for medication tracker
 * Persists medication schedule state via DataStore-backed repository
 */
@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val trackerStateRepository: TrackerStateRepository
) : ViewModel() {

    private val _scheduleList = MutableStateFlow<List<MedicationSchedule>>(emptyList())
    val scheduleList: StateFlow<List<MedicationSchedule>> = _scheduleList.asStateFlow()

    init {
        observeStoredSchedules()
    }

    private fun observeStoredSchedules() {
        viewModelScope.launch {
            trackerStateRepository.scheduleFlow.collect { storedSchedules ->
                if (storedSchedules.isEmpty()) {
                    val defaults = defaultSchedule()
                    trackerStateRepository.saveSchedules(defaults)
                    _scheduleList.value = defaults
                } else {
                    _scheduleList.value = storedSchedules
                }
            }
        }
    }

    fun toggleMedicationTaken(medicationId: String) {
        updateAndPersist { schedule ->
            schedule.map { item ->
                if (item.id == medicationId) {
                    item.copy(isTaken = !item.isTaken)
                } else {
                    item
                }
            }
        }
    }

    fun addMedication(
        medicationName: String,
        dosage: String,
        times: List<String>,
        medicationId: String? = null,
        notes: String? = null
    ) {
        val sanitizedTimes = times.mapNotNull { time ->
            time.trim().takeIf { it.isNotBlank() }
        }.ifEmpty { listOf("08:00") }

        updateAndPersist { schedule ->
            val frequency = sanitizedTimes.size
            val newEntries = sanitizedTimes.map { time ->
                MedicationSchedule(
                    id = UUID.randomUUID().toString(),
                    medicationId = medicationId,
                    medicationName = medicationName,
                    dosage = dosage,
                    time = time,
                    notes = notes,
                    isTaken = false,
                    frequencyPerDay = frequency
                )
            }
            schedule + newEntries
        }
    }

    fun removeMedication(medicationId: String) {
        updateAndPersist { schedule ->
            schedule.filterNot { it.id == medicationId }
        }
    }

    fun resetDailyProgress() {
        updateAndPersist { schedule ->
            schedule.map { it.copy(isTaken = false) }
        }
    }

    fun getTakenCount(list: List<MedicationSchedule> = _scheduleList.value): Int {
        return list.count { it.isTaken }
    }

    fun getTotalCount(list: List<MedicationSchedule> = _scheduleList.value): Int {
        return list.size
    }

    fun getAdherenceRate(list: List<MedicationSchedule> = _scheduleList.value): Int {
        val taken = getTakenCount(list)
        val total = getTotalCount(list)
        return if (total > 0) ((taken * 100f) / total).toInt() else 0
    }

    private fun updateAndPersist(transform: (List<MedicationSchedule>) -> List<MedicationSchedule>) {
        viewModelScope.launch {
            val updated = transform(_scheduleList.value)
            _scheduleList.value = updated
            trackerStateRepository.saveSchedules(updated)
        }
    }

    private fun defaultSchedule(): List<MedicationSchedule> {
        return listOf(
            MedicationSchedule(
                id = DEFAULT_IDS[0],
                medicationName = "Doliprane 1000mg",
                dosage = "1 comprimé",
                time = "08:00",
                frequencyPerDay = 2
            ),
            MedicationSchedule(
                id = DEFAULT_IDS[1],
                medicationName = "Amoxicilline 500mg",
                dosage = "1 gélule",
                time = "12:00",
                frequencyPerDay = 1
            ),
            MedicationSchedule(
                id = DEFAULT_IDS[2],
                medicationName = "Oméprazole 20mg",
                dosage = "1 gélule",
                time = "14:30",
                frequencyPerDay = 1
            ),
            MedicationSchedule(
                id = DEFAULT_IDS[3],
                medicationName = "Doliprane 1000mg",
                dosage = "1 comprimé",
                time = "20:00",
                frequencyPerDay = 2
            )
        )
    }

    companion object {
        private val DEFAULT_IDS = listOf(
            "default-1",
            "default-2",
            "default-3",
            "default-4"
        )
    }
}
