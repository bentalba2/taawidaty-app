/*
 * ChartAnimations.kt
 * TAAWIDATY System
 * 
 * Description: Animated charts for medication adherence trends
 * Created: November 2025
 * 
 * Features:
 * - Animated line charts with path drawing
 * - Bar charts with height animations
 * - Legend with stagger animation
 * - Smooth data transitions
 * - Touch interactions
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.max

/**
 * Animated line chart
 */
@Composable
fun AnimatedLineChart(
    data: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    showPoints: Boolean = true,
    animationDuration: Int = 1000
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = animationDuration,
            easing = FastOutSlowInEasing
        ),
        label = "line_chart_progress"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            if (data.isEmpty()) return@Canvas

            val maxValue = data.maxOrNull() ?: 1f
            val spacing = size.width / (data.size - 1).coerceAtLeast(1)
            val heightScale = size.height / maxValue

            // Draw path
            val path = Path().apply {
                data.forEachIndexed { index, value ->
                    val x = index * spacing
                    val y = size.height - (value * heightScale * animatedProgress)
                    
                    if (index == 0) {
                        moveTo(x, y)
                    } else {
                        lineTo(x, y)
                    }
                }
            }

            // Draw gradient fill
            val fillPath = Path().apply {
                addPath(path)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        lineColor.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                )
            )

            // Draw line
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw points
            if (showPoints) {
                data.forEachIndexed { index, value ->
                    val x = index * spacing
                    val y = size.height - (value * heightScale * animatedProgress)
                    
                    drawCircle(
                        color = lineColor,
                        radius = 6.dp.toPx(),
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
        }

        // Labels
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Animated bar chart
 */
@Composable
fun AnimatedBarChart(
    data: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    animationDuration: Int = 800
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bar_chart_progress"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            if (data.isEmpty()) return@Canvas

            val maxValue = data.maxOrNull() ?: 1f
            val barWidth = (size.width / data.size) * 0.7f
            val spacing = size.width / data.size

            data.forEachIndexed { index, value ->
                val barHeight = (value / maxValue) * size.height * animatedProgress
                val x = (index * spacing) + (spacing - barWidth) / 2
                val y = size.height - barHeight

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Grouped bar chart
 */
@Composable
fun GroupedBarChart(
    dataGroups: List<List<Float>>,
    labels: List<String>,
    legendLabels: List<String>,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color(0xFF0077BE),
        Color(0xFF4CAF50),
        Color(0xFFD4AF37)
    )
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "grouped_bars"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            legendLabels.forEachIndexed { index, label ->
                LegendItem(
                    label = label,
                    color = colors.getOrElse(index) { MaterialTheme.colorScheme.primary },
                    delay = (index * 100).toLong()
                )
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            if (dataGroups.isEmpty() || dataGroups[0].isEmpty()) return@Canvas

            val maxValue = dataGroups.flatten().maxOrNull() ?: 1f
            val groupCount = dataGroups[0].size
            val barGroupWidth = (size.width / groupCount) * 0.8f
            val barWidth = barGroupWidth / dataGroups.size
            val spacing = size.width / groupCount

            dataGroups.forEachIndexed { groupIndex, values ->
                values.forEachIndexed { index, value ->
                    val barHeight = (value / maxValue) * size.height * animatedProgress
                    val groupX = index * spacing
                    val barX = groupX + (groupIndex * barWidth) + (spacing - barGroupWidth) / 2
                    val y = size.height - barHeight

                    drawRoundRect(
                        color = colors.getOrElse(groupIndex) { Color.Gray },
                        topLeft = Offset(barX, y),
                        size = Size(barWidth * 0.9f, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Legend item with animation
 */
@Composable
private fun LegendItem(
    label: String,
    color: Color,
    delay: Long = 0
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay)
        visible = true
    }

    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInHorizontally(
            initialOffsetX = { -it / 4 }
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Adherence trend chart
 */
@Composable
fun AdherenceTrendChart(
    weeklyData: List<Float>,
    modifier: Modifier = Modifier
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Weekly Adherence",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            val average = weeklyData.average().toFloat()
            Text(
                text = "${(average * 100).toInt()}% Average",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AnimatedLineChart(
                data = weeklyData,
                labels = days,
                showPoints = true
            )
        }
    }
}

/**
 * Progress comparison chart
 */
@Composable
fun ProgressComparisonChart(
    currentWeek: List<Float>,
    lastWeek: List<Float>,
    modifier: Modifier = Modifier
) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Week Comparison",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            GroupedBarChart(
                dataGroups = listOf(lastWeek, currentWeek),
                labels = days,
                legendLabels = listOf("Last Week", "This Week"),
                colors = listOf(
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

/**
 * Simple stat card with animated value
 */
@Composable
fun AnimatedStatCard(
    title: String,
    value: Float,
    unit: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "stat_value"
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = animatedValue.toInt().toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
