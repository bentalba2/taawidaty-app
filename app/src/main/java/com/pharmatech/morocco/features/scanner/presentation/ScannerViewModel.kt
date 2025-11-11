package com.pharmatech.morocco.features.scanner.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pharmatech.morocco.features.scanner.domain.CameraService
import com.pharmatech.morocco.features.scanner.domain.ScanResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * UI state for Scanner screen
 */
data class ScannerUiState(
    val lastScanResult: ScanResult? = null,
    val flashMessage: String? = null,
    val isLoading: Boolean = false,
    val scanHistory: List<ScanResult> = emptyList()
)

/**
 * ViewModel for Scanner screen
 */
@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val cameraService: CameraService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    // Expose camera service for the CameraView composable
    val cameraService: CameraService get() = this@ScannerViewModel.cameraService

    init {
        Timber.d("ScannerViewModel initialized")
    }

    /**
     * Handle scan results from the camera service
     */
    fun handleScanResult(scanResult: ScanResult) {
        viewModelScope.launch {
            try {
                Timber.d("Scan result received: $scanResult")

                // Update UI state with scan result
                _uiState.value = _uiState.value.copy(
                    lastScanResult = scanResult,
                    flashMessage = processScanResult(scanResult)
                )

                // Clear flash message after delay
                delay(3000)
                clearFlashMessage()

            } catch (e: Exception) {
                Timber.e(e, "Error handling scan result")
                _uiState.value = _uiState.value.copy(
                    flashMessage = "Error processing scan result"
                )
            }
        }
    }

    /**
     * Process scan result and provide user feedback
     */
    private fun processScanResult(scanResult: ScanResult): String {
        return when (scanResult) {
            is ScanResult.BarcodeScan -> {
                when (scanResult.type) {
                    ScanResult.ScanType.PRODUCT -> "Medication barcode detected"
                    else -> "Barcode scanned successfully"
                }
            }
            is ScanResult.QRCodeScan -> {
                when {
                    scanResult.value.startsWith("http") -> "QR code with link detected"
                    scanResult.value.contains("prescription", ignoreCase = true) -> "Prescription QR code detected"
                    else -> "QR code scanned successfully"
                }
            }
        }
    }

    /**
     * Dismiss scan result dialog
     */
    fun dismissScanResult() {
        _uiState.value = _uiState.value.copy(
            lastScanResult = null
        )
    }

    /**
     * Clear flash message
     */
    fun clearFlashMessage() {
        _uiState.value = _uiState.value.copy(
            flashMessage = null
        )
    }

    /**
     * Save scan result to history
     */
    fun saveToHistory(scanResult: ScanResult) {
        viewModelScope.launch {
            try {
                val currentHistory = _uiState.value.scanHistory.toMutableList()
                currentHistory.add(0, scanResult) // Add to beginning of list

                // Keep only last 50 items
                if (currentHistory.size > 50) {
                    currentHistory.removeAt(currentHistory.size - 1)
                }

                _uiState.value = _uiState.value.copy(
                    scanHistory = currentHistory,
                    flashMessage = "Saved to scan history"
                )

                Timber.d("Scan result saved to history: $scanResult")

                // Clear flash message after delay
                delay(2000)
                clearFlashMessage()

            } catch (e: Exception) {
                Timber.e(e, "Error saving scan result to history")
                _uiState.value = _uiState.value.copy(
                    flashMessage = "Error saving to history"
                )
            }
        }
    }

    /**
     * Open app settings for permission management
     */
    fun openAppSettings(context: Context) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            Timber.d("Opened app settings")
        } catch (e: Exception) {
            Timber.e(e, "Error opening app settings")
        }
    }

    /**
     * Get scan history for display
     */
    fun getScanHistory(): List<ScanResult> {
        return _uiState.value.scanHistory
    }

    /**
     * Clear scan history
     */
    fun clearScanHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                scanHistory = emptyList(),
                flashMessage = "Scan history cleared"
            )

            // Clear flash message after delay
            delay(2000)
            clearFlashMessage()
        }
    }

    /**
     * Handle different types of QR codes appropriately
     */
    private fun handleQRCodeContent(content: String): String {
        return when {
            content.startsWith("http://") || content.startsWith("https://") -> {
                // Web link - could open in browser
                "Web link detected"
            }
            content.contains("prescription", ignoreCase = true) -> {
                // Prescription code
                "Prescription QR code"
            }
            content.contains("medication", ignoreCase = true) -> {
                // Medication information
                "Medication information QR code"
            }
            content.matches(Regex("\\d{12,13}")) -> {
                // GTIN/EAN barcode number
                "Product number detected"
            }
            else -> {
                "QR code scanned"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Release camera resources
        cameraService.release()
        Timber.d("ScannerViewModel cleared and camera resources released")
    }
}