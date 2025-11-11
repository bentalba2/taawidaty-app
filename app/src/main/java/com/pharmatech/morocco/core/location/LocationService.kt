package com.pharmatech.morocco.core.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Location service wrapper for handling GPS location
 */
class LocationService(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationRequest by lazy {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L) // 10 seconds
            .apply {
                setWaitForAccurateLocation(false)
                setMinUpdateIntervalMillis(5000L) // 5 seconds
                setMaxUpdateDelayMillis(15000L) // 15 seconds
            }
            .build()
    }

    private val locationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach { location ->
                    _locationUpdates.trySend(LocationData(location))
                    Timber.d("Location update received: ${location.latitude}, ${location.longitude}")
                }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                _locationAvailability.trySend(locationAvailability.isLocationAvailable)
                if (!locationAvailability.isLocationAvailable) {
                    Timber.w("Location became unavailable")
                }
            }
        }
    }

    private val _locationUpdates = Channel<LocationData>(capacity = Channel.UNLIMITED)
    val locationUpdates: Flow<LocationData> = _locationUpdates.receiveAsFlow()

    private val _locationAvailability = Channel<Boolean>(capacity = Channel.UNLIMITED)
    val locationAvailability: Flow<Boolean> = _locationAvailability.receiveAsFlow()

    /**
     * Check if location permissions are granted
     */
    fun hasLocationPermissions(): Boolean {
        return hasFineLocationPermission() || hasCoarseLocationPermission()
    }

    /**
     * Check if fine location permission is granted
     */
    fun hasFineLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if coarse location permission is granted
     */
    fun hasCoarseLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Get current location (one-time)
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationData {
        return suspendCancellableCoroutine { continuation ->
            if (!hasLocationPermissions()) {
                continuation.resumeWithException(
                    SecurityException("Location permissions not granted")
                )
                return@suspendCancellableCoroutine
            }

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                if (location != null) {
                    Timber.d("Current location obtained: ${location.latitude}, ${location.longitude}")
                    continuation.resume(LocationData(location))
                } else {
                    continuation.resumeWithException(
                        Exception("Unable to retrieve location")
                    )
                }
            }.addOnFailureListener { exception ->
                Timber.e(exception, "Failed to get current location")
                continuation.resumeWithException(exception)
            }
        }
    }

    /**
     * Start location updates
     */
    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        if (!hasLocationPermissions()) {
            Timber.w("Cannot start location updates - permissions not granted")
            return
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            ).addOnSuccessListener {
                Timber.d("Location updates started successfully")
            }.addOnFailureListener { exception ->
                Timber.e(exception, "Failed to start location updates")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error starting location updates")
        }
    }

    /**
     * Stop location updates
     */
    fun stopLocationUpdates() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
                .addOnSuccessListener {
                    Timber.d("Location updates stopped successfully")
                }
                .addOnFailureListener { exception ->
                    Timber.e(exception, "Failed to stop location updates")
                }
        } catch (e: Exception) {
            Timber.e(e, "Error stopping location updates")
        }
    }

    /**
     * Calculate distance between two points in meters
     */
    fun calculateDistance(
        latLng1: LatLng,
        latLng2: LatLng
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            latLng1.latitude, latLng1.longitude,
            latLng2.latitude, latLng2.longitude,
            results
        )
        return results[0]
    }

    /**
     * Format distance for display
     */
    fun formatDistance(meters: Float): String {
        return when {
            meters < 1000 -> "${meters.toInt()} m"
            meters < 10000 -> String.format("%.1f km", meters / 1000)
            else -> String.format("%.0f km", meters / 1000)
        }
    }

    /**
     * Get location provider accuracy level based on permissions
     */
    fun getLocationAccuracy(): LocationAccuracy {
        return when {
            hasFineLocationPermission() -> LocationAccuracy.HIGH
            hasCoarseLocationPermission() -> LocationAccuracy.MEDIUM
            else -> LocationAccuracy.NONE
        }
    }

    /**
     * Check if GPS is enabled
     */
    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
               locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
    }
}

/**
 * Data class for location information
 */
data class LocationData(
    val location: Location
) {
    val latitude: Double get() = location.latitude
    val longitude: Double get() = location.longitude
    val accuracy: Float get() = location.accuracy
    val timestamp: Long get() = location.time
    val latLng: LatLng get() = LatLng(latitude, longitude)

    /**
     * Get formatted address string (placeholder - would require geocoding)
     */
    fun getAddressString(): String {
        return "${latitude}, ${longitude}"
    }

    /**
     * Check if location is reasonably recent (within last 5 minutes)
     */
    fun isRecent(): Boolean {
        val fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000)
        return timestamp > fiveMinutesAgo
    }

    /**
     * Check if location accuracy is good enough
     */
    fun isAccurate(): Boolean {
        return accuracy <= 100f // 100 meters or better
    }
}

/**
 * Location accuracy levels
 */
enum class LocationAccuracy {
    NONE,
    MEDIUM,    // Coarse location
    HIGH       // Fine location
}