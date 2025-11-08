/*
 * IconMorphing.kt
 * TAAWIDATY System
 * 
 * Description: Icon morphing animations for state changes
 * Created: November 2025
 * 
 * Features:
 * - Menu to close icon transition
 * - Add to check animation
 * - Play/pause morphing
 * - Expand/collapse chevron
 * - Smooth icon transitions
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Morphing icon button with smooth transitions
 */
@Composable
fun MorphingIconButton(
    icon1: ImageVector,
    icon2: ImageVector,
    isIcon1: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Crossfade(
            targetState = isIcon1,
            animationSpec = tween(300),
            label = "icon_crossfade"
        ) { showIcon1 ->
            Icon(
                imageVector = if (showIcon1) icon1 else icon2,
                contentDescription = contentDescription
            )
        }
    }
}

/**
 * Menu to close icon with rotation
 */
@Composable
fun MenuCloseIcon(
    isMenu: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isMenu) 0f else 180f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "menu_rotation"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isMenu) Icons.Default.Menu else Icons.Default.Close,
            contentDescription = if (isMenu) "Open Menu" else "Close Menu",
            modifier = Modifier.rotate(rotation)
        )
    }
}

/**
 * Add to check icon with scale animation
 */
@Composable
fun AddCheckIcon(
    isAdded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isAdded) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "add_check_scale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isAdded) 360f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "add_check_rotation"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isAdded) Icons.Default.Check else Icons.Default.Add,
            contentDescription = if (isAdded) "Added" else "Add",
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
            },
            tint = if (isAdded) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

/**
 * Play/Pause icon with smooth transition
 */
@Composable
fun PlayPauseIcon(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        AnimatedContent(
            targetState = isPlaying,
            transitionSpec = {
                scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn() togetherWith
                        scaleOut(spring(Spring.DampingRatioNoBouncy)) + fadeOut()
            },
            label = "play_pause"
        ) { playing ->
            Icon(
                imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (playing) "Pause" else "Play",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

/**
 * Favorite icon with bounce animation
 */
@Composable
fun FavoriteIcon(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "favorite_scale"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
            tint = if (isFavorite) {
                Color(0xFFE91E63) // Pink
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

/**
 * Chevron with rotation animation
 */
@Composable
fun ExpandCollapseChevron(
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "chevron_rotation"
    )

    Icon(
        imageVector = Icons.Default.ExpandMore,
        contentDescription = if (isExpanded) "Collapse" else "Expand",
        modifier = modifier.rotate(rotation)
    )
}

/**
 * Visibility toggle icon
 */
@Composable
fun VisibilityToggleIcon(
    isVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        AnimatedContent(
            targetState = isVisible,
            transitionSpec = {
                fadeIn(animationSpec = tween(200)) togetherWith
                        fadeOut(animationSpec = tween(200))
            },
            label = "visibility"
        ) { visible ->
            Icon(
                imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = if (visible) "Hide" else "Show"
            )
        }
    }
}

/**
 * Lock/Unlock icon with rotation
 */
@Composable
fun LockUnlockIcon(
    isLocked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isLocked) 0f else 15f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "lock_rotation"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
            contentDescription = if (isLocked) "Unlock" else "Lock",
            modifier = Modifier.rotate(rotation)
        )
    }
}

/**
 * Edit/Done icon transition
 */
@Composable
fun EditDoneIcon(
    isEditing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isEditing) 1f else 1.1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "edit_done_scale"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        AnimatedContent(
            targetState = isEditing,
            transitionSpec = {
                scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn() togetherWith
                        scaleOut(spring(Spring.DampingRatioNoBouncy)) + fadeOut()
            },
            label = "edit_done"
        ) { editing ->
            Icon(
                imageVector = if (editing) Icons.Default.Done else Icons.Default.Edit,
                contentDescription = if (editing) "Save" else "Edit",
                modifier = Modifier.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
            )
        }
    }
}

/**
 * Notification bell with shake animation
 */
@Composable
fun NotificationBellIcon(
    hasNotifications: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var shake by remember { mutableStateOf(false) }

    LaunchedEffect(hasNotifications) {
        if (hasNotifications) {
            shake = true
            kotlinx.coroutines.delay(500)
            shake = false
        }
    }

    val rotation by animateFloatAsState(
        targetValue = if (shake) 15f else 0f,
        animationSpec = repeatable(
            iterations = 3,
            animation = tween(100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bell_shake"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Box {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                modifier = Modifier.rotate(rotation)
            )

            if (hasNotifications) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                        .background(
                            color = MaterialTheme.colorScheme.error,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
            }
        }
    }
}

/**
 * Search icon that expands to search bar
 */
@Composable
fun ExpandingSearchIcon(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onToggle) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Search,
                contentDescription = if (isExpanded) "Close Search" else "Search"
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.width(200.dp),
                placeholder = { Text("Search...") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
        }
    }
}

/**
 * Archive/Unarchive icon
 */
@Composable
fun ArchiveIcon(
    isArchived: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isArchived) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "archive_rotation"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isArchived) Icons.Default.Unarchive else Icons.Default.Archive,
            contentDescription = if (isArchived) "Unarchive" else "Archive",
            modifier = Modifier.rotate(rotation),
            tint = if (isArchived) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}
