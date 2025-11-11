/*
 * EmptyStateAnimations.kt
 * TAAWIDATY System
 * 
 * Description: Animated empty state components
 * Created: November 2025
 * 
 * Features:
 * - Fade-in empty illustrations
 * - Pulsing CTA buttons
 * - Floating icon animation
 * - Staggered content reveal
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Animated empty state with floating icon
 */
@Composable
fun AnimatedEmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(600)) + scaleIn(
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Floating icon
            FloatingIcon(icon = icon)

            Spacer(modifier = Modifier.height(8.dp))

            // Title with stagger
            var titleVisible by remember { mutableStateOf(false) }
            LaunchedEffect(visible) {
                if (visible) {
                    kotlinx.coroutines.delay(200)
                    titleVisible = true
                }
            }

            AnimatedVisibility(
                visible = titleVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Description with stagger
            var descVisible by remember { mutableStateOf(false) }
            LaunchedEffect(titleVisible) {
                if (titleVisible) {
                    kotlinx.coroutines.delay(150)
                    descVisible = true
                }
            }

            AnimatedVisibility(
                visible = descVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Action button with stagger
            if (actionLabel != null && onAction != null) {
                var buttonVisible by remember { mutableStateOf(false) }
                LaunchedEffect(descVisible) {
                    if (descVisible) {
                        kotlinx.coroutines.delay(150)
                        buttonVisible = true
                    }
                }

                AnimatedVisibility(
                    visible = buttonVisible,
                    enter = fadeIn() + scaleIn(
                        spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                ) {
                    PulsingActionButton(
                        label = actionLabel,
                        onClick = onAction
                    )
                }
            }
        }
    }
}

/**
 * Floating icon with vertical motion
 */
@Composable
private fun FloatingIcon(icon: ImageVector) {
    val infiniteTransition = rememberInfiniteTransition(label = "float")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_offset"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_scale"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .graphicsLayer {
                translationY = offsetY
                scaleX = scale
                scaleY = scale
            }
            .background(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * Pulsing action button
 */
@Composable
private fun PulsingActionButton(
    label: String,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "button_pulse"
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(top = 8.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Text(text = label)
    }
}

/**
 * Empty list state with illustration
 */
@Composable
fun EmptyListState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Search
) {
    AnimatedEmptyState(
        icon = icon,
        title = title,
        description = description,
        modifier = modifier
    )
}

/**
 * No results found state
 */
@Composable
fun NoResultsState(
    query: String,
    modifier: Modifier = Modifier,
    onClearSearch: () -> Unit
) {
    AnimatedEmptyState(
        icon = Icons.Default.Search,
        title = "No Results Found",
        description = "We couldn't find anything for \"$query\".\nTry adjusting your search.",
        actionLabel = "Clear Search",
        onAction = onClearSearch,
        modifier = modifier
    )
}

/**
 * Empty medication list
 */
@Composable
fun EmptyMedicationState(
    onAddMedication: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedEmptyState(
        icon = Icons.Default.Add,
        title = "No Medications Yet",
        description = "Start tracking your medications by adding your first prescription.",
        actionLabel = "Add Medication",
        onAction = onAddMedication,
        modifier = modifier
    )
}

/**
 * Connection error state
 */
@Composable
fun ConnectionErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedEmptyState(
        icon = Icons.Default.Warning,
        title = "Connection Error",
        description = "Unable to load data. Please check your internet connection.",
        actionLabel = "Retry",
        onAction = onRetry,
        modifier = modifier
    )
}
