package com.pharmatech.morocco.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pharmatech.morocco.core.datastore.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGradientStart,
    onPrimary = Color.White,
    primaryContainer = PrimaryGradientEnd,
    onPrimaryContainer = Color.White,
    secondary = HealthGreen,
    onSecondary = Color.White,
    secondaryContainer = HealthGreen,
    onSecondaryContainer = Color.White,
    tertiary = PremiumGold,
    onTertiary = NeuralDark,
    tertiaryContainer = PremiumGold,
    onTertiaryContainer = NeuralDark,
    background = NeuralDark,
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),
    outline = Gray600,
    outlineVariant = Gray700,
    scrim = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGradientStart,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF1E293B),
    secondary = HealthGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF064E3B),
    tertiary = PremiumGold,
    onTertiary = NeuralDark,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF78350F),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray700,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
    outline = Gray400,
    outlineVariant = Gray200,
    scrim = Color.Black
)

/**
 * TaawidatyThemeProvider - Main theme composable with DataStore integration
 * 
 * Automatically observes theme preference and applies correct theme.
 * Supports System/Light/Dark modes with persistent storage.
 */
@Composable
fun TaawidatyThemeProvider(
    themeViewModel: ThemeViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
    val systemInDarkTheme = isSystemInDarkTheme()

    // Determine if dark theme should be active
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDarkTheme
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    TaawidatyTheme(
        darkTheme = darkTheme,
        content = content
    )
}

/**
 * TaawidatyTheme - Base theme composable
 * 
 * Apply TAAWIDATY color scheme, typography, and shapes.
 * For direct use when theme preference is handled externally.
 */
@Composable
fun TaawidatyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to always use TAAWIDATY brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> TaawidatyDarkColorScheme
        else -> TaawidatyLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TaawidatyTypography,
        shapes = TaawidatyShapes,
        content = content
    )

}

