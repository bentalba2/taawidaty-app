/*
 * FilterChipAnimations.kt
 * TAAWIDATY System
 * 
 * Description: Animated filter chip components
 * Created: November 2025
 * 
 * Features:
 * - Animated selection state
 * - Multi-select with bounce
 * - Clear all animation
 * - Horizontal scroll with stagger
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Animated filter chip with selection state
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AnimatedFilterChip(
    label: String,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "chip_scale"
    )

    FilterChip(
        selected = selected,
        onClick = { onSelectedChange(!selected) },
        label = {
            AnimatedContent(
                targetState = label,
                transitionSpec = {
                    fadeIn() + scaleIn(spring(Spring.DampingRatioMediumBouncy)) togetherWith
                            fadeOut() + scaleOut()
                },
                label = "chip_label"
            ) { text ->
                Text(
                    text = text,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        },
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    modifier = Modifier.size(18.dp)
                )
            }
        } else leadingIcon
    )
}

/**
 * Filter chip row with stagger animation
 */
@Composable
fun AnimatedFilterChipRow(
    filters: List<FilterItem>,
    selectedFilters: Set<String>,
    onFilterToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    onClearAll: (() -> Unit)? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            AnimatedVisibility(
                visible = selectedFilters.isNotEmpty() && onClearAll != null,
                enter = fadeIn() + slideInHorizontally(initialOffsetX = { it / 2 }),
                exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it / 2 })
            ) {
                TextButton(onClick = { onClearAll?.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear all",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear All")
                }
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            itemsIndexed(filters) { index, filter ->
                val delay = (index * 30).toLong()
                var visible by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(delay)
                    visible = true
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + scaleIn(
                        spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                ) {
                    AnimatedFilterChip(
                        label = filter.label,
                        selected = filter.id in selectedFilters,
                        onSelectedChange = { onFilterToggle(filter.id) }
                    )
                }
            }
        }

        // Active filter count
        AnimatedContent(
            targetState = selectedFilters.size,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically { -it } + fadeIn() togetherWith
                            slideOutVertically { it } + fadeOut()
                } else {
                    slideInVertically { it } + fadeIn() togetherWith
                            slideOutVertically { -it } + fadeOut()
                }
            },
            label = "filter_count"
        ) { count ->
            if (count > 0) {
                Text(
                    text = "$count filter${if (count > 1) "s" else ""} active",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Multi-select filter chips with category
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryFilterChips(
    category: FilterCategory,
    selectedFilters: Set<String>,
    onFilterToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(true) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Category header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            IconButton(
                onClick = { expanded = !expanded }
            ) {
                val rotation by animateFloatAsState(
                    targetValue = if (expanded) 180f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "chevron_rotation"
                )

                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.graphicsLayer {
                        rotationZ = rotation
                    }
                )
            }
        }

        // Filter chips
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                category.filters.forEach { filter ->
                    AnimatedFilterChip(
                        label = filter.label,
                        selected = filter.id in selectedFilters,
                        onSelectedChange = { onFilterToggle(filter.id) }
                    )
                }
            }
        }
    }
}

/**
 * Bouncing selected filter badge
 */
@Composable
fun SelectedFilterBadge(
    count: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = count > 0,
        enter = scaleIn(spring(Spring.DampingRatioLowBouncy)) + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        Badge(
            modifier = Modifier.animateContentSize(
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        ) {
            Text(text = count.toString())
        }
    }
}

/**
 * Filter data models
 */
data class FilterItem(
    val id: String,
    val label: String
)

data class FilterCategory(
    val name: String,
    val filters: List<FilterItem>
)
