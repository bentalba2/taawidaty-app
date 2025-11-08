package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.pharmatech.morocco.core.datastore.ThemeMode

/**
 * ThemeToggleButton - Animated button to cycle through theme modes
 * 
 * Displays icon based on current theme mode:
 * - Sun icon for Light mode
 * - Moon icon for Dark mode
 * - Monitor icon for System mode
 * 
 * @param currentMode Current theme mode
 * @param onThemeToggle Callback when theme should change
 * @param modifier Optional modifier
 */
@Composable
fun ThemeToggleButton(
    currentMode: ThemeMode,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation for icon changes
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "theme_icon_scale"
    )

    // Rotation animation for smooth transition
    var rotation by remember { mutableFloatStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "theme_icon_rotation"
    )

    IconButton(
        onClick = {
            rotation += 180f
            onThemeToggle()
        },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (currentMode) {
                    ThemeMode.LIGHT -> Icons.Default.WbSunny
                    ThemeMode.DARK -> Icons.Default.DarkMode
                    ThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                },
                contentDescription = when (currentMode) {
                    ThemeMode.LIGHT -> "Light mode - Click to switch to Dark"
                    ThemeMode.DARK -> "Dark mode - Click to switch to System"
                    ThemeMode.SYSTEM -> "System mode - Click to switch to Light"
                },
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * ThemeToggleSegmented - Segmented control for theme selection
 * 
 * Shows three options with visual indication of selected mode.
 * More explicit than icon-only toggle.
 * 
 * @param currentMode Current theme mode
 * @param onModeChange Callback when mode changes
 * @param modifier Optional modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeToggleSegmented(
    currentMode: ThemeMode,
    onModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ThemeMode.entries.forEach { mode ->
            val selected = currentMode == mode
            
            // Animated selection background
            val backgroundColor by animateColorAsState(
                targetValue = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Transparent
                },
                animationSpec = tween(durationMillis = 200),
                label = "segment_background"
            )
            
            val contentColor by animateColorAsState(
                targetValue = if (selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                animationSpec = tween(durationMillis = 200),
                label = "segment_content"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.small)
                    .background(backgroundColor)
                    .clickable { onModeChange(mode) }
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (mode) {
                            ThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                            ThemeMode.LIGHT -> Icons.Default.WbSunny
                            ThemeMode.DARK -> Icons.Default.DarkMode
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = contentColor
                    )
                    Text(
                        text = when (mode) {
                            ThemeMode.SYSTEM -> "Auto"
                            ThemeMode.LIGHT -> "Light"
                            ThemeMode.DARK -> "Dark"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = contentColor
                    )
                }
            }
        }
    }
}

/**
 * ThemeToggleDropdown - Dropdown menu for theme selection
 * 
 * Space-efficient option that shows menu on click.
 * 
 * @param currentMode Current theme mode
 * @param onModeChange Callback when mode changes
 * @param modifier Optional modifier
 */
@Composable
fun ThemeToggleDropdown(
    currentMode: ThemeMode,
    onModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = when (currentMode) {
                    ThemeMode.LIGHT -> Icons.Default.WbSunny
                    ThemeMode.DARK -> Icons.Default.DarkMode
                    ThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                },
                contentDescription = "Theme settings"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ThemeMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (mode) {
                                    ThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                                    ThemeMode.LIGHT -> Icons.Default.WbSunny
                                    ThemeMode.DARK -> Icons.Default.DarkMode
                                },
                                contentDescription = null
                            )
                            Text(
                                text = when (mode) {
                                    ThemeMode.SYSTEM -> "System Default"
                                    ThemeMode.LIGHT -> "Light Mode"
                                    ThemeMode.DARK -> "Dark Mode"
                                }
                            )
                        }
                    },
                    onClick = {
                        onModeChange(mode)
                        expanded = false
                    },
                    leadingIcon = if (mode == currentMode) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected"
                            )
                        }
                    } else null
                )
            }
        }
    }
}
