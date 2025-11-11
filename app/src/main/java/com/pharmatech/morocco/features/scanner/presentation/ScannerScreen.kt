package com.pharmatech.morocco.features.scanner.presentation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pharmatech.morocco.core.permissions.rememberCameraPermissionState
import com.pharmatech.morocco.features.scanner.domain.CameraService
import com.pharmatech.morocco.features.scanner.domain.ScanResult
import com.pharmatech.morocco.ui.theme.HealthGreen
import timber.log.Timber

/**
 * Scanner Screen - Full barcode/QR code scanning functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    navController: NavController,
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberCameraPermissionState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Show permission dialog if needed
    if (!cameraPermissionState.hasPermission && !cameraPermissionState.permissionRequested) {
        CameraPermissionDialog(
            onRequestPermission = {
                // Request permission - the actual request is handled by the permission state
            },
            onDismiss = { navController.navigateUp() }
        )
    }

    // Show scan result dialog
    if (uiState.lastScanResult != null) {
        ScanResultDialog(
            scanResult = uiState.lastScanResult,
            onDismiss = { viewModel.dismissScanResult() },
            onViewMedication = { medicationId ->
                navController.navigate("medication_details/$medicationId")
                viewModel.dismissScanResult()
            },
            onSaveToHistory = { scanResult ->
                viewModel.saveToHistory(scanResult)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medication Scanner") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (cameraPermissionState.hasPermission) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Camera view with scanning functionality
                CameraView(
                    cameraService = viewModel.cameraService,
                    onScanResult = { scanResult ->
                        viewModel.handleScanResult(scanResult)
                    },
                    onClose = { navController.navigateUp() },
                    modifier = Modifier.fillMaxSize()
                )

                // Flash message area
                if (uiState.flashMessage != null) {
                    LaunchedEffect(uiState.flashMessage) {
                        kotlinx.coroutines.delay(3000)
                        viewModel.clearFlashMessage()
                    }

                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                            .padding(top = 80.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    ) {
                        Text(
                            text = uiState.flashMessage,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // Permission denied state
            PermissionDeniedScreen(
                onRequestPermission = {
                    // Navigate to app settings
                    viewModel.openAppSettings(context)
                },
                onDismiss = { navController.navigateUp() }
            )
        }
    }
}

/**
 * Camera permission dialog
 */
@Composable
private fun CameraPermissionDialog(
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Camera Permission Required",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                "TaaWiDaTy needs camera access to scan medication barcodes and QR codes. This helps you instantly identify medications and get detailed information.\n\nYour camera images are processed locally and are not stored or transmitted."
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onRequestPermission()
                }
            ) {
                Text("Grant Permission")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Permission denied screen with settings navigation
 */
@Composable
private fun PermissionDeniedScreen(
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Camera permission is required to scan medication barcodes and QR codes.\n\nPlease enable camera access in your device settings to use this feature.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Settings")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go Back")
        }
    }
}

/**
 * Scan result dialog with actions
 */
@Composable
private fun ScanResultDialog(
    scanResult: ScanResult,
    onDismiss: () -> Unit,
    onViewMedication: (String) -> Unit,
    onSaveToHistory: (ScanResult) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Success indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                HealthGreen.copy(alpha = 0.2f),
                                RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Success checkmark icon would go here
                        Text(
                            text = "✓",
                            color = HealthGreen,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Scan Successful",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Scan result details
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Scanned Value:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = scanResult.value,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        when (scanResult) {
                            is ScanResult.BarcodeScan -> {
                                Text(
                                    text = "Type: Product Barcode",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Format: ${scanResult.format}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            is ScanResult.QRCodeScan -> {
                                Text(
                                    text = "Type: QR Code",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onSaveToHistory(scanResult) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save to History")
                    }

                    Button(
                        onClick = {
                            // For demo purposes, use a placeholder medication ID
                            // In real implementation, you'd look up the medication
                            onViewMedication("demo_medication_id")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("View Details")
                    }
                }

                // Close button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue Scanning")
                }
            }
        }
    }
}
