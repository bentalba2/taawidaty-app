package com.pharmatech.morocco.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ============================================
// TAAWIDATY Brand Colors
// Trust Blue - Professional, Reliable, Modern
// ============================================

object TaawidatyColors {
    // Primary - Trust Blue (Main Brand Color)
    val TrustBlue50 = Color(0xFFE3F2FD)
    val TrustBlue100 = Color(0xFFBBDEFB)
    val TrustBlue200 = Color(0xFF90CAF9)
    val TrustBlue300 = Color(0xFF64B5F6)
    val TrustBlue400 = Color(0xFF42A5F5)
    val TrustBlue500 = Color(0xFF0077BE)  // Primary Brand
    val TrustBlue600 = Color(0xFF0066A6)
    val TrustBlue700 = Color(0xFF00558E)
    val TrustBlue800 = Color(0xFF004476)
    val TrustBlue900 = Color(0xFF00335E)

    // Secondary - Success Green (Health, Wellness)
    val SuccessGreen50 = Color(0xFFE8F5E9)
    val SuccessGreen100 = Color(0xFFC8E6C9)
    val SuccessGreen200 = Color(0xFFA5D6A7)
    val SuccessGreen300 = Color(0xFF81C784)
    val SuccessGreen400 = Color(0xFF66BB6A)
    val SuccessGreen500 = Color(0xFF4CAF50)  // Success Green
    val SuccessGreen600 = Color(0xFF43A047)
    val SuccessGreen700 = Color(0xFF388E3C)
    val SuccessGreen800 = Color(0xFF2E7D32)
    val SuccessGreen900 = Color(0xFF1B5E20)

    // Tertiary - Prestige Gold (Premium, Excellence)
    val PrestigeGold50 = Color(0xFFFFFBE6)
    val PrestigeGold100 = Color(0xFFFFF4CC)
    val PrestigeGold200 = Color(0xFFFFE999)
    val PrestigeGold300 = Color(0xFFFFDD66)
    val PrestigeGold400 = Color(0xFFFFD633)
    val PrestigeGold500 = Color(0xFFD4AF37)  // Prestige Gold
    val PrestigeGold600 = Color(0xFFB8982F)
    val PrestigeGold700 = Color(0xFF9C8127)
    val PrestigeGold800 = Color(0xFF806A1F)
    val PrestigeGold900 = Color(0xFF645317)

    // Semantic Colors
    val ErrorRed = Color(0xFFDC3545)
    val WarningOrange = Color(0xFFFFC107)
    val InfoCyan = Color(0xFF17A2B8)

    // Neutral Grays (Light Mode)
    val Gray50 = Color(0xFFFAFAFA)
    val Gray100 = Color(0xFFF5F5F5)
    val Gray200 = Color(0xFFEEEEEE)
    val Gray300 = Color(0xFFE0E0E0)
    val Gray400 = Color(0xFFBDBDBD)
    val Gray500 = Color(0xFF9E9E9E)
    val Gray600 = Color(0xFF757575)
    val Gray700 = Color(0xFF616161)
    val Gray800 = Color(0xFF424242)
    val Gray900 = Color(0xFF212121)

    // Dark Mode Specific
    val DarkBackground = Color(0xFF0A1929)
    val DarkSurface = Color(0xFF0F1E2E)
    val DarkCard = Color(0xFF152535)
    val DarkBorder = Color(0xFF1A2F3F)
}

// ============================================
// Enhanced Light Theme Color Scheme
// Eye-Friendly, Better Contrast, Professional
// ============================================

val TaawidatyLightColorScheme = lightColorScheme(
    // Primary - Softer Trust Blue for eye comfort
    primary = Color(0xFF0061A4),  // Softer blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3F2FD),  // Light blue container
    onPrimaryContainer = Color(0xFF001D36),

    // Secondary - Calming Green for health
    secondary = Color(0xFF2E7D32),  // Medical green
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E9),  // Soft green
    onSecondaryContainer = Color(0xFF0D2F0F),

    // Tertiary - Warm accent gold
    tertiary = Color(0xFFB8860B),  // Warm gold
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFF8E1),  // Warm cream
    onTertiaryContainer = Color(0xFF3E2B05),

    // Background & Surface - Softer on eyes
    background = Color(0xFFF8FAFB),  // Very light blue-white
    onBackground = Color(0xFF1C1B1F),  // Soft black
    surface = Color(0xFFFAFCFE),  // Cool white
    onSurface = Color(0xFF1C1B1F),  // Consistent text color
    surfaceVariant = Color(0xFFF0F4F8),  // Light gray-blue
    onSurfaceVariant = Color(0xFF44474E),  // Softer gray

    // Error - Less harsh red
    error = Color(0xFFBA1A1A),  // Softer red
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),  // Light red
    onErrorContainer = Color(0xFF410002),

    // Outline - Better visibility
    outline = Color(0xFF73777F),  // Clear outline
    outlineVariant = Color(0xFFC7C7CE),  // Soft outline
    scrim = Color(0xFF000000)
)

// ============================================
// Dark Theme Color Scheme
// Modern, Comfortable, Eye-Friendly
// ============================================

