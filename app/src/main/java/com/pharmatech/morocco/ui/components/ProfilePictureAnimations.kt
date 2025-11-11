/*
 * ProfilePictureAnimations.kt
 * TAAWIDATY System
 * 
 * Description: Animated profile picture components
 * Created: November 2025
 * 
 * Features:
 * - Zoom animation on tap
 * - Border pulse when selected
 * - Upload progress ring
 * - Avatar transitions
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Animated profile picture with click zoom
 */
@Composable
fun AnimatedProfilePicture(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    content: @Composable BoxScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "profile_scale"
    )

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .clickable {
                isPressed = !isPressed
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Profile picture with pulsing border
 */
@Composable
fun PulsingBorderAvatar(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val borderWidth by infiniteTransition.animateFloat(
        initialValue = if (isSelected) 2f else 0f,
        targetValue = if (isSelected) 4f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "border_pulse"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = if (isSelected) 0.5f else 0f,
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "border_alpha"
    )

    Box(
        modifier = modifier
            .size(size + (borderWidth.dp * 2))
            .border(
                width = borderWidth.dp,
                color = borderColor.copy(alpha = alpha),
                shape = CircleShape
            )
            .padding(borderWidth.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

/**
 * Profile picture with upload progress
 */
@Composable
fun ProfilePictureWithProgress(
    uploadProgress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = uploadProgress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "upload_progress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Progress ring
        AnimatedProgressRing(
            progress = animatedProgress,
            size = size,
            strokeWidth = 4.dp,
            showPercentage = false
        )

        // Avatar
        Box(
            modifier = Modifier
                .size(size - 16.dp)
                .clip(CircleShape)
                .graphicsLayer {
                    alpha = if (uploadProgress < 1f) 0.5f else 1f
                },
            contentAlignment = Alignment.Center
        ) {
            content()
        }

        // Upload complete checkmark
        AnimatedVisibility(
            visible = uploadProgress >= 1f,
            enter = scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Upload complete",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Avatar group with overlap
 */
@Composable
fun AvatarGroup(
    avatarCount: Int,
    maxVisible: Int = 3,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    Row(modifier = modifier) {
        val visible = minOf(avatarCount, maxVisible)
        
        repeat(visible) { index ->
            val delay = (index * 50).toLong()
            var isVisible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(delay)
                isVisible = true
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .offset(x = (-8).dp * index)
                        .size(size)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        if (avatarCount > maxVisible) {
            Box(
                modifier = Modifier
                    .offset(x = (-8).dp * visible)
                    .size(size)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+${avatarCount - maxVisible}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
