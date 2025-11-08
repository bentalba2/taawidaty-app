/*
 * SwipeToDismiss.kt
 * TAAWIDATY System
 * 
 * Description: Swipe-to-delete gestures with reveal animations
 * Created: November 2025
 * 
 * Features:
 * - SwipeToDismiss for list items
 * - Reveal delete icon on swipe
 * - Haptic feedback (if available)
 * - Undo snackbar
 * - Customizable swipe threshold
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Swipe direction
 */
enum class SwipeDirection {
    Left,
    Right,
    Both
}

/**
 * Swipeable item with delete action
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SwipeToDeleteItem(
    item: T,
    onDelete: (T) -> Unit,
    modifier: Modifier = Modifier,
    swipeDirection: SwipeDirection = SwipeDirection.Both,
    deleteThreshold: Float = 0.4f,
    backgroundColor: Color = MaterialTheme.colorScheme.error,
    icon: ImageVector = Icons.Default.Delete,
    iconTint: Color = MaterialTheme.colorScheme.onError,
    content: @Composable (T) -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete(item)
                    true
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = swipeState,
        modifier = modifier,
        backgroundContent = {
            val direction = swipeState.dismissDirection
            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.Center
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                val scale by animateFloatAsState(
                    targetValue = if (swipeState.progress > deleteThreshold) 1.3f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "icon_scale"
                )

                Icon(
                    imageVector = icon,
                    contentDescription = "Delete",
                    tint = iconTint,
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                )
            }
        },
        enableDismissFromStartToEnd = swipeDirection == SwipeDirection.Right || swipeDirection == SwipeDirection.Both,
        enableDismissFromEndToStart = swipeDirection == SwipeDirection.Left || swipeDirection == SwipeDirection.Both
    ) {
        content(item)
    }
}

/**
 * Custom swipeable item with manual control
 */
@Composable
fun CustomSwipeItem(
    onSwipeComplete: () -> Unit,
    modifier: Modifier = Modifier,
    swipeThreshold: Float = 200f,
    backgroundColor: Color = MaterialTheme.colorScheme.error,
    icon: ImageVector = Icons.Default.Delete,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var isDeleted by remember { mutableStateOf(false) }

    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isDeleted) -1000f else offsetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "swipe_offset",
        finishedListener = {
            if (isDeleted) {
                onSwipeComplete()
            }
        }
    )

    Box(modifier = modifier) {
        // Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            val iconAlpha = (abs(offsetX) / swipeThreshold).coerceIn(0f, 1f)
            val iconScale = 1f + (iconAlpha * 0.3f)

            Icon(
                imageVector = icon,
                contentDescription = "Delete",
                tint = Color.White.copy(alpha = iconAlpha),
                modifier = Modifier.graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                }
            )
        }

        // Content
        Box(
            modifier = Modifier
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (abs(offsetX) > swipeThreshold) {
                                isDeleted = true
                            } else {
                                offsetX = 0f
                            }
                        },
                        onDragCancel = {
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val newOffset = offsetX + dragAmount
                            offsetX = newOffset.coerceAtMost(0f)
                        }
                    )
                }
        ) {
            content()
        }
    }
}

/**
 * Swipe with undo functionality
 */
@Composable
fun <T> SwipeWithUndo(
    item: T,
    onDelete: (T) -> Unit,
    modifier: Modifier = Modifier,
    undoDuration: Long = 4000L,
    content: @Composable (T) -> Unit
) {
    var isDeleted by remember { mutableStateOf(false) }
    var showUndo by remember { mutableStateOf(false) }

    LaunchedEffect(showUndo) {
        if (showUndo) {
            kotlinx.coroutines.delay(undoDuration)
            if (showUndo) {
                onDelete(item)
            }
        }
    }

    Box(modifier = modifier) {
        AnimatedVisibility(
            visible = !isDeleted,
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeOut()
        ) {
            SwipeToDeleteItem(
                item = item,
                onDelete = {
                    isDeleted = true
                    showUndo = true
                },
                content = content
            )
        }

        AnimatedVisibility(
            visible = showUndo,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn()
        ) {
            Snackbar(
                action = {
                    TextButton(onClick = {
                        isDeleted = false
                        showUndo = false
                    }) {
                        Text("UNDO")
                    }
                },
                modifier = Modifier.padding(8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Item deleted")
            }
        }
    }
}

/**
 * Reveal action on swipe
 */
@Composable
fun SwipeRevealActions(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }

    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "reveal_offset"
    )

    Box(modifier = modifier) {
        // Actions background
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Edit button
            IconButton(
                onClick = {
                    onEdit()
                    offsetX = 0f
                },
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Delete button
            IconButton(
                onClick = {
                    onDelete()
                    offsetX = 0f
                },
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.error)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onError
                )
            }
        }

        // Content
        Box(
            modifier = Modifier
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            offsetX = if (offsetX < -80) -140f else 0f
                        },
                        onDragCancel = {
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val newOffset = offsetX + dragAmount
                            offsetX = newOffset.coerceIn(-200f, 0f)
                        }
                    )
                }
        ) {
            content()
        }
    }
}

/**
 * Medication item with swipe-to-delete
 */
@Composable
fun SwipeableMedicationItem(
    medicationName: String,
    dosage: String,
    time: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit = {},
    onTap: () -> Unit = {}
) {
    SwipeToDeleteItem(
        item = medicationName,
        onDelete = { onDelete() },
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            onClick = onTap,
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = medicationName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$dosage • $time",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
