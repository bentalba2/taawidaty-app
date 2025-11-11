package com.pharmatech.morocco.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Apple-inspired Design System
 * Clean, minimal, and refined UI components
 */

// ============================================
// Apple-Inspired Color Palette
// Clean whites, subtle grays, careful blues
// ============================================

object AppleColors {
    // Light Theme Colors
    object Light {
        // Primary Colors - Clean Blue
        val Primary = Color(0xFF007AFF)               // iOS Blue
        val PrimaryVariant = Color(0xFF0056CC)

        // Background Colors
        val Background = Color(0xFFF2F2F7)            // iOS System Background
        val Surface = Color(0xFFFFFFFF)                // Pure White
        val Card = Color(0xFFFFFFFF)

        // Text Colors
        val OnBackground = Color(0xFF000000)           // Pure Black
        val OnSurface = Color(0xFF1C1C1E)             // iOS Label
        val OnSurfaceSecondary = Color(0xFF8E8E93)    // iOS Secondary Label
        val OnSurfaceTertiary = Color(0xFFC7C7CC)     // iOS Tertiary Label

        // Accent Colors
        val Success = Color(0xFF34C759)               // iOS Green
        val Warning = Color(0xFFFF9500)               // iOS Orange
        val Error = Color(0xFFFF3B30)                 // iOS Red
        val Info = Color(0xFF5AC8FA)                  // iOS Light Blue

        // UI Elements
        val Separator = Color(0xFFC6C6C8)             // iOS Separator
        val Fill = Color(0xFFE5E5EA)                  // iOS Fill
        val SystemGray = Color(0xFF8E8E93)            // iOS System Gray
    }

    // Dark Theme Colors
    object Dark {
        // Primary Colors
        val Primary = Color(0xFF0A84FF)               // iOS Blue (Dark)
        val PrimaryVariant = Color(0xFF0979FF)

        // Background Colors
        val Background = Color(0xFF000000)            // Pure Black
        val Surface = Color(0xFF1C1C1E)               // iOS System Background
        val Card = Color(0xFF2C2C2E)                  // iOS Secondary Background

        // Text Colors
        val OnBackground = Color(0xFFFFFFFF)           // Pure White
        val OnSurface = Color(0xFFFFFFFF)             // iOS Label (Dark)
        val OnSurfaceSecondary = Color(0xFFAEAEB2)    // iOS Secondary Label
        val OnSurfaceTertiary = Color(0xFF48484A)     // iOS Tertiary Label

        // Accent Colors
        val Success = Color(0xFF30D158)               // iOS Green (Dark)
        val Warning = Color(0xFFFF9F0A)               // iOS Orange (Dark)
        val Error = Color(0xFFFF453A)                 // iOS Red (Dark)
        val Info = Color(0xFF64D2FF)                  // iOS Light Blue (Dark)

        // UI Elements
        val Separator = Color(0xFF38383A)             // iOS Separator (Dark)
        val Fill = Color(0xFF3A3A3C)                  // iOS Fill (Dark)
        val SystemGray = Color(0xFF8E8E93)            // iOS System Gray
    }
}

// ============================================
// Apple-Inspired Typography
// San Francisco-like system fonts
// ============================================

object AppleTypography {
    val displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 41.sp,
        letterSpacing = 0.37.sp
    )

    val displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.36.sp
    )

    val displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.35.sp
    )

    val headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 25.sp,
        letterSpacing = 0.38.sp
    )

    val headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.43).sp
    )

    val headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.43).sp
    )

    val titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 21.sp
    )

    val titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )

    val titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )

    val bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 22.sp
    )

    val bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 20.sp
    )

    val bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    )

    val labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 20.sp
    )

    val labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 16.sp
    )

    val labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 13.sp
    )
}

// ============================================
// Apple-Inspired Material 3 Color Schemes
// ============================================

