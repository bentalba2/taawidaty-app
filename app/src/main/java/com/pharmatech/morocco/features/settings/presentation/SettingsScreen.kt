package com.pharmatech.morocco.features.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pharmatech.morocco.R
import com.pharmatech.morocco.ui.components.AppleCard
import com.pharmatech.morocco.ui.components.AppleListItem
import com.pharmatech.morocco.ui.components.ApplePrimaryButton
import com.pharmatech.morocco.ui.components.AppleSwitch
import com.pharmatech.morocco.ui.theme.*

/**
 * Apple-style Settings Screen
 * Clean, minimal design with theme options
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = AppleTypography.headlineMedium.copy(
                            color = if (isDarkTheme()) AppleColors.Dark.OnBackground else AppleColors.Light.OnBackground,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isDarkTheme()) AppleColors.Dark.OnBackground else AppleColors.Light.OnBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkTheme()) AppleColors.Dark.Background else AppleColors.Light.Background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(if (isDarkTheme()) AppleColors.Dark.Background else AppleColors.Light.Background)
                .verticalScroll(rememberScrollState())
        ) {
            // Theme Settings Section
            SettingsSection(
                title = "Appearance",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Light Mode Option
                AppleListItem(
                    title = "Light Mode",
                    subtitle = "Use light theme throughout the app",
                    leadingIcon = Icons.Default.LightMode,
                    onClick = {
                        viewModel.setThemeMode(AppleThemeManager.THEME_LIGHT)
                    },
                    trailingIcon = if (uiState.themeMode == AppleThemeManager.THEME_LIGHT) {
                        Icons.Default.Check
                    } else null,
                    showDivider = false
                )

                // Dark Mode Option
                AppleListItem(
                    title = "Dark Mode",
                    subtitle = "Use dark theme throughout the app",
                    leadingIcon = Icons.Default.DarkMode,
                    onClick = {
                        viewModel.setThemeMode(AppleThemeManager.THEME_DARK)
                    },
                    trailingIcon = if (uiState.themeMode == AppleThemeManager.THEME_DARK) {
                        Icons.Default.Check
                    } else null,
                    showDivider = false
                )

                // System Default Option
                AppleListItem(
                    title = "System Default",
                    subtitle = "Automatically switch based on device settings",
                    leadingIcon = Icons.Default.SettingsBrightness,
                    onClick = {
                        viewModel.setThemeMode(AppleThemeManager.THEME_SYSTEM)
                    },
                    trailingIcon = if (uiState.themeMode == AppleThemeManager.THEME_SYSTEM) {
                        Icons.Default.Check
                    } else null,
                    showDivider = false
                )
            }

            // Additional Settings Section
            SettingsSection(
                title = "Preferences",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Dynamic Colors
                AppleListItem(
                    title = "Dynamic Colors",
                    subtitle = "Use dynamic colors on supported devices",
                    leadingIcon = Icons.Default.Palette,
                    onClick = {
                        viewModel.toggleDynamicColors()
                    },
                    showDivider = false
                ) {
                    AppleSwitch(
                        checked = uiState.useDynamicColors,
                        onCheckedChange = { viewModel.setUseDynamicColors(it) }
                    )
                }

                // Haptic Feedback
                AppleListItem(
                    title = "Haptic Feedback",
                    subtitle = "Provide haptic feedback for interactions",
                    leadingIcon = Icons.Default.Vibration,
                    onClick = {
                        viewModel.toggleHapticFeedback()
                    },
                    showDivider = false
                ) {
                    AppleSwitch(
                        checked = uiState.hapticFeedbackEnabled,
                        onCheckedChange = { viewModel.setHapticFeedbackEnabled(it) }
                    )
                }
            }

            // App Info Section
            SettingsSection(
                title = "About",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // App Version
                AppleListItem(
                    title = "Version",
                    subtitle = uiState.appVersion,
                    leadingIcon = Icons.Default.Info,
                    onClick = null,
                    showDivider = false
                )
            }

            // Reset Button
            if (uiState.hasCustomSettings) {
                AppleCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    onClick = {
                        viewModel.resetToDefaults()
                    }
                ) {
                    ApplePrimaryButton(
                        text = "Reset to Default Settings",
                        onClick = {
                            viewModel.resetToDefaults()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Settings Section Header
 */
@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = AppleTypography.titleSmall.copy(
                color = if (isDarkTheme()) AppleColors.Dark.OnSurfaceTertiary else AppleColors.Light.OnSurfaceTertiary,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        AppleCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = 0f
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun isDarkTheme(): Boolean {
    return MaterialTheme.colorScheme.background == AppleColors.Dark.Background
}