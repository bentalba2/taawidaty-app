/*
 * ProgressRingAnimation.kt
 * TAAWIDATY System
 * 
 * Description: Animated circular progress rings with smooth arc transitions
 * Created: November 2025
 * 
 * Features:
 * - Animated circular progress for adherence tracking
 * - Smooth arc transitions with spring physics
 * - Percentage count-up animation
 * - Multiple ring styles and sizes
 * - Gradient progress rings
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Animated circular progress ring
 */
@Composable
fun AnimatedProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 12.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    animationDuration: Int = 1000,
    showPercentage: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress_ring"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .progressSemantics(animatedProgress)
        ) {
            val diameter = size.toPx()
            val strokeWidthPx = strokeWidth.toPx()
            val radius = (diameter - strokeWidthPx) / 2

            // Background circle
            drawCircle(
                color = backgroundColor,
                radius = radius,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )

            // Progress arc
            val sweepAngle = animatedProgress * 360f
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                size = Size(diameter - strokeWidthPx, diameter - strokeWidthPx),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }

        if (showPercentage) {
            val percentage = (animatedProgress * 100).toInt()
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Gradient progress ring
 */
@Composable
fun GradientProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 12.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    gradientColors: List<Color> = listOf(
        Color(0xFF0077BE), // Trust Blue
        Color(0xFF4CAF50)  // Success Green
    ),
    showPercentage: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "gradient_progress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .progressSemantics(animatedProgress)
        ) {
            val diameter = size.toPx()
            val strokeWidthPx = strokeWidth.toPx()
            val radius = (diameter - strokeWidthPx) / 2

            // Background circle
            drawCircle(
                color = backgroundColor,
                radius = radius,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )

            // Gradient progress arc
            val sweepAngle = animatedProgress * 360f
            val brush = Brush.sweepGradient(
                colors = gradientColors,
                center = Offset(diameter / 2, diameter / 2)
            )
            
            drawArc(
                brush = brush,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                size = Size(diameter - strokeWidthPx, diameter - strokeWidthPx),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }

        if (showPercentage) {
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Multi-layer progress ring
 */
@Composable
fun MultiLayerProgressRing(
    layers: List<ProgressLayer>,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    strokeWidth: Dp = 8.dp,
    spacing: Dp = 4.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        layers.forEachIndexed { index, layer ->
            val animatedProgress by animateFloatAsState(
                targetValue = layer.progress.coerceIn(0f, 1f),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "layer_$index"
            )

            val layerSize = size - ((strokeWidth + spacing) * 2 * index)
            val offset = (strokeWidth + spacing) * index

            Canvas(
                modifier = Modifier
                    .size(layerSize)
                    .offset(offset, offset)
            ) {
                val diameter = layerSize.toPx()
                val strokeWidthPx = strokeWidth.toPx()
                val radius = (diameter - strokeWidthPx) / 2

                // Background
                drawCircle(
                    color = layer.backgroundColor,
                    radius = radius,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )

                // Progress
                val sweepAngle = animatedProgress * 360f
                drawArc(
                    color = layer.color,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                    size = Size(diameter - strokeWidthPx, diameter - strokeWidthPx),
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
            }
        }
    }
}

/**
 * Progress ring with label
 */
@Composable
fun LabeledProgressRing(
    progress: Float,
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    strokeWidth: Dp = 10.dp,
    progressColor: Color = MaterialTheme.colorScheme.primary
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnimatedProgressRing(
            progress = progress,
            size = size,
            strokeWidth = strokeWidth,
            progressColor = progressColor,
            showPercentage = true
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Adherence tracking ring with stats
 */
@Composable
fun AdherenceRing(
    adherencePercentage: Float,
    takenCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = adherencePercentage.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "adherence"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        GradientProgressRing(
            progress = adherencePercentage,
            size = size,
            strokeWidth = 16.dp,
            showPercentage = false
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Adherence",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "$takenCount/$totalCount doses",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Segmented progress ring
 */
@Composable
fun SegmentedProgressRing(
    segments: Int,
    completedSegments: Int,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 12.dp,
    completedColor: Color = MaterialTheme.colorScheme.primary,
    incompleteColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    val animatedCompleted by animateIntAsState(
        targetValue = completedSegments.coerceIn(0, segments),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "segments"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val diameter = size.toPx()
            val strokeWidthPx = strokeWidth.toPx()
            val segmentAngle = 360f / segments
            val gapAngle = 4f

            for (i in 0 until segments) {
                val startAngle = -90f + (i * segmentAngle)
                val color = if (i < animatedCompleted) completedColor else incompleteColor
                
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = segmentAngle - gapAngle,
                    useCenter = false,
                    topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                    size = Size(diameter - strokeWidthPx, diameter - strokeWidthPx),
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
            }
        }

        Text(
            text = "$animatedCompleted/$segments",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Pulsing progress ring
 */
@Composable
fun PulsingProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 12.dp,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    pulseEnabled: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (pulseEnabled && progress >= 1f) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .size(size * scale),
        contentAlignment = Alignment.Center
    ) {
        AnimatedProgressRing(
            progress = progress,
            size = size,
            strokeWidth = strokeWidth,
            progressColor = progressColor,
            showPercentage = true
        )
    }
}

/**
 * Data class for progress layers
 */
data class ProgressLayer(
    val progress: Float,
    val color: Color,
    val backgroundColor: Color
)
