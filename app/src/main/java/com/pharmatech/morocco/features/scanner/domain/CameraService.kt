package com.pharmatech.morocco.features.scanner.domain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Camera service for barcode scanning functionality
 */
class CameraService(
    private val context: Context
) {
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraExecutor: ExecutorService? = null
    private var barcodeScanner = BarcodeScanning.getClient()

    private val _cameraState = MutableStateFlow<CameraState>(CameraState.Initializing)
    val cameraState: StateFlow<CameraState> = _cameraState.asStateFlow()

    private val _scanResults = Channel<ScanResult>(capacity = Channel.UNLIMITED)
    val scanResults = _scanResults.receiveAsFlow()

    private val _torchState = MutableStateFlow(false)
    val torchState: StateFlow<Boolean> = _torchState.asStateFlow()

    init {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    /**
     * Initialize camera with barcode scanning
     */
    fun initializeCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        if (!hasCameraPermission(context)) {
            _cameraState.value = CameraState.PermissionDenied
            return
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()

                // Setup preview
                preview = Preview.Builder()
                    .setTargetAspectRatio(androidx.camera.core.AspectRatio.RATIO_4_3)
                    .build()
                preview?.setSurfaceProvider(previewView.surfaceProvider)

                // Setup image analysis for barcode scanning
                imageAnalyzer = ImageAnalysis.Builder()
                    .setTargetAspectRatio(androidx.camera.core.AspectRatio.RATIO_4_3)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor!!, BarcodeAnalyzer())
                    }

                // Select back camera as default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Unbind any previous use cases
                cameraProvider?.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )

                _cameraState.value = CameraState.Ready

                // Check if torch is available
                camera?.cameraInfo?.torchState?.observe(lifecycleOwner) { torchState ->
                    _torchState.value = torchState == TorchState.ON
                }

            } catch (e: Exception) {
                Timber.e(e, "Camera initialization failed")
                _cameraState.value = CameraState.Error(e.message ?: "Camera initialization failed")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Toggle torch/flashlight
     */
    fun toggleTorch() {
        camera?.cameraControl?.enableTorch(!_torchState.value)
    }

    /**
     * Stop camera and release resources
     */
    fun stopCamera() {
        try {
            cameraProvider?.unbindAll()
            camera = null
            preview = null
            imageAnalyzer = null
            _cameraState.value = CameraState.Initializing
        } catch (e: Exception) {
            Timber.e(e, "Error stopping camera")
        }
    }

    /**
     * Release all resources
     */
    fun release() {
        stopCamera()
        cameraExecutor?.shutdown()
        barcodeScanner.close()
    }

    /**
     * Check if camera permission is granted
     */
    private fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Provide haptic feedback on successful scan
     */
    private fun vibrate() {
        val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }
    }

    /**
     * Barcode analyzer using ML Kit
     */
    inner class BarcodeAnalyzer : ImageAnalysis.Analyzer {
        override fun analyze(image: ImageProxy) {
            val mediaImage = image.image
            if (mediaImage != null) {
                val inputImage = InputImage.fromMediaImage(
                    mediaImage,
                    image.imageInfo.rotationDegrees
                )

                barcodeScanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            barcodes.forEach { barcode ->
                                processBarcode(barcode)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Timber.e(e, "Barcode scanning failed")
                    }
                    .addOnCompleteListener {
                        image.close()
                    }
            } else {
                image.close()
            }
        }

        private fun processBarcode(barcode: Barcode) {
            val rawValue = barcode.rawValue
            if (!rawValue.isNullOrEmpty()) {
                val scanResult = when (barcode.format) {
                    Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8,
                    Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E -> {
                        // Product barcode
                        ScanResult.BarcodeScan(
                            value = rawValue,
                            format = barcode.format.toString(),
                            type = ScanResult.ScanType.PRODUCT
                        )
                    }
                    Barcode.FORMAT_QR_CODE -> {
                        // QR code
                        ScanResult.QRCodeScan(
                            value = rawValue,
                            type = ScanResult.ScanType.QR_CODE
                        )
                    }
                    else -> {
                        ScanResult.BarcodeScan(
                            value = rawValue,
                            format = barcode.format.toString(),
                            type = ScanResult.ScanType.UNKNOWN
                        )
                    }
                }

                // Send scan result and provide feedback
                _scanResults.trySend(scanResult)
                vibrate()

                // Pause scanning temporarily to avoid duplicate reads
                _cameraState.value = CameraState.Scanning
            }
        }
    }
}

/**
 * Camera state enumeration
 */
sealed class CameraState {
    object Initializing : CameraState()
    object Ready : CameraState()
    object Scanning : CameraState()
    object PermissionDenied : CameraState()
    data class Error(val message: String) : CameraState()
}

/**
 * Scan result sealed class
 */
sealed class ScanResult {
    abstract val value: String
    abstract val type: ScanType

    data class BarcodeScan(
        override val value: String,
        val format: String,
        override val type: ScanType
    ) : ScanResult()

    data class QRCodeScan(
        override val value: String,
        override val type: ScanType
    ) : ScanResult()

    enum class ScanType {
        PRODUCT,
        QR_CODE,
        UNKNOWN
    }
}