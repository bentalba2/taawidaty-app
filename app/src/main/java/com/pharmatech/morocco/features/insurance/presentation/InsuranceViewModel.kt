package com.pharmatech.morocco.features.insurance.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pharmatech.morocco.features.medication.domain.model.InsuranceType
import com.pharmatech.morocco.features.medication.domain.model.Medication
import com.pharmatech.morocco.features.medication.domain.usecase.MedicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Insurance Reimbursement Calculator
 * Manages 3-step flow state and medication search
 */
@HiltViewModel
class InsuranceViewModel @Inject constructor(
    private val medicationRepository: MedicationRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(InsuranceUiState())
    val uiState: StateFlow<InsuranceUiState> = _uiState.asStateFlow()
    
    init {
        // Preload medications
        viewModelScope.launch {
            medicationRepository.loadMedications()
        }
    }
    
    /**
     * Step 1: Select Insurance
     */
    fun selectInsurance(insuranceType: InsuranceType) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedInsurance = insuranceType,
                currentStep = 2 // Auto-advance to medication search
            )
        }
    }
    
    /**
     * Step 2: Update search query and search medications
     */
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query, isLoading = true) }
        
        viewModelScope.launch {
            try {
                val results = if (query.isBlank()) {
                    emptyList()
                } else {
                    medicationRepository.searchMedications(query, limit = 50)
                        .map { it.medication } // Extract medication from search result
                }
                
                _uiState.update { currentState ->
                    currentState.copy(
                        searchResults = results,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = e.message ?: "Erreur de recherche"
                    )
                }
            }
        }
    }
    
    /**
     * Step 2: Select medication and advance to results
     */
    fun selectMedication(medication: Medication) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedMedication = medication,
                currentStep = 3 // Advance to results
            )
        }
    }
    
    /**
     * Navigate to previous step
     */
    fun previousStep() {
        _uiState.update { currentState ->
            val newStep = (currentState.currentStep - 1).coerceAtLeast(1)
            currentState.copy(currentStep = newStep)
        }
    }
    
    /**
     * Reset calculator to start
     */
    fun reset() {
        _uiState.value = InsuranceUiState()
    }
    
    /**
     * Pre-select medication (when navigating from medication screen)
     */
    fun setPreSelectedMedication(medicationName: String) {
        viewModelScope.launch {
            try {
                val medication = medicationRepository.getMedicationByName(medicationName)
                if (medication != null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            selectedMedication = medication,
                            currentStep = if (currentState.selectedInsurance != null) 3 else 1
                        )
                    }
                }
            } catch (e: Exception) {
                // Ignore if medication not found
            }
        }
    }
}

/**
 * UI State for Insurance Calculator
 */
data class InsuranceUiState(
    val currentStep: Int = 1,
    val selectedInsurance: InsuranceType? = null,
    val searchQuery: String = "",
    val searchResults: List<Medication> = emptyList(),
    val selectedMedication: Medication? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
