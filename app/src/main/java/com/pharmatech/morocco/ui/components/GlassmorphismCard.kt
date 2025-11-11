package com.pharmatech.morocco.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * GlassmorphismCard - Glass-effect card with blur and transparency
 * 
 * Creates modern glassmorphism effect with:
 * - Semi-transparent background
 * - Subtle border
 * - Backdrop blur simulation
 * - Shadow elevation
 * 
 * @param modifier Optional modifier
 * @param backgroundColor Background color (use with alpha)
 * @param borderColor Border color
 * @param borderWidth Border width
 * @param shape Card shape
 * @param elevation Shadow elevation level
 * @param contentPadding Padding for content
 * @param content Card content
 */
@Composable
fun GlassmorphismCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
    borderColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
    borderWidth: Dp = 1.dp,
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: GlassElevation = GlassElevation.Medium,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(borderWidth, borderColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation.shadowElevation
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

/**
 * GlassElevation - Predefined elevation levels for glass cards
 */
enum class GlassElevation(val shadowElevation: Dp) {
    None(0.dp),
    Soft(2.dp),
    Medium(4.dp),
    Strong(8.dp),
    Glow(12.dp)
}

/**
 * FrostedGlassCard - Enhanced glass card with gradient overlay
 * 
 * More sophisticated glassmorphism with gradient effects.
 * 
 * @param modifier Optional modifier
 * @param backgroundColors Gradient colors for background
 * @param blurRadius Blur effect radius
 * @param borderColor Border color
 * @param shape Card shape
 * @param elevation Shadow elevation
 * @param content Card content
 */
@Composable
fun FrostedGlassCard(
    modifier: Modifier = Modifier,
    backgroundColors: List<Color> = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
    ),
    blurRadius: Dp = 0.dp,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
    shape: Shape = RoundedCornerShape(20.dp),
    elevation: GlassElevation = GlassElevation.Medium,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .clip(shape)
            .then(
                if (blurRadius > 0.dp) Modifier.blur(blurRadius) else Modifier
            ),
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = elevation.shadowElevation
    ) {
        Box(
            modifier = Modifier.background(
                brush = Brush.verticalGradient(backgroundColors)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            ) {
                content()
            }
        }
    }
}

/**
 * NeumorphicCard - Soft neumorphic design card
 * 
 * Subtle depth with inner/outer shadows.
 * 
 * @param modifier Optional modifier
 * @param backgroundColor Background color
 * @param shape Card shape
 * @param elevation Elevation style
 * @param content Card content
 */
@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: NeumorphicElevation = NeumorphicElevation.Raised,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = when (elevation) {
                NeumorphicElevation.Flat -> 0.dp
                NeumorphicElevation.Raised -> 4.dp
                NeumorphicElevation.Pressed -> 1.dp
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

/**
 * NeumorphicElevation - Elevation styles for neumorphic cards
 */
enum class NeumorphicElevation {
    Flat,
    Raised,
    Pressed
}

/**
 * GradientGlassCard - Glass card with custom gradient background
 * 
 * Perfect for hero sections or featured content.
 * 
 * @param modifier Optional modifier
 * @param gradientColors List of gradient colors
 * @param gradientAngle Gradient angle in degrees
 * @param borderColor Border color
 * @param shape Card shape
 * @param elevation Shadow elevation
 * @param content Card content
 */
@Composable
fun GradientGlassCard(
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
    ),
    gradientAngle: Float = 135f,
    borderColor: Color = Color.White.copy(alpha = 0.3f),
    shape: Shape = RoundedCornerShape(24.dp),
    elevation: GlassElevation = GlassElevation.Medium,
    contentPadding: PaddingValues = PaddingValues(24.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(1.5.dp, borderColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation.shadowElevation
        )
    ) {
        Box(
            modifier = Modifier.background(
                brush = Brush.linearGradient(
                    colors = gradientColors
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            ) {
                content()
            }
        }
    }
}

/**
 * OutlinedGlassCard - Glass card with prominent outline
 * 
 * Lighter glass effect with focus on border.
 * 
 * @param modifier Optional modifier
 * @param backgroundColor Background color
 * @param borderColor Border color
 * @param borderWidth Border width
 * @param shape Card shape
 * @param content Card content
 */
@Composable
fun OutlinedGlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
    borderColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
    borderWidth: Dp = 2.dp,
    shape: Shape = RoundedCornerShape(16.dp),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.outlinedCardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

/**
 * AccentGlassCard - Glass card with accent color tint
 * 
 * For highlighting special content with brand colors.
 * 
 * @param modifier Optional modifier
 * @param accentColor Accent color for tint
 * @param alpha Background transparency
 * @param shape Card shape
 * @param elevation Shadow elevation
 * @param content Card content
 */
@Composable
fun AccentGlassCard(
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    alpha: Float = 0.15f,
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: GlassElevation = GlassElevation.Soft,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    GlassmorphismCard(
        modifier = modifier,
        backgroundColor = accentColor.copy(alpha = alpha),
        borderColor = accentColor.copy(alpha = 0.3f),
        shape = shape,
        elevation = elevation,
        contentPadding = contentPadding,
        content = content
    )
}

/**
 * DarkGlassCard - Dark glass card for dark mode
 * 
 * Optimized for dark theme with proper contrast.
 * 
 * @param modifier Optional modifier
 * @param shape Card shape
 * @param elevation Shadow elevation
 * @param content Card content
 */
@Composable
fun DarkGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: GlassElevation = GlassElevation.Medium,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    GlassmorphismCard(
        modifier = modifier,
        backgroundColor = Color.Black.copy(alpha = 0.4f),
        borderColor = Color.White.copy(alpha = 0.1f),
        shape = shape,
        elevation = elevation,
        contentPadding = contentPadding,
        content = content
    )
}
