/*
 * FinalPolishAnimations.kt
 * TAAWIDATY System
 * 
 * Description: Final polish animations and micro-interactions
 * Created: November 2025
 * 
 * Features:
 * - Haptic feedback helpers
 * - Skeleton loading optimization
 * - Stagger utilities
 * - Animation presets
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Animation preset configurations
 */
object AnimationPresets {
    // Spring physics
    val bouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val smooth = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val gentle = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    // Tween animations
    val quickFade = tween<Float>(durationMillis = 150)
    val normalFade = tween<Float>(durationMillis = 300)
    val slowFade = tween<Float>(durationMillis = 500)

    // Enter/Exit transitions
    val slideInFromBottom: EnterTransition = slideInVertically(
        initialOffsetY = { it },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeIn(normalFade)

    val slideOutToBottom: ExitTransition = slideOutVertically(
        targetOffsetY = { it },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeOut(quickFade)

    val scaleInBouncy: EnterTransition = scaleIn(
        spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeIn(normalFade)
    
    val scaleOutQuick: ExitTransition = scaleOut(quickFade) + fadeOut(quickFade)
}

/**
 * Staggered list animation helper
 */
@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.staggeredItems(
    items: List<Any>,
    staggerDelayMs: Long = 30,
    key: ((Any) -> Any)? = null,
    itemContent: @Composable (Any) -> Unit
) {
    items(
        items = items,
        key = key
    ) { item ->
        val index = items.indexOf(item)
        var visible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(index * staggerDelayMs)
            visible = true
        }

        AnimatedVisibility(
            visible = visible,
            enter = AnimationPresets.slideInFromBottom,
            modifier = Modifier.animateItemPlacement()
        ) {
            itemContent(item)
        }
    }
}

/**
 * Shimmer loading effect (optimized)
 */
@Composable
fun ShimmerLoading(
    modifier: Modifier = Modifier,
    shimmerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    highlightColor: Color = MaterialTheme.colorScheme.surface
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    Box(
        modifier = modifier
            .background(shimmerColor)
            .shimmerEffect(offset, highlightColor)
    )
}

/**
 * Shimmer effect modifier
 */
private fun Modifier.shimmerEffect(offset: Float, highlightColor: Color): Modifier {
    return this.background(
        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                highlightColor.copy(alpha = 0.3f),
                Color.Transparent
            ),
            startX = offset - 200f,
            endX = offset + 200f
        )
    )
}

/**
 * Content skeleton for lists
 */
@Composable
fun ListItemSkeleton(
    modifier: Modifier = Modifier,
    showIcon: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (showIcon) {
            ShimmerLoading(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerLoading(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            ShimmerLoading(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}

/**
 * Card skeleton
 */
@Composable
fun CardSkeleton(
    modifier: Modifier = Modifier,
    height: Dp = 120.dp
) {
    Card(
        modifier = modifier.height(height),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShimmerLoading(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            ShimmerLoading(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            ShimmerLoading(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}

/**
 * Bounce click effect
 */
@Composable
fun Modifier.bounceClick(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier {
    var isPressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "bounce_scale"
    )

    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(enabled = enabled) {
            isPressed = true
            onClick()
            // Reset after click
            scope.launch {
                delay(100)
                isPressed = false
            }
        }
}

/**
 * Fade-in when visible
 */
@Composable
fun FadeInBox(
    delayMs: Long = 0,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delayMs)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(AnimationPresets.normalFade) + scaleIn(AnimationPresets.bouncy),
        modifier = modifier
    ) {
        Box(content = content)
    }
}

/**
 * Loading dots indicator
 */
@Composable
fun LoadingDots(
    modifier: Modifier = Modifier,
    dotCount: Int = 3,
    dotSize: Dp = 8.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(dotCount) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "dot_$index")

            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 600,
                        delayMillis = index * 200,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_alpha_$index"
            )

            Box(
                modifier = Modifier
                    .size(dotSize)
                    .background(color.copy(alpha = alpha), androidx.compose.foundation.shape.CircleShape)
            )
        }
    }
}

/**
 * Crossfade between content states
 */
@Composable
fun <T> AnimatedContentCrossfade(
    targetState: T,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            fadeIn(AnimationPresets.normalFade) togetherWith
                    fadeOut(AnimationPresets.quickFade)
        },
        modifier = modifier,
        label = "crossfade"
    ) { state ->
        content(state)
    }
}

/**
 * Slide-in text
 */
@Composable
fun SlideInText(
    text: String,
    delayMs: Long = 0,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(text) {
        visible = false
        delay(delayMs)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { -it / 4 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(tween(300))
    ) {
        Text(
            text = text,
            style = style,
            modifier = modifier
        )
    }
}
