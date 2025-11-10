package com.pharmatech.morocco.features.home.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pharmatech.morocco.core.location.LocationData
import com.pharmatech.morocco.core.location.LocationService
import com.pharmatech.morocco.features.tracker.domain.model.MedicationSchedule
import com.pharmatech.morocco.features.tracker.domain.repository.TrackerStateRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val trackerStateRepository: TrackerStateRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Mock nearby pharmacies data (in real app, would come from API/database)
    private val mockPharmacies = listOf(
        NearbyPharmacy(
            id = "1",
            name = "Pharmacie du Centre",
            address = "123 Avenue Mohammed V, Rabat",
            distance = 0.0f,
            latLng = LatLng(34.0209, -6.8416), // Central Rabat
            rating = 4.5f,
            isOpen = true
        ),
        NearbyPharmacy(
            id = "2",
            name = "Pharmacie Agdal",
            address = "45 Rue Agdal, Rabat",
            distance = 0.0f,
            latLng = LatLng(34.0150, -6.8367),
            rating = 4.2f,
            isOpen = true
        ),
        NearbyPharmacy(
            id = "3",
            name = "Pharmacie Hay Ryad",
            address = "78 Boulevard Hay Ryad, Rabat",
            distance = 0.0f,
            latLng = LatLng(34.0089, -6.8243),
            rating = 4.7f,
            isOpen = false
        )
    )

    init {
        observeTrackerMetrics()
        initializeLocationFeatures()
    }

    private fun observeTrackerMetrics() {
        viewModelScope.launch {
            trackerStateRepository.scheduleFlow.collect { schedules ->
                _uiState.value = _uiState.value.copy(
                    totalMedications = schedules.size,
                    takenCount = schedules.count { it.isTaken },
                    remainingCount = schedules.count { !it.isTaken },
                    progress = if (schedules.isNotEmpty()) {
                        schedules.count { it.isTaken }.toFloat() / schedules.size
                    } else 0f,
                    nextMedication = schedules.filterNot { it.isTaken }
                        .sortedBy { it.time }
                        .firstOrNull()
                        ?.let {
                            NextMedication(
                                name = it.medicationName,
                                time = it.time,
                                dosage = it.dosage
                            )
                        }
                )
            }
        }
    }

    private fun initializeLocationFeatures() {
        viewModelScope.launch {
            // Check if location permissions are available
            val hasPermissions = locationService.hasLocationPermissions()
            val isLocationEnabled = locationService.isLocationEnabled()

            _uiState.value = _uiState.value.copy(
                locationPermissionGranted = hasPermissions,
                locationEnabled = isLocationEnabled,
                isLoadingLocation = hasPermissions && isLocationEnabled
            )

            if (hasPermissions && isLocationEnabled) {
                loadCurrentLocation()
            }
        }
    }

    /**
     * Load current user location and calculate distances to nearby pharmacies
     */
    fun loadCurrentLocation() {
        if (!locationService.hasLocationPermissions()) {
            _uiState.value = _uiState.value.copy(
                locationPermissionGranted = false,
                isLoadingLocation = false,
                locationError = "Location permissions not granted"
            )
            return
        }

        if (!locationService.isLocationEnabled()) {
            _uiState.value = _uiState.value.copy(
                locationEnabled = false,
                isLoadingLocation = false,
                locationError = "Location services are disabled"
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoadingLocation = true, locationError = null)

        viewModelScope.launch {
            try {
                val locationData = locationService.getCurrentLocation()

                // Calculate distances to pharmacies
                val pharmaciesWithDistance = mockPharmacies.map { pharmacy ->
                    val distance = locationService.calculateDistance(
                        locationData.latLng,
                        pharmacy.latLng
                    )
                    pharmacy.copy(distance = distance)
                }.sortedBy { it.distance }

                _uiState.value = _uiState.value.copy(
                    currentLocation = locationData,
                    nearbyPharmacies = pharmaciesWithDistance.take(3), // Show top 3
                    isLoadingLocation = false,
                    locationError = null
                )

                // Start location updates for real-time tracking
                locationService.startLocationUpdates()

            } catch (e: Exception) {
                Timber.e(e, "Failed to get current location")
                _uiState.value = _uiState.value.copy(
                    isLoadingLocation = false,
                    locationError = "Failed to get location: ${e.message}"
                )
            }
        }
    }

    /**
     * Handle location permission request result
     */
    fun onLocationPermissionResult(granted: Boolean) {
        _uiState.value = _uiState.value.copy(locationPermissionGranted = granted)

        if (granted) {
            loadCurrentLocation()
        }
    }

    /**
     * Request location permission and navigate to app settings if needed
     */
    fun requestLocationPermission(context: Context) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e, "Error opening app settings")
        }
    }

    /**
     * Clear location error
     */
    fun clearLocationError() {
        _uiState.value = _uiState.value.copy(locationError = null)
    }

    /**
     * Refresh location data
     */
    fun refreshLocation() {
        loadCurrentLocation()
    }

    override fun onCleared() {
        super.onCleared()
        locationService.stopLocationUpdates()
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