val TaawidatyDarkColorScheme = darkColorScheme(
    // Primary - Trust Blue (Lighter for dark mode)
    primary = TaawidatyColors.TrustBlue300,
    onPrimary = TaawidatyColors.TrustBlue900,
    primaryContainer = TaawidatyColors.TrustBlue800,
    onPrimaryContainer = TaawidatyColors.TrustBlue100,

    // Secondary - Success Green (Lighter for dark mode)
    secondary = TaawidatyColors.SuccessGreen300,
    onSecondary = TaawidatyColors.SuccessGreen900,
    secondaryContainer = TaawidatyColors.SuccessGreen800,
    onSecondaryContainer = TaawidatyColors.SuccessGreen100,

    // Tertiary - Prestige Gold (Adjusted for dark mode)
    tertiary = TaawidatyColors.PrestigeGold300,
    onTertiary = TaawidatyColors.PrestigeGold900,
    tertiaryContainer = TaawidatyColors.PrestigeGold800,
    onTertiaryContainer = TaawidatyColors.PrestigeGold100,

    // Background & Surface
    background = TaawidatyColors.DarkBackground,
    onBackground = Color(0xFFE3E8EF),
    surface = TaawidatyColors.DarkSurface,
    onSurface = Color(0xFFE3E8EF),
    surfaceVariant = TaawidatyColors.DarkCard,
    onSurfaceVariant = TaawidatyColors.Gray400,

    // Error
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    // Outline
    outline = TaawidatyColors.Gray600,
    outlineVariant = TaawidatyColors.DarkBorder,
    scrim = Color.Black
)

// ============================================
// Legacy Compatibility Layer
// Maintains backward compatibility with existing code
// TODO: Migrate all usages to TaawidatyColors
// ============================================

@Deprecated("Use TaawidatyColors instead", ReplaceWith("TaawidatyColors"))
object ShifaaColors {
    val Gold = TaawidatyColors.PrestigeGold500
    val GoldLight = TaawidatyColors.PrestigeGold300
    val GoldDark = TaawidatyColors.PrestigeGold700
    
    val PharmacyGreen = TaawidatyColors.SuccessGreen500
    val PharmacyGreenLight = TaawidatyColors.SuccessGreen300
    val PharmacyGreenDark = TaawidatyColors.SuccessGreen700
    
    val TealDark = TaawidatyColors.TrustBlue800
    val TealMedium = TaawidatyColors.TrustBlue600
    val TealLight = TaawidatyColors.TrustBlue400
    
    val Emerald = TaawidatyColors.SuccessGreen400
    val DarkGreen = TaawidatyColors.SuccessGreen900
    
    val IvoryWhite = Color(0xFFFAFBFC)
    val CreamWhite = TaawidatyColors.Gray50
    val CharcoalBlack = TaawidatyColors.Gray900
    val WarmGray = TaawidatyColors.Gray700
}

@Deprecated("Use TaawidatyLightColorScheme instead", ReplaceWith("TaawidatyLightColorScheme"))
val ShifaaLightColorScheme = TaawidatyLightColorScheme

@Deprecated("Use TaawidatyDarkColorScheme instead", ReplaceWith("TaawidatyDarkColorScheme"))
val ShifaaDarkColorScheme = TaawidatyDarkColorScheme

// ============================================
// Extended Semantic Colors
// For specific use cases (alerts, status, etc.)
// ============================================

object TaawidatySemanticColors {
    // Hospital & Medical
    val HospitalRed = Color(0xFFDC3545)
    val ClinicBlue = TaawidatyColors.TrustBlue600
    val MedicalGreen = TaawidatyColors.SuccessGreen600
    
    // Status Colors
    val SuccessGreen = TaawidatyColors.SuccessGreen500
    val WarningOrange = TaawidatyColors.WarningOrange
    val ErrorRed = TaawidatyColors.ErrorRed
    val InfoBlue = TaawidatyColors.InfoCyan
    
    // Insurance & Finance
    val TrustBlue = TaawidatyColors.TrustBlue500
    val PremiumGold = TaawidatyColors.PrestigeGold500
}

@Deprecated("Use TaawidatySemanticColors instead", ReplaceWith("TaawidatySemanticColors"))
object ShifaaExtendedColors {
    val hospitalRed = TaawidatySemanticColors.HospitalRed
    val clinicBlue = TaawidatySemanticColors.ClinicBlue
    val successGreen = TaawidatySemanticColors.SuccessGreen
    val warningOrange = TaawidatySemanticColors.WarningOrange
    val infoBlue = TaawidatySemanticColors.InfoBlue
}

// ============================================
// Enhanced Gradient & UI Colors
// Better Visual Appeal, Modern Design
// ============================================

// Primary Gradients - Soft and Professional
val PrimaryGradientStart = Color(0xFF4FC3F7)  // Light blue
val PrimaryGradientEnd = Color(0xFF0061A4)    // Softer dark blue

// Health Gradients - Calming and Medical
val HealthGradientStart = Color(0xFF81C784)   // Light green
val HealthGradientEnd = Color(0xFF2E7D32)     // Medical green

// Premium Gradients - Warm and Elegant
val PremiumGradientStart = Color(0xFFFFD54F)  // Warm gold
val PremiumGradientEnd = Color(0xFFB8860B)    // Deep gold

// Semantic Colors with Better Eye Comfort
val HealthGreen = Color(0xFF2E7D32)           // Medical green
val PremiumGold = Color(0xFFB8860B)           // Warm gold
val NeuralDark = Color(0xFF1A237E)            // Soft dark blue
val ErrorRed = Color(0xFFBA1A1A)              // Softer error red

// Additional UI Colors
val SurfaceGlass = Color(0xFAFFFFFF)          // Glass morphism white
val SurfaceOverlay = Color(0x0A000000)        // Subtle overlay
val AccentBlue = Color(0xFF03A9F4)            // Bright accent
val SoftGray = Color(0xFFF5F7FA)             // Very light gray

// Neutral Grays (Legacy)
val Gray100 = TaawidatyColors.Gray100
val Gray200 = TaawidatyColors.Gray200
val Gray400 = TaawidatyColors.Gray400
val Gray600 = TaawidatyColors.Gray600
val Gray700 = TaawidatyColors.Gray700

