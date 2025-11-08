/*
 * MultiStepCalculator.kt
 * TAAWIDATY System
 * 
 * Description: Multi-step calculator with animations
 * Created: November 2025
 * 
 * Features:
 * - Progress indicator with smooth transitions
 * - Slide transitions between steps
 * - Shake animation on validation errors
 * - Summary page with glassmorphism
 * - Step navigation with spring physics
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Multi-step calculator container with progress tracking
 */
@Composable
fun MultiStepCalculator(
    steps: List<StepData>,
    currentStep: Int,
    onStepChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showSummary: Boolean = false,
    summaryContent: @Composable () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress indicator
        ProgressStepIndicator(
            steps = steps,
            currentStep = currentStep,
            modifier = Modifier.fillMaxWidth()
        )

        // Step content with slide animation
        AnimatedContent(
            targetState = if (showSummary) -1 else currentStep,
            transitionSpec = {
                if (targetState > initialState || targetState == -1) {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn() togetherWith slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeOut()
                } else {
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn() togetherWith slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeOut()
                }
            },
            label = "step_content"
        ) { step ->
            if (step == -1) {
                summaryContent()
            } else {
                steps.getOrNull(step)?.content?.invoke()
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 0 && !showSummary) {
                OutlinedButton(
                    onClick = { onStepChange(currentStep - 1) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Previous")
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (!showSummary) {
                Button(
                    onClick = { onStepChange(currentStep + 1) },
                    modifier = Modifier.weight(1f),
                    enabled = steps.getOrNull(currentStep)?.isValid?.invoke() ?: false
                ) {
                    Text(if (currentStep < steps.size - 1) "Next" else "Summary")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (currentStep < steps.size - 1) {
                            Icons.Default.ArrowForward
                        } else {
                            Icons.Default.Check
                        },
                        contentDescription = "Next",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Progress step indicator with animated transitions
 */
@Composable
fun ProgressStepIndicator(
    steps: List<StepData>,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            // Step circle
            StepCircle(
                stepNumber = index + 1,
                label = step.title,
                isActive = index == currentStep,
                isCompleted = index < currentStep,
                modifier = Modifier.weight(1f)
            )

            // Connecting line
            if (index < steps.size - 1) {
                StepConnector(
                    isCompleted = index < currentStep,
                    modifier = Modifier.width(24.dp)
                )
            }
        }
    }
}

/**
 * Individual step circle with animation
 */
@Composable
fun StepCircle(
    stepNumber: Int,
    label: String,
    isActive: Boolean,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "circle_scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isCompleted -> MaterialTheme.colorScheme.primary
            isActive -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(300),
        label = "circle_color"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            isCompleted -> MaterialTheme.colorScheme.onPrimary
            isActive -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(300),
        label = "content_color"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .animateContentSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = isCompleted,
                transitionSpec = {
                    scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn() togetherWith
                            scaleOut(spring(Spring.DampingRatioNoBouncy)) + fadeOut()
                },
                label = "step_icon"
            ) { completed ->
                if (completed) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = stepNumber.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
            }
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive || isCompleted) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * Connector line between steps
 */
@Composable
fun StepConnector(
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val width by animateDpAsState(
        targetValue = if (isCompleted) 24.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "connector_width"
    )

    Box(
        modifier = modifier
            .height(2.dp)
            .background(
                color = if (isCompleted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                shape = RoundedCornerShape(1.dp)
            )
    )
}

/**
 * Shake animation for validation errors
 */
@Composable
fun ShakeAnimation(
    trigger: Boolean,
    content: @Composable () -> Unit
) {
    var shakeOffset by remember { mutableStateOf(0f) }

    LaunchedEffect(trigger) {
        if (trigger) {
            val shakeKeyframes = listOf(0f, -10f, 10f, -10f, 10f, -5f, 5f, 0f)
            shakeKeyframes.forEach { offset ->
                shakeOffset = offset
                kotlinx.coroutines.delay(50)
            }
            shakeOffset = 0f
        }
    }

    Box(
        modifier = Modifier.offset(x = shakeOffset.dp)
    ) {
        content()
    }
}

/**
 * Validation error message with animation
 */
@Composable
fun AnimatedErrorMessage(
    message: String?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = message != null,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.errorContainer
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚠️",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = message ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

/**
 * Summary card with glassmorphism
 */
@Composable
fun SummaryCard(
    title: String,
    items: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit = {},
    onEdit: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        modifier = modifier
    ) {
        GlassmorphismCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = GlassElevation.Medium
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                HorizontalDivider()

                items.forEachIndexed { index, (label, value) ->
                    val itemDelay = (index * 50).toLong()
                    var itemVisible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(itemDelay)
                        itemVisible = true
                    }

                    AnimatedVisibility(
                        visible = itemVisible,
                        enter = fadeIn() + slideInHorizontally(
                            initialOffsetX = { -it / 4 }
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Edit")
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

/**
 * Data class for calculator steps
 */
data class StepData(
    val title: String,
    val content: @Composable () -> Unit,
    val isValid: () -> Boolean = { true }
)
