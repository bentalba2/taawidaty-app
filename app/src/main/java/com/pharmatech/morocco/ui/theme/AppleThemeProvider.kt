package com.pharmatech.morocco.ui.theme

import android.app.Application
import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.HiltAndroidApp

/**
 * Apple-inspired Theme Provider
 * Provides Apple-like design system with dark mode support
 */

@HiltAndroidApp
class AppleApp : Application()

@Composable
fun AppleThemeProvider(
    darkTheme: Boolean? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    // Determine dark mode based on preference or system setting
    val effectiveDarkMode = when {
        darkTheme != null -> darkTheme
        else -> isSystemInDarkTheme()
    }

    val appleColorScheme = if (effectiveDarkMode) {
        AppleDarkColorScheme
    } else {
        AppleLightColorScheme
    }

    val typography = AppleTypography
    val shapes = MaterialTheme.shapes.copy(
        small = RoundedCornerShape(AppleShapes.CornerRadiusSmall),
        medium = RoundedCornerShape(AppleShapes.CornerRadiusMedium),
        large = RoundedCornerShape(AppleShapes.CornerRadiusLarge)
    )

    MaterialTheme(
        colorScheme = appleColorScheme,
        typography = typography,
        shapes = shapes
    ) {
        content()
    }
}

@Composable
fun AppleThemeProviderWithManager(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val themeManager = remember { AppleThemeManager(context) }
    val isSystemInDarkMode = isSystemInDarkTheme()

    val themeState = rememberAppleThemeState(
        themeManager = themeManager,
        systemDarkMode = isSystemInDarkMode
    )

    CompositionLocalProvider(
        LocalAppleThemeManager provides themeManager,
        LocalAppleThemeState provides themeState
    ) {
        AppleThemeProvider(
            darkTheme = themeState.theme.isDarkMode
        ) {
            content()
        }
    }
}

// Composition locals for theme management
val LocalAppleThemeManager = staticCompositionLocalOf<AppleThemeManager> {
    error("AppleThemeManager not provided")
}

val LocalAppleThemeState = staticCompositionLocalOf<AppleThemeState> {
    error("AppleThemeState not provided")
}

/**
 * Apple Design System Extensions
 */
object AppleDesignSystem {
    @Composable
    fun isDarkMode(): Boolean {
        return MaterialTheme.colorScheme.background == AppleColors.Dark.Background
    }

    @Composable
    fun getCurrentColorScheme(): ColorScheme {
        return MaterialTheme.colorScheme
    }

    @Composable
    fun getCurrentTypography(): androidx.compose.material3.Typography {
        return MaterialTheme.typography
    }

    // Standard spacing values following Apple's design principles
    val spacing = object {
        val xs = 4.dp
        val sm = 8.dp
        val md = 16.dp
        val lg = 24.dp
        val xl = 32.dp
        val xxl = 48.dp
    }

    // Standard elevation values
    val elevation = object {
        val none = 0.dp
        val subtle = 1.dp
        val low = 2.dp
        val medium = 4.dp
        val high = 8.dp
        val prominent = 16.dp
    }

    // Border radius values
    val radius = object {
        val none = 0.dp
        val small = 8.dp
        val medium = 12.dp
        val large = 16.dp
        val extraLarge = 24.dp
        val round = 1000.dp // For circular shapes
    }
}

/**
 * Apple-specific color utilities
 */
@Composable
fun AppleColor(
    light: Color,
    dark: Color
): Color {
    return if (AppleDesignSystem.isDarkMode()) dark else light
}

@Composable
fun AppleSurface(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        content = content
    )
}

/**
 * Apple-style typography shortcuts
 */
@Composable
fun AppleTextStyles() {
    val currentTypography = MaterialTheme.typography

    object TextStyle {
        val largeTitle = currentTypography.displayLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            lineHeight = 41.sp
        )

        val title1 = currentTypography.displayMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 34.sp
        )

        val title2 = currentTypography.displaySmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp
        )

        val title3 = currentTypography.headlineLarge.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 25.sp
        )

        val headline = currentTypography.headlineMedium.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            lineHeight = 22.sp
        )

        val body = currentTypography.bodyLarge.copy(
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp,
            lineHeight = 22.sp
        )

        val callout = currentTypography.bodyMedium.copy(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 21.sp
        )

        val subheadline = currentTypography.bodySmall.copy(
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 20.sp
        )

        val footnote = currentTypography.labelMedium.copy(
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        val caption1 = currentTypography.labelSmall.copy(
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )

        val caption2 = currentTypography.labelSmall.copy(
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            lineHeight = 13.sp
        )
    }
}