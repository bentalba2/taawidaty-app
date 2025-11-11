package com.pharmatech.morocco.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.text.DecimalFormat
import kotlin.math.roundToInt

/**
 * CountUpAnimation - Animated number counter from 0 to target value
 * 
 * Uses spring physics for natural counting animation.
 * Optimized with derivedStateOf for performance.
 * 
 * @param targetValue Final number to count up to
 * @param modifier Optional modifier
 * @param textStyle Text style
 * @param color Text color
 * @param fontSize Font size
 * @param fontWeight Font weight
 * @param durationMillis Animation duration
 * @param delayMillis Delay before starting animation
 * @param decimals Number of decimal places (0 for integers)
 * @param prefix Optional prefix (e.g., "$")
 * @param suffix Optional suffix (e.g., " DH")
 * @param useGrouping Whether to use thousand separators
 */
@Composable
fun CountUpAnimation(
    targetValue: Number,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    durationMillis: Int = 1000,
    delayMillis: Long = 0L,
    decimals: Int = 0,
    prefix: String = "",
    suffix: String = "",
    useGrouping: Boolean = true
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Delayed start
    LaunchedEffect(Unit) {
        if (delayMillis > 0) {
            delay(delayMillis)
        }
        startAnimation = true
    }

    // Animate the value with spring physics
    val animatedValue by animateFloatAsState(
        targetValue = if (startAnimation) targetValue.toFloat() else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "count_up_animation"
    )

    // Format the number
    val displayText by remember {
        derivedStateOf {
            formatNumber(animatedValue, decimals, prefix, suffix, useGrouping)
        }
    }

    Text(
        text = displayText,
        modifier = modifier,
        style = textStyle.copy(
            fontSize = if (fontSize != TextUnit.Unspecified) fontSize else textStyle.fontSize,
            fontWeight = fontWeight ?: textStyle.fontWeight
        ),
        color = color
    )
}

/**
 * CountUpCurrency - Specialized count-up for currency values
 * 
 * Pre-configured for currency display with proper formatting.
 * 
 * @param amount Currency amount
 * @param currency Currency symbol/code
 * @param modifier Optional modifier
 * @param textStyle Text style
 * @param color Text color
 * @param durationMillis Animation duration
 * @param delayMillis Delay before starting
 */
@Composable
fun CountUpCurrency(
    amount: Double,
    currency: String = "DH",
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.headlineMedium,
    color: Color = MaterialTheme.colorScheme.primary,
    durationMillis: Int = 1200,
    delayMillis: Long = 0L
) {
    CountUpAnimation(
        targetValue = amount,
        modifier = modifier,
        textStyle = textStyle,
        color = color,
        durationMillis = durationMillis,
        delayMillis = delayMillis,
        decimals = 2,
        suffix = " $currency",
        useGrouping = true
    )
}

/**
 * CountUpPercentage - Count-up for percentage values
 * 
 * @param percentage Percentage value (0-100)
 * @param modifier Optional modifier
 * @param textStyle Text style
 * @param color Text color
 * @param durationMillis Animation duration
 * @param delayMillis Delay before starting
 * @param decimals Decimal places
 */
@Composable
fun CountUpPercentage(
    percentage: Float,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    color: Color = MaterialTheme.colorScheme.primary,
    durationMillis: Int = 800,
    delayMillis: Long = 0L,
    decimals: Int = 1
) {
    CountUpAnimation(
        targetValue = percentage,
        modifier = modifier,
        textStyle = textStyle,
        color = color,
        durationMillis = durationMillis,
        delayMillis = delayMillis,
        decimals = decimals,
        suffix = "%",
        useGrouping = false
    )
}

/**
 * CountUpInteger - Count-up for integer values
 * 
 * Optimized for whole numbers (no decimals).
 * 
 * @param value Integer value
 * @param modifier Optional modifier
 * @param textStyle Text style
 * @param color Text color
 * @param durationMillis Animation duration
 * @param delayMillis Delay before starting
 * @param prefix Optional prefix
 * @param suffix Optional suffix
 */
@Composable
fun CountUpInteger(
    value: Int,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.displaySmall,
    color: Color = MaterialTheme.colorScheme.onSurface,
    durationMillis: Int = 1000,
    delayMillis: Long = 0L,
    prefix: String = "",
    suffix: String = ""
) {
    CountUpAnimation(
        targetValue = value,
        modifier = modifier,
        textStyle = textStyle,
        color = color,
        durationMillis = durationMillis,
        delayMillis = delayMillis,
        decimals = 0,
        prefix = prefix,
        suffix = suffix,
        useGrouping = true
    )
}

/**
 * StaggeredCountUpRow - Multiple count-up animations with stagger
 * 
 * For displaying multiple statistics that count up in sequence.
 * 
 * @param values List of values to animate
 * @param staggerDelayMs Delay between each animation start
 * @param content Content for each value with its index
 */
@Composable
fun StaggeredCountUpRow(
    values: List<Number>,
    staggerDelayMs: Long = 100L,
    content: @Composable (value: Number, index: Int, delayMs: Long) -> Unit
) {
    values.forEachIndexed { index, value ->
        val delay = index * staggerDelayMs
        content(value, index, delay)
    }
}

/**
 * Format number helper function
 */
private fun formatNumber(
    value: Float,
    decimals: Int,
    prefix: String,
    suffix: String,
    useGrouping: Boolean
): String {
    val formatter = if (decimals > 0) {
        DecimalFormat("#,##0.${"0".repeat(decimals)}")
    } else {
        DecimalFormat("#,##0")
    }
    
    formatter.isGroupingUsed = useGrouping
    
    val formattedNumber = if (decimals > 0) {
        formatter.format(value.toDouble())
    } else {
        formatter.format(value.roundToInt())
    }
    
    return "$prefix$formattedNumber$suffix"
}

/**
 * AnimatedStatCard - Card with count-up animation for statistics
 * 
 * Pre-built card component for displaying animated stats.
 * 
 * @param value Numeric value to display
 * @param label Label/description
 * @param modifier Optional modifier
 * @param icon Optional icon
 * @param color Accent color
 * @param decimals Number of decimal places
 * @param prefix Value prefix
 * @param suffix Value suffix
 */
@Composable
fun AnimatedStatCard(
    value: Number,
    label: String,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    color: Color = MaterialTheme.colorScheme.primary,
    decimals: Int = 0,
    prefix: String = "",
    suffix: String = ""
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon?.invoke()
            
            if (icon != null) {
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            CountUpAnimation(
                targetValue = value,
                textStyle = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Bold,
                decimals = decimals,
                prefix = prefix,
                suffix = suffix
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
