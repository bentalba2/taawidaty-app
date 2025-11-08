/*
 * AnimatedLoadingStates.kt
 * TAAWIDATY System
 * 
 * Description: Animated loading components with shimmer and skeleton screens
 * Created: November 2025
 * 
 * Features:
 * - Shimmer effect for loading cards
 * - Skeleton screen placeholders
 * - Pulsing indicators
 * - Smooth fade-in when loaded
 * - Multiple loading variants
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Shimmer effect composable
 */
@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = colors,
        start = Offset(translateAnim - 1000f, translateAnim - 1000f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier.background(brush)
    )
}

/**
 * Shimmer card placeholder
 */
@Composable
fun ShimmerCard(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(12.dp)
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        ShimmerEffect(modifier = Modifier.fillMaxSize())
    }
}

/**
 * Medication card skeleton
 */
@Composable
fun MedicationCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon skeleton
            ShimmerEffect(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title skeleton
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                // Subtitle skeleton
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }

            // Trailing skeleton
            ShimmerEffect(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
            )
        }
    }
}

/**
 * List skeleton with multiple cards
 */
@Composable
fun ListSkeleton(
    itemCount: Int = 3,
    modifier: Modifier = Modifier,
    itemSkeleton: @Composable (Int) -> Unit = { MedicationCardSkeleton() }
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(itemCount) { index ->
            var visible by remember { mutableStateOf(false) }
            
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay((index * 100).toLong())
                visible = true
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(300)) +
                        expandVertically(animationSpec = tween(300))
            ) {
                itemSkeleton(index)
            }
        }
    }
}

/**
 * Pulsing loading indicator
 */
@Composable
fun PulsingDot(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    minAlpha: Float = 0.3f,
    maxAlpha: Float = 1f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}

/**
 * Three pulsing dots indicator
 */
@Composable
fun PulsingDotsIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "pulse_$index")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 800,
                        delayMillis = index * 200,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse_alpha_$index"
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = alpha))
            )
        }
    }
}

/**
 * Circular loading indicator with rotation
 */
@Composable
fun RotatingLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_angle"
    )

    Box(
        modifier = modifier
            .size(48.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            color = color,
            strokeWidth = 3.dp
        )
    }
}

/**
 * Content with loading state
 */
@Composable
fun <T> LoadingContent(
    isLoading: Boolean,
    data: T?,
    modifier: Modifier = Modifier,
    loadingContent: @Composable () -> Unit = { 
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    },
    errorContent: @Composable (String?) -> Unit = { message ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message ?: "An error occurred",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    },
    emptyContent: @Composable () -> Unit = {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No data available",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    },
    content: @Composable (T) -> Unit
) {
    Box(modifier = modifier) {
        AnimatedContent(
            targetState = when {
                isLoading -> LoadingState.Loading
                data != null -> LoadingState.Success
                else -> LoadingState.Empty
            },
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            },
            label = "loading_content"
        ) { state ->
            when (state) {
                LoadingState.Loading -> loadingContent()
                LoadingState.Success -> data?.let { content(it) }
                LoadingState.Empty -> emptyContent()
            }
        }
    }
}

/**
 * Profile skeleton
 */
@Composable
fun ProfileSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Avatar skeleton
        ShimmerEffect(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )

        // Name skeleton
        ShimmerEffect(
            modifier = Modifier
                .width(150.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        // Email skeleton
        ShimmerEffect(
            modifier = Modifier
                .width(200.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stats skeletons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(3) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShimmerEffect(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    ShimmerEffect(
                        modifier = Modifier
                            .width(60.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}

/**
 * Fade-in content when loaded
 */
@Composable
fun FadeInContent(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Loading state enum
 */
private enum class LoadingState {
    Loading,
    Success,
    Empty
}
