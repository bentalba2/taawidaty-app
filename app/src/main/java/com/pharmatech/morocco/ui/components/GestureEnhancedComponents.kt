package com.pharmatech.morocco.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Swipeable card with reveal actions
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> SwipeableCard(
    item: T,
    isSwiped: Boolean,
    onSwipe: (T) -> Unit,
    onDelete: (T) -> Unit,
    onEdit: (T) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val width = 320.dp
    val swipeWidth = 100.dp

    Box(
        modifier = modifier
            .width(width)
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        // Background actions
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Edit action
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onEdit(item)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Delete action
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.error,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onDelete(item)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Foreground content
        Surface(
            modifier = Modifier
                .offset { IntOffset(0, 0) }
                .swipeable(
                    state = rememberSwipeableState(isSwiped) { newState ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSwipe(item)
                        newState
                    },
                    anchors = mapOf(
                        0f to false,
                        -with(LocalDensity.current) { swipeWidth.toPx() } to true
                    ),
                    thresholds = { _, _ -> FractionalThreshold(0.5f) },
                    orientation = Orientation.Horizontal
                ),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            content(item)
        }
    }
}

/**
 * Pull to refresh container
 */
@Composable
fun PullToRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val pullThreshold = 100.dp
    var isPulling by remember { mutableStateOf(false) }
    var pullProgress by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = {
                        isPulling = true
                    },
                    onDragEnd = {
                        if (pullProgress >= 1f && !isRefreshing) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onRefresh()
                        }
                        isPulling = false
                        pullProgress = 0f
                    },
                    onVerticalDrag = { change ->
                        if (change.y > 0 && !isRefreshing) {
                            val dragDistance = change.y
                            with(LocalDensity.current) {
                                pullProgress = (dragDistance / pullThreshold.toPx()).coerceIn(0f, 1f)
                            }
                        }
                    }
                )
            }
    ) {
        // Pull to refresh indicator
        if (isPulling || isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                val rotation by animateFloatAsState(
                    targetValue = if (isRefreshing) 360f else pullProgress * 180f,
                    animationSpec = if (isRefreshing) {
                        infiniteRepeatable(
                            animation = tween(1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        )
                    } else {
                        tween(300)
                    },
                    label = "refreshRotation"
                )

                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer {
                            rotationZ = rotation
                        },
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        content()
    }
}

/**
 * Ripple touch surface with better feedback
 */
@Composable
fun RippleTouchSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp),
    color: Color = MaterialTheme.colorScheme.primary,
    content: @Composable BoxScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        color = color,
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(
            bounded = true,
            radius = 200.dp,
            color = Color.White.copy(alpha = 0.3f)
        )
    ) {
        content()
    }
}

/**
 * Expandable card with smooth animation
 */
@Composable
fun ExpandableCard(
    title: String,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    modifier: Modifier = Modifier,
    expandedContent: @Composable ColumnScope.() -> Unit = {},
    collapsedContent: @Composable ColumnScope.() -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val cardHeight by animateDpAsState(
        targetValue = if (isExpanded) 300.dp else 120.dp,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "cardHeight"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "expandRotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onToggleExpanded()
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.graphicsLayer {
                        rotationZ = rotation
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Content
            if (isExpanded) {
                expandedContent()
            } else {
                collapsedContent()
            }
        }
    }
}

/**
 * Gesture-controlled slider with haptic feedback
 */
@Composable
fun HapticSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int? = null,
    onValueChangeFinished: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    var lastValue by remember { mutableStateOf(value) }

    Slider(
        value = value,
        onValueChange = { newValue ->
            onValueChange(newValue)

            // Provide haptic feedback at significant points
            val stepSize = if (steps != null) {
                (valueRange.endInclusive - valueRange.start) / steps
            } else {
                0.1f
            }

            if (kotlin.math.abs(newValue - lastValue) >= stepSize) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                lastValue = newValue
            }
        },
        modifier = modifier,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onValueChangeFinished?.invoke()
        },
        colors = SliderDefaults.colors(
            activeTickColor = MaterialTheme.colorScheme.primary,
            inactiveTickColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            thumbColor = MaterialTheme.colorScheme.primary
        )
    )
}

/**
 * Touch-sensitive image with zoom capability
 */
@Composable
fun ZoomableImage(
    modifier: Modifier = Modifier,
    minScale: Float = 1f,
    maxScale: Float = 3f,
    content: @Composable () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    scale = (scale * zoom).coerceIn(minScale, maxScale)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    if (scale > 1f) {
                        offsetX += change.x
                        offsetY += change.y
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                }
        ) {
            content()
        }
    }
}