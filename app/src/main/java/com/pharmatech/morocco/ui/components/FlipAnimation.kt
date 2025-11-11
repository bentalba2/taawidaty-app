/*
 * FlipAnimation.kt
 * TAAWIDATY System
 * 
 * Description: 3D flip animations for cards with perspective
 * Created: November 2025
 * 
 * Features:
 * - 3D flip effect for medication cards
 * - Smooth rotation with perspective
 * - Front/back face visibility
 * - Click or swipe to flip
 * - Spring physics for natural motion
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Flip state for card
 */
enum class FlipState {
    Front,
    Back
}

/**
 * Flippable card with 3D rotation
 */
@Composable
fun FlipCard(
    modifier: Modifier = Modifier,
    flipState: FlipState = FlipState.Front,
    onFlip: (FlipState) -> Unit = {},
    flipOnClick: Boolean = true,
    flipOnSwipe: Boolean = false,
    animationDuration: Int = 600,
    front: @Composable () -> Unit,
    back: @Composable () -> Unit
) {
    var currentState by remember { mutableStateOf(flipState) }
    
    // Animate rotation from 0 to 180 degrees
    val rotation by animateFloatAsState(
        targetValue = if (currentState == FlipState.Back) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_rotation"
    )

    val flipModifier = modifier.then(
        if (flipOnClick) {
            Modifier.clickable {
                currentState = if (currentState == FlipState.Front) {
                    FlipState.Back
                } else {
                    FlipState.Front
                }
                onFlip(currentState)
            }
        } else if (flipOnSwipe) {
            var offsetX by remember { mutableStateOf(0f) }
            Modifier.pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (abs(offsetX) > 100) {
                            currentState = if (currentState == FlipState.Front) {
                                FlipState.Back
                            } else {
                                FlipState.Front
                            }
                            onFlip(currentState)
                        }
                        offsetX = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX += dragAmount
                    }
                )
            }
        } else {
            Modifier
        }
    )

    LaunchedEffect(flipState) {
        currentState = flipState
    }

    Box(modifier = flipModifier) {
        // Front face
        Box(
            modifier = Modifier.graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
        ) {
            if (rotation <= 90f) {
                front()
            }
        }

        // Back face
        Box(
            modifier = Modifier.graphicsLayer {
                rotationY = rotation - 180f
                cameraDistance = 12f * density
            }
        ) {
            if (rotation > 90f) {
                back()
            }
        }
    }
}

/**
 * Medication card with flip animation
 */
@Composable
fun FlippableMedicationCard(
    medicationName: String,
    dosage: String,
    frequency: String,
    nextDose: String,
    notes: String?,
    instructions: String?,
    sideEffects: String?,
    modifier: Modifier = Modifier,
    isFlipped: Boolean = false,
    onFlip: (Boolean) -> Unit = {}
) {
    var flipState by remember { mutableStateOf(if (isFlipped) FlipState.Back else FlipState.Front) }

    FlipCard(
        modifier = modifier,
        flipState = flipState,
        onFlip = { state ->
            flipState = state
            onFlip(state == FlipState.Back)
        },
        flipOnClick = true,
        front = {
            MedicationCardFront(
                medicationName = medicationName,
                dosage = dosage,
                frequency = frequency,
                nextDose = nextDose
            )
        },
        back = {
            MedicationCardBack(
                medicationName = medicationName,
                notes = notes,
                instructions = instructions,
                sideEffects = sideEffects
            )
        }
    )
}

/**
 * Front face of medication card
 */
@Composable
private fun MedicationCardFront(
    medicationName: String,
    dosage: String,
    frequency: String,
    nextDose: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = medicationName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Tap to flip",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

            InfoRow(label = "Dosage", value = dosage)
            InfoRow(label = "Frequency", value = frequency)
            InfoRow(label = "Next Dose", value = nextDose)
        }
    }
}

/**
 * Back face of medication card
 */
@Composable
private fun MedicationCardBack(
    medicationName: String,
    notes: String?,
    instructions: String?,
    sideEffects: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = medicationName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Details",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))

            notes?.let {
                DetailSection(title = "Notes", content = it)
            }

            instructions?.let {
                DetailSection(title = "Instructions", content = it)
            }

            sideEffects?.let {
                DetailSection(title = "Side Effects", content = it)
            }

            if (notes == null && instructions == null && sideEffects == null) {
                Text(
                    text = "No additional details available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Info row for card front
 */
@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * Detail section for card back
 */
@Composable
private fun DetailSection(
    title: String,
    content: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

/**
 * Simple flip card for general use
 */
@Composable
fun SimpleFlipCard(
    modifier: Modifier = Modifier,
    isFlipped: Boolean = false,
    onFlipChange: (Boolean) -> Unit = {},
    frontContent: @Composable BoxScope.() -> Unit,
    backContent: @Composable BoxScope.() -> Unit
) {
    var currentlyFlipped by remember { mutableStateOf(isFlipped) }

    val rotation by animateFloatAsState(
        targetValue = if (currentlyFlipped) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "simple_flip"
    )

    LaunchedEffect(isFlipped) {
        currentlyFlipped = isFlipped
    }

    Box(
        modifier = modifier.clickable {
            currentlyFlipped = !currentlyFlipped
            onFlipChange(currentlyFlipped)
        }
    ) {
        // Front
        Box(
            modifier = Modifier.graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
        ) {
            if (rotation <= 90f) {
                Box(content = frontContent)
            }
        }

        // Back
        Box(
            modifier = Modifier.graphicsLayer {
                rotationY = rotation - 180f
                cameraDistance = 12f * density
            }
        ) {
            if (rotation > 90f) {
                Box(content = backContent)
            }
        }
    }
}

/**
 * Flip card with vertical rotation
 */
@Composable
fun VerticalFlipCard(
    modifier: Modifier = Modifier,
    isFlipped: Boolean = false,
    onFlipChange: (Boolean) -> Unit = {},
    frontContent: @Composable BoxScope.() -> Unit,
    backContent: @Composable BoxScope.() -> Unit
) {
    var currentlyFlipped by remember { mutableStateOf(isFlipped) }

    val rotation by animateFloatAsState(
        targetValue = if (currentlyFlipped) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "vertical_flip"
    )

    LaunchedEffect(isFlipped) {
        currentlyFlipped = isFlipped
    }

    Box(
        modifier = modifier.clickable {
            currentlyFlipped = !currentlyFlipped
            onFlipChange(currentlyFlipped)
        }
    ) {
        // Front
        Box(
            modifier = Modifier.graphicsLayer {
                rotationX = rotation
                cameraDistance = 12f * density
            }
        ) {
            if (rotation <= 90f) {
                Box(content = frontContent)
            }
        }

        // Back
        Box(
            modifier = Modifier.graphicsLayer {
                rotationX = rotation - 180f
                cameraDistance = 12f * density
            }
        ) {
            if (rotation > 90f) {
                Box(content = backContent)
            }
        }
    }
}
