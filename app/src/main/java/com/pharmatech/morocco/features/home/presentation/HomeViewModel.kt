package com.pharmatech.morocco.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pharmatech.morocco.features.tracker.domain.model.MedicationSchedule
import com.pharmatech.morocco.features.tracker.domain.repository.TrackerStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val trackerStateRepository: TrackerStateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeTrackerMetrics()
    }

    private fun observeTrackerMetrics() {
        viewModelScope.launch {
            trackerStateRepository.scheduleFlow.collect { schedules ->
                _uiState.value = schedules.toHomeUiState()
            }
        }
    }
}

data class HomeUiState(
    val totalMedications: Int = 0,
    val takenCount: Int = 0,
    val remainingCount: Int = 0,
    val progress: Float = 0f,
    val nextMedication: NextMedication? = null
)

data class NextMedication(
    val name: String,
    val time: String,
    val dosage: String
)

private fun List<MedicationSchedule>.toHomeUiState(): HomeUiState {
    if (isEmpty()) {
        return HomeUiState()
    }

    val total = size
    val taken = count { it.isTaken }
    val remaining = total - taken
    val progressValue = if (total > 0) taken.toFloat() / total else 0f

    val nextMedication = filterNot { it.isTaken }
        .sortedBy { it.time }
        .firstOrNull()
        ?.let {
            NextMedication(
                name = it.medicationName,
                time = it.time,
                dosage = it.dosage
            )
        }

    return HomeUiState(
        totalMedications = total,
        takenCount = taken,
        remainingCount = remaining,
        progress = progressValue,
        nextMedication = nextMedication
    )
}