val AppleLightColorScheme = lightColorScheme(
    // Primary - iOS Blue
    primary = AppleColors.Light.Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3F2FD),
    onPrimaryContainer = AppleColors.Light.PrimaryVariant,

    // Secondary - iOS Gray
    secondary = AppleColors.Light.SystemGray,
    onSecondary = Color.White,
    secondaryContainer = AppleColors.Light.Fill,
    onSecondaryContainer = AppleColors.Light.OnSurface,

    // Tertiary - iOS Accent Blue
    tertiary = Color(0xFF5856D6), // iOS Indigo
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEFEFF2),
    onTertiaryContainer = Color(0xFF1614A1),

    // Background
    background = AppleColors.Light.Background,
    onBackground = AppleColors.Light.OnBackground,
    surface = AppleColors.Light.Surface,
    onSurface = AppleColors.Light.OnSurface,
    surfaceVariant = AppleColors.Light.Fill,
    onSurfaceVariant = AppleColors.Light.OnSurfaceSecondary,

    // Error
    error = AppleColors.Light.Error,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    // UI Elements
    outline = AppleColors.Light.Separator,
    outlineVariant = AppleColors.Light.Fill,
    scrim = Color.Black.copy(alpha = 0.32f)
)

val AppleDarkColorScheme = darkColorScheme(
    // Primary - iOS Blue (Dark)
    primary = AppleColors.Dark.Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF004599),
    onPrimaryContainer = Color(0xFFD1E4FF),

    // Secondary
    secondary = AppleColors.Dark.SystemGray,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF363638),
    onSecondaryContainer = AppleColors.Dark.OnSurface,

    // Tertiary
    tertiary = Color(0xFF635BDB), // iOS Indigo (Dark)
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF2E2B5C),
    onTertiaryContainer = Color(0xFFE5E0FF),

    // Background
    background = AppleColors.Dark.Background,
    onBackground = AppleColors.Dark.OnBackground,
    surface = AppleColors.Dark.Surface,
    onSurface = AppleColors.Dark.OnSurface,
    surfaceVariant = AppleColors.Dark.Card,
    onSurfaceVariant = AppleColors.Dark.OnSurfaceSecondary,

    // Error
    error = AppleColors.Dark.Error,
    onError = Color.Black,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    // UI Elements
    outline = AppleColors.Dark.Separator,
    outlineVariant = AppleColors.Dark.Fill,
    scrim = Color.Black.copy(alpha = 0.5f)
)

// ============================================
// Apple-Inspired Shapes
// Minimal, clean, consistent radii
// ============================================

object AppleShapes {
    val CornerRadiusSmall = 8.dp
    val CornerRadiusMedium = 12.dp
    val CornerRadiusLarge = 16.dp
    val CornerRadiusExtraLarge = 24.dp

    // Button shapes
    val ButtonShape = androidx.compose.ui.graphics.Shape { size, _ ->
        androidx.compose.ui.graphics.Path().apply {
            val cornerRadius = 12.dp.toPx()
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    androidx.compose.ui.geometry.Rect(0f, 0f, size.width, size.height),
                    androidx.compose.ui.geometry.CornerRadius(cornerRadius)
                )
            )
        }
    }

    // Card shapes
    val CardShape = androidx.compose.ui.graphics.Shape { size, _ ->
        androidx.compose.ui.graphics.Path().apply {
            val cornerRadius = 16.dp.toPx()
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    androidx.compose.ui.geometry.Rect(0f, 0f, size.width, size.height),
                    androidx.compose.ui.geometry.CornerRadius(cornerRadius)
                )
            )
        }
    }

    // Navigation bar shape
    val NavigationBarShape = androidx.compose.ui.graphics.Shape { size, _ ->
        androidx.compose.ui.graphics.Path().apply {
            val cornerRadius = 20.dp.toPx()
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    androidx.compose.ui.geometry.Rect(0f, 0f, size.width, size.height),
                    androidx.compose.ui.geometry.CornerRadius(cornerRadius)
                )
            )
        }
    }
}

// ============================================
// Apple-Inspired Easing
// Natural, smooth animations
// ============================================

object AppleEasing {
    val EaseInOut = androidx.compose.animation.core.EaseInOutCubic
    val EaseOut = androidx.compose.animation.core.EaseOutCubic
    val EaseIn = androidx.compose.animation.core.EaseInCubic

    // Spring animations for natural feel
    val Spring = androidx.compose.animation.core.Spring(
        dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
        stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
    )

    val SpringStiff = androidx.compose.animation.core.Spring(
        dampingRatio = androidx.compose.animation.core.Spring.DampingRatioLowBouncy,
        stiffness = androidx.compose.animation.core.Spring.StiffnessHigh
    )
}