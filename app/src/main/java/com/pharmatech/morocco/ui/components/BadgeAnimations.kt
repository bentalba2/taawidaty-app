/*
 * BadgeAnimations.kt
 * TAAWIDATY System
 * 
 * Description: Animated badge and notification components
 * Created: November 2025
 * 
 * Features:
 * - Bounce animation on new notification
 * - Pulsing badge
 * - Scale-in when count increases
 * - Animated badge appearances
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Animated notification badge
 */
@Composable
fun AnimatedNotificationBadge(
    count: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.error,
    contentColor: Color = MaterialTheme.colorScheme.onError
) {
    var previousCount by remember { mutableStateOf(count) }
    var shouldAnimate by remember { mutableStateOf(false) }

    LaunchedEffect(count) {
        if (count > previousCount) {
            shouldAnimate = true
            kotlinx.coroutines.delay(500)
            shouldAnimate = false
        }
        previousCount = count
    }

    val scale by animateFloatAsState(
        targetValue = if (shouldAnimate) 1.3f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "badge_scale"
    )

    AnimatedVisibility(
        visible = count > 0,
        enter = scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn(),
        exit = scaleOut(spring(Spring.DampingRatioNoBouncy)) + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(CircleShape)
                .background(backgroundColor)
                .padding(horizontal = 6.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

/**
 * Pulsing badge
 */
@Composable
fun PulsingBadge(
    visible: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.error
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(CircleShape)
                .background(color)
        )
    }
}

/**
 * Badge with icon
 */
@Composable
fun IconBadge(
    count: Int,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        icon()
        
        if (count > 0) {
            AnimatedNotificationBadge(
                count = count,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

/**
 * Bounce badge on appear
 */
@Composable
fun BounceBadge(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
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
        content()
    }
}
