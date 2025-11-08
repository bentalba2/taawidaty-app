/*
 * AnimatedToast.kt
 * TAAWIDATY System
 * 
 * Description: Animated toast and snackbar components
 * Created: November 2025
 * 
 * Features:
 * - Slide-in from top/bottom animations
 * - Auto-dismiss with progress bar
 * - Icon animations
 * - Success/error/info/warning variants
 * - Custom duration and actions
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Toast message types
 */
enum class ToastType {
    Success,
    Error,
    Info,
    Warning
}

/**
 * Toast position
 */
enum class ToastPosition {
    Top,
    Bottom
}

/**
 * Animated toast message
 */
@Composable
fun AnimatedToast(
    message: String,
    type: ToastType = ToastType.Info,
    position: ToastPosition = ToastPosition.Bottom,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    duration: Long = 3000L,
    showProgress: Boolean = true,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    val (icon, containerColor, contentColor) = when (type) {
        ToastType.Success -> Triple(
            Icons.Default.Check,
            Color(0xFF4CAF50),
            Color.White
        )
        ToastType.Error -> Triple(
            Icons.Default.Close,
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.onError
        )
        ToastType.Info -> Triple(
            Icons.Default.Info,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        ToastType.Warning -> Triple(
            Icons.Default.Warning,
            Color(0xFFFFA726),
            Color.White
        )
    }

    var progress by remember { mutableStateOf(1f) }

    LaunchedEffect(visible) {
        if (visible) {
            progress = 1f
            val startTime = System.currentTimeMillis()
            while (progress > 0 && visible) {
                val elapsed = System.currentTimeMillis() - startTime
                progress = 1f - (elapsed.toFloat() / duration)
                kotlinx.coroutines.delay(16) // ~60fps
            }
            if (visible) {
                onDismiss()
            }
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = when (position) {
            ToastPosition.Top -> slideInVertically(
                initialOffsetY = { -it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn()
            ToastPosition.Bottom -> slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn()
        },
        exit = when (position) {
            ToastPosition.Top -> slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeOut()
            ToastPosition.Bottom -> slideOutVertically(
                targetOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeOut()
        },
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(12.dp),
            color = containerColor,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Animated icon
                    val iconScale by animateFloatAsState(
                        targetValue = if (visible) 1f else 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "icon_scale"
                    )

                    Icon(
                        imageVector = icon,
                        contentDescription = type.name,
                        tint = contentColor,
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer {
                                scaleX = iconScale
                                scaleY = iconScale
                            }
                    )

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor,
                        modifier = Modifier.weight(1f)
                    )

                    // Action button
                    if (actionLabel != null && onAction != null) {
                        TextButton(
                            onClick = {
                                onAction()
                                onDismiss()
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = contentColor
                            )
                        ) {
                            Text(actionLabel)
                        }
                    }

                    // Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = contentColor.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Progress bar
                if (showProgress) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = contentColor,
                        trackColor = contentColor.copy(alpha = 0.3f),
                    )
                }
            }
        }
    }
}

/**
 * Compact toast variant
 */
@Composable
fun CompactToast(
    message: String,
    type: ToastType = ToastType.Info,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    duration: Long = 2000L
) {
    val (icon, containerColor, contentColor) = when (type) {
        ToastType.Success -> Triple(
            Icons.Default.Check,
            Color(0xFF4CAF50),
            Color.White
        )
        ToastType.Error -> Triple(
            Icons.Default.Close,
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.onError
        )
        ToastType.Info -> Triple(
            Icons.Default.Info,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        ToastType.Warning -> Triple(
            Icons.Default.Warning,
            Color(0xFFFFA726),
            Color.White
        )
    }

    LaunchedEffect(visible) {
        if (visible) {
            kotlinx.coroutines.delay(duration)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = scaleOut(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = containerColor,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = type.name,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor
                )
            }
        }
    }
}

/**
 * Snackbar with custom animations
 */
@Composable
fun AnimatedSnackbar(
    message: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    duration: Long = 4000L,
    withDismissAction: Boolean = true
) {
    LaunchedEffect(visible) {
        if (visible && actionLabel == null) {
            kotlinx.coroutines.delay(duration)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeOut(),
        modifier = modifier
    ) {
        Snackbar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            action = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (actionLabel != null && onAction != null) {
                        TextButton(onClick = {
                            onAction()
                            onDismiss()
                        }) {
                            Text(actionLabel)
                        }
                    }
                    if (withDismissAction) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(message)
        }
    }
}

/**
 * Toast manager state
 */
@Stable
class ToastState {
    var message by mutableStateOf("")
        private set
    var type by mutableStateOf(ToastType.Info)
        private set
    var visible by mutableStateOf(false)
        private set

    fun show(message: String, type: ToastType = ToastType.Info) {
        this.message = message
        this.type = type
        this.visible = true
    }

    fun dismiss() {
        visible = false
    }
}

@Composable
fun rememberToastState() = remember { ToastState() }

/**
 * Toast host container
 */
@Composable
fun ToastHost(
    state: ToastState,
    modifier: Modifier = Modifier,
    position: ToastPosition = ToastPosition.Bottom
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = when (position) {
            ToastPosition.Top -> Alignment.TopCenter
            ToastPosition.Bottom -> Alignment.BottomCenter
        }
    ) {
        AnimatedToast(
            message = state.message,
            type = state.type,
            position = position,
            visible = state.visible,
            onDismiss = { state.dismiss() }
        )
    }
}
