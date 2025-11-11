package com.pharmatech.morocco.features.scanner.presentation

import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.pharmatech.morocco.features.scanner.domain.CameraService
import com.pharmatech.morocco.features.scanner.domain.CameraState
import com.pharmatech.morocco.features.scanner.domain.ScanResult
import com.pharmatech.morocco.ui.theme.HealthGreen
import com.pharmatech.morocco.ui.theme.PrimaryGradientEnd
import com.pharmatech.morocco.ui.theme.PrimaryGradientStart
import kotlinx.coroutines.delay
import timber.log.Timber

/**
 * Camera view composable with barcode scanning overlay
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraView(
    cameraService: CameraService,
    onScanResult: (ScanResult) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    val cameraState by cameraService.cameraState.collectAsState()
    val torchState by cameraService.torchState.collectAsState()

    // Handle scan results
    LaunchedEffect(Unit) {
        cameraService.scanResults.collect { result ->
            onScanResult(result)
            // Add delay to prevent duplicate scans
            delay(2000)
        }
    }

    // Cleanup when composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            cameraService.stopCamera()
        }
    }

    Box(modifier = modifier) {
        // Camera preview
        AndroidView(
            factory = { context ->
                PreviewView(context).apply {
                    previewView = this
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { preview ->
                previewView?.let { pv ->
                    cameraService.initializeCamera(lifecycleOwner, pv)
                }
            }
        )

        // Scanning overlay
        ScanningOverlay(
            modifier = Modifier.fillMaxSize(),
            torchState = torchState,
            cameraState = cameraState,
            onTorchClick = { cameraService.toggleTorch() },
            onCloseClick = onClose
        )

        // Loading or error states
        when (cameraState) {
            is CameraState.Initializing -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = HealthGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Initializing Camera...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                }
            }

            is CameraState.PermissionDenied -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Camera Permission Required",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Please grant camera permission to scan medication barcodes and QR codes.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            is CameraState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Camera Error",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = cameraState.message,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            else -> {
                // Camera is ready, show overlay
            }
        }
    }
}

/**
 * Scanning overlay with targeting reticle and controls
 */
@Composable
private fun ScanningOverlay(
    modifier: Modifier = Modifier,
    torchState: Boolean,
    cameraState: CameraState,
    onTorchClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Box(modifier = modifier) {
        // Scanning area overlay (darkened corners)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scanningRectSize = Size(size.width * 0.7f, size.height * 0.4f)
            val scanningRect = Rect(
                center = center,
                size = scanningRectSize
            )

            drawScanningOverlay(scanningRect)
        }

        // Top controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Close button
            IconButton(
                onClick = onCloseClick,
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }

            // Torch button (if available)
            if (cameraState is CameraState.Ready) {
                IconButton(
                    onClick = onTorchClick,
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (torchState) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                        contentDescription = if (torchState) "Turn off flashlight" else "Turn on flashlight",
                        tint = Color.White
                    )
                }
            }
        }

        // Scanning instructions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Align barcode within frame",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Camera will automatically detect barcodes and QR codes",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // Scanning line animation
        if (cameraState is CameraState.Ready) {
            ScanningLine(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(2.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

/**
 * Draw the scanning overlay with transparent center area
 */
private fun DrawScope.drawScanningOverlay(scanningRect: Rect) {
    val overlayColor = Color.Black.copy(alpha = 0.6f)

    // Top overlay
    drawRect(
        color = overlayColor,
        topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
        size = Size(size.width, scanningRect.top)
    )

    // Bottom overlay
    drawRect(
        color = overlayColor,
        topLeft = androidx.compose.ui.geometry.Offset(0f, scanningRect.bottom),
        size = Size(size.width, size.height - scanningRect.bottom)
    )

    // Left overlay
    drawRect(
        color = overlayColor,
        topLeft = androidx.compose.ui.geometry.Offset(0f, scanningRect.top),
        size = Size(scanningRect.left, scanningRect.height)
    )

    // Right overlay
    drawRect(
        color = overlayColor,
        topLeft = androidx.compose.ui.geometry.Offset(scanningRect.right, scanningRect.top),
        size = Size(size.width - scanningRect.right, scanningRect.height)
    )

    // Corner brackets
    val cornerLength = 24f
    val cornerWidth = 4f
    val cornerColor = HealthGreen

    // Top-left corner
    drawRoundRect(
        color = cornerColor,
        topLeft = androidx.compose.ui.geometry.Offset(scanningRect.left - 2f, scanningRect.top - 2f),
        size = Size(cornerLength, cornerWidth),
        cornerRadius = CornerRadius(cornerWidth / 2)
    )
    drawRoundRect(
        color = cornerColor,
        topLeft = androidx.compose.ui.geometry.Offset(scanningRect.left - 2f, scanningRect.top - 2f),
        size = Size(cornerWidth, cornerLength),
        cornerRadius = CornerRadius(cornerWidth / 2)
    )

    // Top-right corner
    drawRoundRect(
        color = cornerColor,
        topLeft = androidx.compose.ui.geometry.Offset(scanningRect.right - cornerLength + 2f, scanningRect.top - 2f),
        size = Size(cornerLength, cornerWidth),
        cornerRadius = CornerRadius(cornerWidth / 2)
    )
    drawRoundRect(
        color = cornerColor,
        topLeft = androidx.compose.ui.geometry.Offset(scanningRect.right - cornerWidth + 2f, scanningRect.top - 2f),
        size = Size(cornerWidth, cornerLength),
        cornerRadius = CornerRadius(cornerWidth / 2)
    )

    // Bottom-left corner
    drawRoundRect(
        color = cornerColor,
        topLeft = androidx.compose.ui.geometry.Offset(scanningRect.left - 2f, scanningRect.bottom - cornerWidth + 2f),
        size = Size(cornerLength, cornerWidth),
        cornerRadius = CornerRadius(cornerWidth / 2)
    )
    drawRoundRect(
        color = cornerColor,
        topLeft = androidx.compose.ui.geometry.Offset(scanningRect.left - 2f, scanningRect.bottom - cornerLength + 2f),
        size = Size(cornerWidth, cornerLength),
        cornerRadius = CornerRadius(cornerWidth / 2)
    )

    // Bottom-right corner
    drawRoundRect(
        color = cornerColor,
        topLeft = androidx.compose.ui.geometry.Offset(scanningRect.right - cornerLength + 2f, scanningRect.bottom - cornerWidth + 2f),
        size = Size(cornerLength, cornerWidth),
        cornerRadius = CornerRadius(cornerWidth / 2)
    )
    drawRoundRect(
        color = cornerColor,
        topLeft = androidx.compose.ui.geometry.Offset(scanningRect.right - cornerWidth + 2f, scanningRect.bottom - cornerLength + 2f),
        size = Size(cornerWidth, cornerLength),
        cornerRadius = CornerRadius(cornerWidth / 2)
    )
}

/**
 * Animated scanning line
 */
@Composable
private fun ScanningLine(
    modifier: Modifier = Modifier
) {
    var scanningPosition by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            scanningPosition = 0f
            delay(100)
            scanningPosition = 1f
            delay(1500)
        }
    }

    Box(
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val lineHeight = 2.dp.toPx()
            val lineWidth = size.width
            val lineY = size.height * scanningPosition

            // Create gradient effect for the scanning line
            drawRect(
                color = HealthGreen,
                topLeft = androidx.compose.ui.geometry.Offset(0f, lineY - lineHeight / 2),
                size = Size(lineWidth, lineHeight)
            )
        }
    }
}