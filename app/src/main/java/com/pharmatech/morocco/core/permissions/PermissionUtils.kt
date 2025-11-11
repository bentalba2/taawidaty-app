package com.pharmatech.morocco.core.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

/**
 * Camera permission state and handler
 */
data class CameraPermissionState(
    val hasPermission: Boolean = false,
    val shouldShowRationale: Boolean = false,
    val permissionRequested: Boolean = false
)

/**
 * Handles camera permission requests with Material 3 appropriate dialogs
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberCameraPermissionState(): CameraPermissionState {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    var permissionState by remember {
        mutableStateOf(
            CameraPermissionState(
                hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            )
        )
    }

    // Update permission state when it changes
    LaunchedEffect(cameraPermissionState.status) {
        permissionState = permissionState.copy(
            hasPermission = cameraPermissionState.status.isGranted,
            shouldShowRationale = cameraPermissionState.status.shouldShowRationale
        )
    }

    return permissionState.copy(
        permissionRequested = cameraPermissionState.permissionRequested
    )
}

/**
 * Location permission state and handler
 */
data class LocationPermissionState(
    val hasFineLocation: Boolean = false,
    val hasCoarseLocation: Boolean = false,
    val shouldShowRationale: Boolean = false,
    val permissionRequested: Boolean = false
)

/**
 * Handles location permission requests
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberLocationPermissionState(): LocationPermissionState {
    val context = LocalContext.current
    val fineLocationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val coarseLocationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_COARSE_LOCATION)

    var permissionState by remember {
        mutableStateOf(
            LocationPermissionState(
                hasFineLocation = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED,
                hasCoarseLocation = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
        )
    }

    // Update permission state when it changes
    LaunchedEffect(fineLocationPermissionState.status, coarseLocationPermissionState.status) {
        permissionState = permissionState.copy(
            hasFineLocation = fineLocationPermissionState.status.isGranted,
            hasCoarseLocation = coarseLocationPermissionState.status.isGranted,
            shouldShowRationale = fineLocationPermissionState.status.shouldShowRationale ||
                                coarseLocationPermissionState.status.shouldShowRationale
        )
    }

    return permissionState.copy(
        permissionRequested = fineLocationPermissionState.permissionRequested ||
                             coarseLocationPermissionState.permissionRequested
    )
}

/**
 * Checks if app has notification permission (Android 13+)
 */
fun hasNotificationPermission(context: Context): Boolean {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true // Notification permission not required before Android 13
    }
}

/**
 * Permission request launcher for notifications (Android 13+)
 */
@Composable
fun rememberNotificationPermissionLauncher(): (() -> Unit) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* Handle result if needed */ }
    )

    return {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}