package com.pharmatech.morocco.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * PulseButton - Button with continuous breathing/pulse animation
 * 
 * Subtle scale effect that pulses infinitely to draw attention.
 * 
 * @param onClick Click callback
 * @param modifier Optional modifier
 * @param enabled Whether button is enabled
 * @param pulseEnabled Whether pulse animation is active
 * @param minScale Minimum scale (e.g., 0.95 for 5% shrink)
 * @param maxScale Maximum scale (e.g., 1.05 for 5% growth)
 * @param pulseDurationMs Duration of one pulse cycle
 * @param colors Button colors
 * @param shape Button shape
 * @param content Button content
 */
@Composable
fun PulseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    pulseEnabled: Boolean = true,
    minScale: Float = 0.97f,
    maxScale: Float = 1.03f,
    pulseDurationMs: Int = 2000,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    shape: Shape = ButtonDefaults.shape,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    // Infinite pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = pulseDurationMs,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Press animation
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "press_scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier.scale(if (pulseEnabled && enabled) scale * pressScale else pressScale),
        enabled = enabled,
        colors = colors,
        shape = shape,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

/**
 * BreatheButton - Button with subtle breathing animation
 * 
 * More subtle than pulse, good for primary CTAs.
 * 
 * @param onClick Click callback
 * @param modifier Optional modifier
 * @param enabled Whether button is enabled
 * @param breatheEnabled Whether breathing animation is active
 * @param content Button content
 */
@Composable
fun BreatheButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    breatheEnabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable RowScope.() -> Unit
) {
    PulseButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        pulseEnabled = breatheEnabled,
        minScale = 0.98f,
        maxScale = 1.02f,
        pulseDurationMs = 3000,
        colors = colors,
        content = content
    )
}

/**
 * SpringButton - Button with spring press animation only
 * 
 * No pulse, just satisfying press feedback.
 * 
 * @param onClick Click callback
 * @param modifier Optional modifier
 * @param enabled Whether button is enabled
 * @param pressScale Scale when pressed (e.g., 0.95 for 5% shrink)
 * @param colors Button colors
 * @param content Button content
 */
@Composable
fun SpringButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    pressScale: Float = 0.95f,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    shape: Shape = ButtonDefaults.shape,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "spring_press"
    )

    Button(
        onClick = onClick,
        modifier = modifier.scale(scale),
        enabled = enabled,
        colors = colors,
        shape = shape,
        interactionSource = interactionSource,
        content = content
    )
}

/**
 * PulseIconButton - Icon button with pulse animation
 * 
 * Good for FABs or important action buttons.
 * 
 * @param onClick Click callback
 * @param modifier Optional modifier
 * @param enabled Whether button is enabled
 * @param pulseEnabled Whether pulse is active
 * @param colors Icon button colors
 * @param content Icon content
 */
@Composable
fun PulseIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    pulseEnabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    content: @Composable () -> Unit
) {
    // Pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "icon_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_pulse_scale"
    )

    // Press animation
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "icon_press"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier.scale(if (pulseEnabled && enabled) scale * pressScale else pressScale),
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        content = content
    )
}

/**
 * PulseFloatingActionButton - FAB with breathing animation
 * 
 * Eye-catching FAB with pulse to draw user attention.
 * 
 * @param onClick Click callback
 * @param modifier Optional modifier
 * @param pulseEnabled Whether pulse is active
 * @param shape FAB shape
 * @param containerColor Container color
 * @param contentColor Content color
 * @param content FAB content
 */
@Composable
fun PulseFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    pulseEnabled: Boolean = true,
    shape: Shape = FloatingActionButtonDefaults.shape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable () -> Unit
) {
    // Pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "fab_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fab_pulse_scale"
    )

    // Press animation
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fab_press"
    )

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.scale(if (pulseEnabled) scale * pressScale else pressScale),
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        interactionSource = interactionSource,
        content = content
    )
}

/**
 * RippleButton - Button with enhanced ripple effect
 * 
 * Standard button with customized ripple for better visual feedback.
 * 
 * @param onClick Click callback
 * @param modifier Optional modifier
 * @param enabled Whether button is enabled
 * @param colors Button colors
 * @param border Optional border
 * @param content Button content
 */
@Composable
fun RippleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    border: BorderStroke? = null,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "ripple_press"
    )

    Button(
        onClick = onClick,
        modifier = modifier.scale(scale),
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        border = border,
        content = content
    )
}
