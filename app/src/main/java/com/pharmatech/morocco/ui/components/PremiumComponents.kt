package com.pharmatech.morocco.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pharmatech.morocco.R
import com.pharmatech.morocco.ui.theme.*

/**
 * Premium responsive button with haptic feedback and smooth animations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "",
    icon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    variant: PremiumButtonVariant = PremiumButtonVariant.Primary,
    size: PremiumButtonSize = PremiumButtonSize.Medium,
    hapticFeedback: Boolean = true,
    colors: PremiumButtonColors? = null
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val targetScale = when {
        !enabled || loading -> 0.95f
        isPressed -> 0.97f
        else -> 1f
    }

    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(
            durationMillis = 100,
            easing = FastOutSlowInEasing
        ),
        label = "buttonScale"
    )

    val elevation by animateDpAsState(
        targetValue = when {
            !enabled || loading -> 0.dp
            isPressed -> 2.dp
            else -> 8.dp
        },
        animationSpec = tween(
            durationMillis = 150,
            easing = FastOutSlowInEasing
        ),
        label = "buttonElevation"
    )

    val buttonColors = colors ?: PremiumButtonColors.defaultForVariant(variant)

    Box(
        modifier = modifier
            .defaultMinSize(
                minHeight = when (size) {
                    PremiumButtonSize.Small -> 40.dp
                    PremiumButtonSize.Medium -> 48.dp
                    PremiumButtonSize.Large -> 56.dp
                }
            )
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(12.dp),
                ambientColor = buttonColors.shadowColor.copy(alpha = 0.3f),
                spotColor = buttonColors.shadowColor.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = when {
                    !enabled -> Brush.horizontalGradient(
                        colors = listOf(
                            buttonColors.disabledBackgroundColor,
                            buttonColors.disabledBackgroundColor
                        )
                    )
                    isPressed -> Brush.horizontalGradient(
                        colors = listOf(
                            buttonColors.pressedBackgroundColor,
                            buttonColors.pressedBackgroundColor
                        )
                    )
                    loading -> Brush.horizontalGradient(
                        colors = listOf(
                            buttonColors.loadingBackgroundColor,
                            buttonColors.loadingBackgroundColor
                        )
                    )
                    else -> Brush.horizontalGradient(
                        colors = buttonColors.backgroundColorGradient
                    )
                }
            )
            .clickable(
                onClick = {
                    if (enabled && !loading) {
                        if (hapticFeedback) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        onClick()
                    }
                },
                enabled = enabled && !loading,
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = true,
                    radius = 200.dp,
                    color = buttonColors.rippleColor
                )
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = buttonColors.contentColor
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
                        tint = buttonColors.contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (text.isNotEmpty()) {
                    Text(
                        text = text,
                        style = when (size) {
                            PremiumButtonSize.Small -> MaterialTheme.typography.labelMedium
                            PremiumButtonSize.Medium -> MaterialTheme.typography.labelLarge
                            PremiumButtonSize.Large -> MaterialTheme.typography.titleSmall
                        },
                        color = buttonColors.contentColor,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Premium floating action button with smooth animations
 */
@Composable
fun PremiumFloatingActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    extended: Boolean = false,
    text: String = "",
    hapticFeedback: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val rotation by animateFloatAsState(
        targetValue = if (isPressed) 15f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "fabRotation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(
            durationMillis = 150,
            easing = FastOutSlowInEasing
        ),
        label = "fabScale"
    )

    FloatingActionButton(
        onClick = {
            if (hapticFeedback) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            onClick()
        },
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
            },
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        if (extended) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Premium card with enhanced shadows and smooth interactions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation: Dp = 4.dp,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val targetElevation = when {
        !enabled -> 0.dp
        isPressed -> elevation + 4.dp
        else -> elevation
    }

    val animatedElevation by animateDpAsState(
        targetValue = targetElevation,
        animationSpec = tween(150),
        label = "cardElevation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(100),
        label = "cardScale"
    )

    Card(
        onClick = onClick ?: {},
        enabled = enabled,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .let { modifier ->
                if (onClick != null) {
                    modifier.clickable(
                        interactionSource = interactionSource,
                        indication = rememberRipple(
                            bounded = true,
                            radius = 200.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClick()
                    }
                } else modifier
            },
        shape = shape,
        colors = colors,
        elevation = CardDefaults.cardElevation(
            defaultElevation = animatedElevation
        )
    ) {
        content()
    }
}

