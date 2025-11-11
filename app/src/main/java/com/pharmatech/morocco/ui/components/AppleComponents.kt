package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pharmatech.morocco.ui.theme.*

/**
 * Apple-style Primary Button
 * Clean, minimal, with smooth animations
 */
@Composable
fun ApplePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed || loading) 0.95f else 1f,
        animationSpec = tween(150, easing = AppleEasing.EaseInOut),
        label = "buttonScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (enabled && !loading) 1f else 0.6f,
        animationSpec = tween(150, easing = AppleEasing.EaseInOut),
        label = "buttonAlpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = if (enabled) AppleColors.Light.Primary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                enabled = enabled && !loading,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                },
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = true,
                    radius = 200.dp,
                    color = Color.White.copy(alpha = 0.3f)
                )
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            AppleActivityIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                horizontalGravity = Alignment.CenterHorizontally
            ) {
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = text,
                    style = AppleTypography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Apple-style Secondary Button
 * Clean outline design
 */
@Composable
fun AppleSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(150, easing = AppleEasing.EaseInOut),
        label = "buttonScale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> AppleColors.Light.Fill
            isPressed -> AppleColors.Light.Fill
            else -> Color.Transparent
        },
        animationSpec = tween(150, easing = AppleEasing.EaseInOut),
        label = "backgroundColor"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                enabled = enabled,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                },
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = true,
                    radius = 200.dp,
                    color = AppleColors.Light.Primary.copy(alpha = 0.3f)
                )
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(
                width = 1.dp,
                color = AppleColors.Light.Separator,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = AppleColors.Light.Primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = text,
                style = AppleTypography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (enabled) AppleColors.Light.Primary else AppleColors.Light.OnSurfaceTertiary,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Apple-style Card
 * Minimal, clean with subtle shadows
 */
@Composable
fun AppleCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    elevation: Float = 1f,
    content: @Composable ColumnScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedElevation by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) elevation + 2f else elevation,
        animationSpec = tween(150, easing = AppleEasing.EaseInOut),
        label = "cardElevation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(150, easing = AppleEasing.EaseInOut),
        label = "cardScale"
    )

    Card(
        onClick = onClick ?: {},
        enabled = enabled,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentThemeDark()) {
                AppleColors.Dark.Card
            } else {
                AppleColors.Light.Surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = animatedElevation.dp,
            pressedElevation = (animatedElevation + 4f).dp
        ),
        interactionSource = if (onClick != null) interactionSource else remember { MutableInteractionSource() },
        indication = if (onClick != null) {
            rememberRipple(
                bounded = true,
                radius = 200.dp,
                color = AppleColors.Light.Primary.copy(alpha = 0.2f)
            )
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            if (onClick != null && enabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onClick()
                                true
                            } else {
                                false
                            }
                        }
                    )
                },
            content = content
        )
    }
}

/**
 * Apple-style List Item
 * Clean, minimal with proper spacing
 */
@Composable
fun AppleListItem(
    title: String,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showDivider: Boolean = true
) {
    val haptic = LocalHapticFeedback.current

    AppleCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = 0f
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            leadingIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppleColors.Light.OnSurfaceSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = AppleTypography.bodyLarge.copy(
                        color = if (isCurrentThemeDark()) {
                            AppleColors.Dark.OnSurface
                        } else {
                            AppleColors.Light.OnSurface
                        },
                        fontWeight = FontWeight.Medium
                    )
                )

                subtitle?.let {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = it,
                        style = AppleTypography.bodySmall.copy(
                            color = if (isCurrentThemeDark()) {
                                AppleColors.Dark.OnSurfaceSecondary
                            } else {
                                AppleColors.Light.OnSurfaceSecondary
                            }
                        )
                    )
                }
            }

            trailingIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppleColors.Light.OnSurfaceTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    if (showDivider) {
        Divider(
            modifier = Modifier.padding(start = if (leadingIcon != null) 56.dp else 16.dp),
            color = if (isCurrentThemeDark()) {
                AppleColors.Dark.Separator
            } else {
                AppleColors.Light.Separator
            }
        )
    }
}

/**
 * Apple-style Activity Indicator
 * Clean, minimal loading indicator
 */
@Composable
fun AppleActivityIndicator(
    modifier: Modifier = Modifier,
    color: Color = AppleColors.Light.Primary,
    strokeWidth: Float = 3f
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(
        modifier = modifier
            .size(20.dp)
            .graphicsLayer {
                rotationZ = rotation
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val strokeWidthPx = strokeWidth.toPx()

        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    color.copy(alpha = 0.3f),
                    color,
                    color,
                    color.copy(alpha = 0.3f)
                )
            ),
            startAngle = 0f,
            sweepAngle = 300f,
            useCenter = false,
            size = androidx.compose.ui.geometry.Size(
                width = canvasWidth - strokeWidthPx,
                height = canvasHeight - strokeWidthPx
            ),
            topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidthPx,
                cap = StrokeCap.Round
            )
        )
    }
}

/**
 * Apple-style Switch
 * Clean, minimal toggle
 */
@Composable
fun AppleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (checked) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "switchScale"
    )

    Switch(
        checked = checked,
        onCheckedChange = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onCheckedChange(it)
        },
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = AppleColors.Light.Primary,
            uncheckedThumbColor = AppleColors.Light.OnSurface,
            uncheckedTrackColor = AppleColors.Light.Fill
        ),
        thumbContent = {
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (checked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = AppleColors.Light.Primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    )
}

// Helper function to check current theme
@Composable
private fun isCurrentThemeDark(): Boolean {
    return MaterialTheme.colorScheme.background == AppleColors.Dark.Background
}