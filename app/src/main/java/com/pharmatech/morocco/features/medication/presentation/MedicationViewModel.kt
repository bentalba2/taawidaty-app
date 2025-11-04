package com.pharmatech.morocco.features.medication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pharmatech.morocco.features.medication.domain.model.MedicationSearchResult
import com.pharmatech.morocco.features.medication.domain.usecase.MedicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for medication search and display
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class MedicationViewModel @Inject constructor(
    private val medicationRepository: MedicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicationUiState())
    val uiState: StateFlow<MedicationUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                medicationRepository.loadMedications()
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Erreur de chargement: ${e.message}"
                    )
                }
            }
        }
    }

    fun searchMedications(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val results = medicationRepository.searchMedications(query, limit = 50)
                _uiState.update { 
                    it.copy(
                        searchResults = results,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Erreur de recherche: ${e.message}"
                    )
                }
            }
        }
    }
}

/**
 * UI state for medication screen
 */
data class MedicationUiState(
    val searchResults: List<MedicationSearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