/**
 * Premium input field with better visual feedback
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardType: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsPressedAsState()

    val borderWidth by animateDpAsState(
        targetValue = when {
            isError -> 2.dp
            isFocused -> 2.dp
            else -> 1.dp
        },
        animationSpec = tween(150),
        label = "borderWidth"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> MaterialTheme.colorScheme.error
            isFocused -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        },
        animationSpec = tween(150),
        label = "borderColor"
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label?.let { { Text(it) } },
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        trailingIcon = trailingIcon?.let {
            {
                IconButton(onClick = { onTrailingIconClick?.invoke() }) {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor,
            errorBorderColor = borderColor
        ),
        shape = RoundedCornerShape(12.dp),
        isError = isError,
        enabled = enabled,
        singleLine = singleLine,
        keyboardOptions = keyboardType,
        visualTransformation = visualTransformation,
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

/**
 * Premium navigation item with better touch feedback
 */
@Composable
fun PremiumNavigationItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null,
    hapticFeedback: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else if (selected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "navItemScale"
    )

    val iconColor by animateColorAsState(
        targetValue = when {
            selected -> MaterialTheme.colorScheme.primary
            isPressed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        },
        animationSpec = tween(150),
        label = "iconColor"
    )

    Box(
        modifier = modifier
            .size(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = false,
                    radius = 28.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            ) {
                if (hapticFeedback) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        BadgedBox(
            badge = {
                badge?.let {
                    Badge {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Premium switch with smooth animations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hapticFeedback: Boolean = true
) {
    val haptic = LocalHapticFeedback.current

    Switch(
        checked = checked,
        onCheckedChange = {
            if (hapticFeedback) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            onCheckedChange(it)
        },
        modifier = modifier,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        thumbContent = {
            Box(
                modifier = Modifier.size(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (checked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    )
}

/**
 * Enum classes for button variants and sizes
 */
enum class PremiumButtonVariant {
    Primary, Secondary, Success, Warning, Error, Outline
}

enum class PremiumButtonSize {
    Small, Medium, Large
}

/**
 * Premium button colors
 */
data class PremiumButtonColors(
    val backgroundColorGradient: List<Color>,
    val contentColor: Color,
    val disabledBackgroundColor: Color,
    val pressedBackgroundColor: Color,
    val loadingBackgroundColor: Color,
    val rippleColor: Color,
    val shadowColor: Color
) {
    companion object {
        fun defaultForVariant(variant: PremiumButtonVariant): PremiumButtonColors {
            return when (variant) {
                PremiumButtonVariant.Primary -> PremiumButtonColors(
                    backgroundColorGradient = listOf(PrimaryGradientStart, PrimaryGradientEnd),
                    contentColor = Color.White,
                    disabledBackgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    pressedBackgroundColor = PrimaryGradientEnd,
                    loadingBackgroundColor = PrimaryGradientStart,
                    rippleColor = Color.White.copy(alpha = 0.3f),
                    shadowColor = MaterialTheme.colorScheme.primary
                )
                PremiumButtonVariant.Secondary -> PremiumButtonColors(
                    backgroundColorGradient = listOf(
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.secondaryContainer
                    ),
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    disabledBackgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    pressedBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    loadingBackgroundColor = MaterialTheme.colorScheme.secondary,
                    rippleColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                    shadowColor = MaterialTheme.colorScheme.secondary
                )
                PremiumButtonVariant.Success -> PremiumButtonColors(
                    backgroundColorGradient = listOf(HealthGreen, Color(0xFF059669)),
                    contentColor = Color.White,
                    disabledBackgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    pressedBackgroundColor = Color(0xFF059669),
                    loadingBackgroundColor = HealthGreen,
                    rippleColor = Color.White.copy(alpha = 0.3f),
                    shadowColor = HealthGreen
                )
                PremiumButtonVariant.Warning -> PremiumButtonColors(
                    backgroundColorGradient = listOf(PremiumGold, Color(0xFFD97706)),
                    contentColor = Color.White,
                    disabledBackgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    pressedBackgroundColor = Color(0xFFD97706),
                    loadingBackgroundColor = PremiumGold,
                    rippleColor = Color.White.copy(alpha = 0.3f),
                    shadowColor = PremiumGold
                )
                PremiumButtonVariant.Error -> PremiumButtonColors(
                    backgroundColorGradient = listOf(
                        MaterialTheme.colorScheme.error,
                        MaterialTheme.colorScheme.errorContainer
                    ),
                    contentColor = MaterialTheme.colorScheme.onError,
                    disabledBackgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    pressedBackgroundColor = MaterialTheme.colorScheme.errorContainer,
                    loadingBackgroundColor = MaterialTheme.colorScheme.error,
                    rippleColor = MaterialTheme.colorScheme.onError.copy(alpha = 0.3f),
                    shadowColor = MaterialTheme.colorScheme.error
                )
                PremiumButtonVariant.Outline -> PremiumButtonColors(
                    backgroundColorGradient = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface
                    ),
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledBackgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    pressedBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    loadingBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    rippleColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shadowColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}