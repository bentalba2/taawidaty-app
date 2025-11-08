package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.delay

/**
 * AnimatedDropdownMenu - Material 3 dropdown with smooth animations
 * 
 * Features:
 * - Slide-in animation from top
 * - Fade animation
 * - Scale animation
 * - Staggered item entrance (30ms delay)
 * - Chevron rotation
 * 
 * @param expanded Whether the dropdown is expanded
 * @param onDismissRequest Callback when user dismisses the dropdown
 * @param modifier Optional modifier
 * @param offset Offset from anchor point
 * @param content Dropdown content
 */
@Composable
fun AnimatedDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    // Animated visibility with slide, fade, and scale
    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 150)
        ) + slideInVertically(
            animationSpec = tween(durationMillis = 200),
            initialOffsetY = { -it / 4 }
        ) + scaleIn(
            animationSpec = tween(durationMillis = 200),
            initialScale = 0.8f
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 100)
        ) + slideOutVertically(
            animationSpec = tween(durationMillis = 150),
            targetOffsetY = { -it / 4 }
        ) + scaleOut(
            animationSpec = tween(durationMillis = 150),
            targetScale = 0.8f
        )
    ) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            offset = offset,
            properties = PopupProperties(focusable = true)
        ) {
            content()
        }
    }
}

/**
 * AnimatedDropdownItem - Dropdown menu item with staggered entrance animation
 * 
 * @param text Item text
 * @param onClick Click callback
 * @param modifier Optional modifier
 * @param index Item index for stagger delay
 * @param leadingIcon Optional leading icon
 * @param trailingIcon Optional trailing icon
 * @param selected Whether item is selected
 * @param enabled Whether item is enabled
 */
@Composable
fun AnimatedDropdownItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    selected: Boolean = false,
    enabled: Boolean = true
) {
    // Staggered animation state
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(index * 30L) // 30ms stagger delay
        visible = true
    }

    // Animated entrance
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 200)
        ) + slideInVertically(
            animationSpec = tween(durationMillis = 200),
            initialOffsetY = { -20 }
        ),
        exit = fadeOut()
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = text,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                )
            },
            onClick = onClick,
            modifier = modifier,
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            },
            trailingIcon = if (selected && trailingIcon == null) {
                {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (trailingIcon != null) {
                {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null
                    )
                }
            } else null,
            enabled = enabled,
            colors = MenuDefaults.itemColors(
                textColor = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        )
    }
}

/**
 * AnimatedExposedDropdownMenu - Exposed dropdown with animated chevron
 * 
 * Full dropdown implementation with text field, chevron rotation, and animations.
 * 
 * @param items List of items to display
 * @param selectedItem Currently selected item
 * @param onItemSelected Callback when item is selected
 * @param label Dropdown label
 * @param modifier Optional modifier
 * @param enabled Whether dropdown is enabled
 * @param itemContent Custom content for each item
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AnimatedExposedDropdownMenu(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    itemContent: @Composable (T) -> String = { it.toString() }
) {
    var expanded by remember { mutableStateOf(false) }
    
    // Animated chevron rotation
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "chevron_rotation"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedItem?.let { itemContent(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(rotation)
                )
            },
            enabled = enabled,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        AnimatedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEachIndexed { index, item ->
                AnimatedDropdownItem(
                    text = itemContent(item),
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    index = index,
                    selected = item == selectedItem
                )
            }
        }
    }
}

/**
 * StaggeredDropdownList - Dropdown with custom staggered list content
 * 
 * For complex dropdown content with custom layouts.
 * 
 * @param items List of items
 * @param expanded Whether dropdown is expanded
 * @param onDismissRequest Dismiss callback
 * @param modifier Optional modifier
 * @param staggerDelayMs Delay between items in milliseconds
 * @param itemContent Content for each item
 */
@Composable
fun <T> StaggeredDropdownList(
    items: List<T>,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    staggerDelayMs: Long = 30L,
    itemContent: @Composable (T, Int) -> Unit
) {
    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(tween(200)) + scaleIn(
            tween(200),
            initialScale = 0.9f
        ),
        exit = fadeOut(tween(150)) + scaleOut(
            tween(150),
            targetScale = 0.9f
        )
    ) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 8.dp,
            tonalElevation = 2.dp
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .padding(vertical = 8.dp)
            ) {
                itemsIndexed(items) { index, item ->
                    var visible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        delay(index * staggerDelayMs)
                        visible = true
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(200)) + slideInVertically(
                            tween(200),
                            initialOffsetY = { -20 }
                        )
                    ) {
                        itemContent(item, index)
                    }
                }
            }
        }
    }
}
