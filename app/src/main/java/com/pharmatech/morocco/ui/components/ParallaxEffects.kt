/*
 * ParallaxEffects.kt
 * TAAWIDATY System
 * 
 * Description: Parallax scroll effects and reveal animations
 * Created: November 2025
 * 
 * Features:
 * - Header parallax on scroll
 * - Scale animations on scroll
 * - Reveal animations for sections
 * - Sticky header effects
 * - Smooth scroll-based transitions
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

/**
 * Parallax header that moves slower than scroll
 */
@Composable
fun ParallaxHeader(
    scrollState: ScrollState,
    headerHeight: Dp = 200.dp,
    parallaxFactor: Float = 0.5f,
    content: @Composable BoxScope.() -> Unit
) {
    val offset = scrollState.value * parallaxFactor

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight)
            .graphicsLayer {
                translationY = -offset
            }
    ) {
        content()
    }
}

/**
 * Parallax header for LazyColumn
 */
@Composable
fun LazyParallaxHeader(
    lazyListState: LazyListState,
    headerHeight: Dp = 200.dp,
    parallaxFactor: Float = 0.5f,
    content: @Composable BoxScope.() -> Unit
) {
    val firstVisibleItemScrollOffset = lazyListState.firstVisibleItemScrollOffset
    val offset = firstVisibleItemScrollOffset * parallaxFactor

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight)
            .graphicsLayer {
                translationY = -offset
            }
    ) {
        content()
    }
}

/**
 * Scale header based on scroll
 */
@Composable
fun ScaleHeader(
    scrollState: ScrollState,
    headerHeight: Dp = 200.dp,
    maxScale: Float = 1.2f,
    content: @Composable BoxScope.() -> Unit
) {
    val scrollProgress = min(scrollState.value / 300f, 1f)
    val scale = 1f + (maxScale - 1f) * (1f - scrollProgress)
    val alpha = 1f - (scrollProgress * 0.5f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
    ) {
        content()
    }
}

/**
 * Fade header on scroll
 */
@Composable
fun FadeHeader(
    scrollState: ScrollState,
    headerHeight: Dp = 200.dp,
    fadeThreshold: Float = 300f,
    content: @Composable BoxScope.() -> Unit
) {
    val alpha = 1f - min(scrollState.value / fadeThreshold, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight)
            .alpha(alpha)
    ) {
        content()
    }
}

/**
 * Sticky header with fade-in background
 */
@Composable
fun StickyHeader(
    scrollState: ScrollState,
    threshold: Float = 100f,
    content: @Composable BoxScope.() -> Unit
) {
    val progress = min(scrollState.value / threshold, 1f)
    val backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = progress)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = backgroundColor,
        shadowElevation = (progress * 4).dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            content()
        }
    }
}

/**
 * Reveal animation on scroll
 */
@Composable
fun ScrollReveal(
    scrollState: ScrollState,
    threshold: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isVisible = scrollState.value >= threshold

    var hasBeenVisible by remember { mutableStateOf(false) }
    if (isVisible && !hasBeenVisible) {
        hasBeenVisible = true
    }

    AnimatedVisibility(
        visible = hasBeenVisible,
        enter = fadeIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + slideInVertically(
            initialOffsetY = { it / 2 },
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
 * Scale on scroll reveal
 */
@Composable
fun ScaleReveal(
    scrollState: ScrollState,
    threshold: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isVisible = scrollState.value >= threshold

    var hasBeenVisible by remember { mutableStateOf(false) }
    if (isVisible && !hasBeenVisible) {
        hasBeenVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (hasBeenVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale_reveal"
    )

    val alpha by animateFloatAsState(
        targetValue = if (hasBeenVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "alpha_reveal"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
    ) {
        content()
    }
}

/**
 * Parallax background layer
 */
@Composable
fun ParallaxBackground(
    scrollState: ScrollState,
    speed: Float = 0.3f,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.graphicsLayer {
            translationY = scrollState.value * speed
        }
    ) {
        content()
    }
}

/**
 * Gradient overlay that intensifies on scroll
 */
@Composable
fun ScrollGradientOverlay(
    scrollState: ScrollState,
    maxAlpha: Float = 0.7f,
    threshold: Float = 200f,
    modifier: Modifier = Modifier
) {
    val alpha = min(scrollState.value / threshold, 1f) * maxAlpha

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = alpha),
                        Color.Transparent
                    )
                )
            )
    )
}

/**
 * Collapsing toolbar effect
 */
@Composable
fun CollapsingToolbar(
    scrollState: ScrollState,
    expandedHeight: Dp = 200.dp,
    collapsedHeight: Dp = 64.dp,
    title: String,
    expandedContent: @Composable BoxScope.() -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    val maxScroll = with(androidx.compose.ui.platform.LocalDensity.current) {
        (expandedHeight - collapsedHeight).toPx()
    }
    
    val scrollProgress = min(scrollState.value / maxScroll, 1f)
    val currentHeight = expandedHeight - (expandedHeight - collapsedHeight) * scrollProgress

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(currentHeight),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = (scrollProgress * 4).dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Expanded content (fades out)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(1f - scrollProgress)
            ) {
                expandedContent()
            }

            // Toolbar content (always visible)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(collapsedHeight)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.BottomStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.5f + (scrollProgress * 0.5f)
                    )
                )

                Row(content = actions)
            }
        }
    }
}

/**
 * Staggered reveal for list items based on scroll
 */
@Composable
fun StaggeredScrollReveal(
    index: Int,
    scrollState: ScrollState,
    baseThreshold: Int = 100,
    staggerDelay: Int = 50,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val threshold = baseThreshold + (index * staggerDelay)
    ScrollReveal(
        scrollState = scrollState,
        threshold = threshold,
        modifier = modifier,
        content = content
    )
}

/**
 * Depth parallax - multiple layers at different speeds
 */
@Composable
fun DepthParallax(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    layers: List<Pair<Float, @Composable BoxScope.() -> Unit>>
) {
    Box(modifier = modifier) {
        layers.forEach { (speed, content) ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = scrollState.value * speed
                    }
            ) {
                content()
            }
        }
    }
}

/**
 * Zoom effect on scroll
 */
@Composable
fun ZoomOnScroll(
    scrollState: ScrollState,
    threshold: Float = 300f,
    maxZoom: Float = 1.5f,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val scrollProgress = min(scrollState.value / threshold, 1f)
    val scale = 1f + ((maxZoom - 1f) * scrollProgress)

    Box(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        content()
    }
}
