/*
 * SuccessAnimations.kt
 * TAAWIDATY System
 * 
 * Description: Success confirmation animations
 * Created: November 2025
 * 
 * Features:
 * - Checkmark drawing animation
 * - Success ripple effect
 * - Confetti celebration
 * - Scale-in confirmation
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated success checkmark
 */
@Composable
fun AnimatedSuccessCheckmark(
    visible: Boolean,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 80.dp
) {
    var animationPlayed by remember { mutableStateOf(false) }

    LaunchedEffect(visible) {
        if (visible && !animationPlayed) {
            animationPlayed = true
        }
    }

    val progress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "checkmark_progress"
    )

    val scale by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "checkmark_scale"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Background circle
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .scale(scale)
        ) {
            val radius = size.toPx() / 2

            // Circle background
            drawCircle(
                color = Color(0xFF4CAF50),
                radius = radius * progress,
                center = center
            )

            // Checkmark path
            if (progress > 0.3f) {
                val checkProgress = ((progress - 0.3f) / 0.7f).coerceIn(0f, 1f)

                val path = Path().apply {
                    val startX = center.x - radius * 0.3f
                    val startY = center.y
                    val midX = center.x - radius * 0.1f
                    val midY = center.y + radius * 0.25f
                    val endX = center.x + radius * 0.35f
                    val endY = center.y - radius * 0.3f

                    moveTo(startX, startY)

                    if (checkProgress < 0.5f) {
                        val t = checkProgress * 2
                        lineTo(
                            startX + (midX - startX) * t,
                            startY + (midY - startY) * t
                        )
                    } else {
                        lineTo(midX, midY)
                        val t = (checkProgress - 0.5f) * 2
                        lineTo(
                            midX + (endX - midX) * t,
                            midY + (endY - midY) * t
                        )
                    }
                }

                drawPath(
                    path = path,
                    color = Color.White,
                    style = Stroke(
                        width = radius * 0.15f,
                        cap = StrokeCap.Round
                    )
                )
            }
        }
    }
}

/**
 * Success dialog with animation
 */
@Composable
fun SuccessDialog(
    visible: Boolean,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ),
        exit = fadeOut() + scaleOut()
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            },
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedSuccessCheckmark(
                        visible = visible,
                        size = 64.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            modifier = modifier
        )
    }
}

/**
 * Success ripple effect
 */
@Composable
fun SuccessRippleEffect(
    trigger: Boolean,
    modifier: Modifier = Modifier
) {
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(trigger) {
        if (trigger) {
            isAnimating = true
            kotlinx.coroutines.delay(2000)
            isAnimating = false
        }
    }

    if (isAnimating) {
        val infiniteTransition = rememberInfiniteTransition(label = "ripple")

        val scale1 by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 2f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "ripple1"
        )

        val alpha1 by infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "alpha1"
        )

        Box(
            modifier = modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale1)
                    .alpha(alpha1)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * Medication added success
 */
@Composable
fun MedicationAddedSuccess(
    visible: Boolean,
    medicationName: String,
    onDismiss: () -> Unit
) {
    SuccessDialog(
        visible = visible,
        title = "Medication Added",
        message = "$medicationName has been successfully added to your tracker.",
        onDismiss = onDismiss
    )
}

/**
 * Dose taken success
 */
@Composable
fun DoseTakenSuccess(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it }
        ) + fadeOut(),
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Dose marked as taken",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }

    LaunchedEffect(visible) {
        if (visible) {
            kotlinx.coroutines.delay(3000)
            onDismiss()
        }
    }
}

/**
 * Achievement unlocked animation
 */
@Composable
fun AchievementUnlocked(
    visible: Boolean,
    title: String,
    description: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it }
        ) + fadeIn() + scaleIn(
            spring(Spring.DampingRatioMediumBouncy)
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it }
        ) + fadeOut(),
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🏆",
                    style = MaterialTheme.typography.displayMedium
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }

    LaunchedEffect(visible) {
        if (visible) {
            kotlinx.coroutines.delay(4000)
            onDismiss()
        }
    }
}
