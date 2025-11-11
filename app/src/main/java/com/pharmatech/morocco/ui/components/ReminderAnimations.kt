/*
 * ReminderAnimations.kt
 * TAAWIDATY System
 * 
 * Description: Medication reminder pulse animations
 * Created: November 2025
 * 
 * Features:
 * - Pulsing reminder card
 * - Bell shake animation
 * - Urgent attention pulse
 * - Dismiss swipe animation
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.sin

/**
 * Pulsing reminder card
 */
@Composable
fun PulsingReminderCard(
    medicationName: String,
    dosage: String,
    time: String,
    isOverdue: Boolean,
    onTake: () -> Unit,
    onSnooze: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "reminder_pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isOverdue) 1.02f else 1.01f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isOverdue) 800 else 1500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "card_scale"
    )

    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isOverdue) 1f else 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isOverdue) 800 else 1500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "border_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bell icon with shake
                ShakingBellIcon(isOverdue = isOverdue)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medicationName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isOverdue) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                    Text(
                        text = "$dosage • $time",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isOverdue) {
                            MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        }
                    )
                }

                if (isOverdue) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .alpha(borderAlpha)
                            .background(
                                color = MaterialTheme.colorScheme.error,
                                shape = CircleShape
                            )
                    )
                }
            }

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onSnooze,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Snooze",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Snooze")
                }

                Button(
                    onClick = onTake,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Take",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Take")
                }
            }
        }
    }
}

/**
 * Shaking bell icon
 */
@Composable
private fun ShakingBellIcon(isOverdue: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "bell_shake")

    val rotation by infiniteTransition.animateFloat(
        initialValue = if (isOverdue) -15f else 0f,
        targetValue = if (isOverdue) 15f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isOverdue) 200 else 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bell_rotation"
    )

    Icon(
        imageVector = Icons.Default.Notifications,
        contentDescription = "Reminder",
        modifier = Modifier
            .size(40.dp)
            .graphicsLayer {
                rotationZ = rotation
            },
        tint = if (isOverdue) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.primary
        }
    )
}

/**
 * Reminder notification badge
 */
@Composable
fun ReminderBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = count > 0,
        enter = scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "badge_pulse")

        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "badge_scale"
        )

        Badge(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            containerColor = MaterialTheme.colorScheme.error
        ) {
            Text(
                text = if (count > 9) "9+" else count.toString(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Upcoming reminder list item
 */
@Composable
fun UpcomingReminderItem(
    medicationName: String,
    time: String,
    dosage: String,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(50)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn()
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medicationName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dosage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                Text(
                    text = time,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Snooze options bottom sheet
 */
@Composable
fun SnoozeOptionsSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSnooze: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val snoozeOptions = listOf(
        5 to "5 minutes",
        15 to "15 minutes",
        30 to "30 minutes",
        60 to "1 hour"
    )

    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Snooze for",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                snoozeOptions.forEachIndexed { index, (minutes, label) ->
                    var visible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay((index * 50).toLong())
                        visible = true
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInHorizontally(
                            initialOffsetX = { it }
                        ) + fadeIn()
                    ) {
                        OutlinedButton(
                            onClick = {
                                onSnooze(minutes)
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
