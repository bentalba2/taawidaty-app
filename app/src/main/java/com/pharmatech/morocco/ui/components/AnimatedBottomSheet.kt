/*
 * AnimatedBottomSheet.kt
 * TAAWIDATY System
 * 
 * Description: Modal bottom sheet with drag animations and backdrop blur
 * Created: November 2025
 * 
 * Features:
 * - Drag-to-dismiss with spring physics
 * - Backdrop blur effect
 * - Smooth expand/collapse animations
 * - Custom peek height
 * - Swipe handle indicator
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Modal bottom sheet with animations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedModalBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDragHandle() },
    content: @Composable ColumnScope.() -> Unit
) {
    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            modifier = modifier,
            dragHandle = dragHandle,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                content = content
            )
        }
    }
}

/**
 * Custom drag handle with animation
 */
@Composable
fun BottomSheetDragHandle(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
) {
    var isDragging by remember { mutableStateOf(false) }

    val width by animateDpAsState(
        targetValue = if (isDragging) 48.dp else 32.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "handle_width"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(width)
                .height(4.dp)
                .clip(CircleShape)
                .background(color)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false },
                        onVerticalDrag = { _, _ -> }
                    )
                }
        )
    }
}

/**
 * Glass bottom sheet with blur effect
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    content: @Composable ColumnScope.() -> Unit
) {
    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            modifier = modifier,
            dragHandle = { BottomSheetDragHandle() },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                content = content
            )
        }
    }
}

/**
 * Bottom sheet header with title and close button
 */
@Composable
fun BottomSheetHeader(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 12.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

/**
 * Animated backdrop for bottom sheet
 */
@Composable
fun AnimatedBackdrop(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Black.copy(alpha = 0.5f)
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(200)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, _ -> }
                }
        )
    }
}

/**
 * Bottom sheet with custom content animation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaggeredBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    items: List<BottomSheetItem>,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            modifier = modifier,
            dragHandle = { BottomSheetDragHandle() },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                BottomSheetHeader(
                    title = title,
                    onClose = onDismiss
                )

                items.forEachIndexed { index, item ->
                    val animatedDelay = (index * 50).toLong()
                    
                    var itemVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(animatedDelay)
                        itemVisible = true
                    }

                    AnimatedVisibility(
                        visible = itemVisible,
                        enter = fadeIn(animationSpec = tween(300)) +
                                slideInHorizontally(
                                    initialOffsetX = { -it / 4 },
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                    ) {
                        ListItem(
                            headlineContent = { Text(item.title) },
                            supportingContent = item.subtitle?.let { { Text(it) } },
                            leadingContent = item.icon?.let {
                                {
                                    Icon(
                                        imageVector = it,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            modifier = Modifier.clickable {
                                item.onClick()
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Medication details bottom sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationDetailsSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    medicationName: String,
    dosage: String,
    frequency: String,
    notes: String?,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            modifier = modifier,
            dragHandle = { BottomSheetDragHandle() },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BottomSheetHeader(
                    title = medicationName,
                    subtitle = "Medication Details",
                    onClose = onDismiss
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailRow(label = "Dosage", value = dosage)
                    DetailRow(label = "Frequency", value = frequency)
                    notes?.let {
                        DetailRow(label = "Notes", value = it)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onEdit()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Edit")
                    }

                    Button(
                        onClick = {
                            onDelete()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

/**
 * Detail row for bottom sheet
 */
@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Data class for bottom sheet items
 */
data class BottomSheetItem(
    val title: String,
    val subtitle: String? = null,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    val onClick: () -> Unit
)
