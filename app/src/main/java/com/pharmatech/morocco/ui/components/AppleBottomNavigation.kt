package com.pharmatech.morocco.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Apple-style Bottom Navigation
 * Clean, minimal with smooth animations
 */
@Composable
fun AppleBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            containerColor = if (isDarkTheme()) {
                AppleColors.Dark.Surface
            } else {
                AppleColors.Light.Surface
            },
            tonalElevation = 0.dp
        ) {
            AppleNavigationItems.items.forEach { item ->
                AppleNavigationItem(
                    item = item,
                    isSelected = currentRoute == item.route,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNavigate(item.route)
                    }
                )
            }
        }
    }
}

/**
 * Apple-style Navigation Item
 * Minimal design with smooth animations
 */
@Composable
private fun RowScope.AppleNavigationItem(
    item: AppleNavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "navScale"
    )

    val iconAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.6f,
        animationSpec = tween(300, easing = EaseInOutCubic),
        label = "navIconAlpha"
    )

    val iconTint by animateColorAsState(
        targetValue = if (isSelected) {
            if (isDarkTheme()) AppleColors.Dark.Primary else AppleColors.Light.Primary
        } else {
            if (isDarkTheme()) AppleColors.Dark.OnSurfaceTertiary else AppleColors.Light.OnSurfaceTertiary
        },
        animationSpec = tween(300, easing = EaseInOutCubic),
        label = "navIconTint"
    )

    NavigationBarItem(
        icon = {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.label,
                    tint = iconTint,
                    modifier = Modifier
                        .size(24.dp)
                        .alpha(iconAlpha)
                )

                // Subtle indicator dot for selected item
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .align(Alignment.BottomCenter)
                            .offset(y = 20.dp)
                            .background(
                                color = if (isDarkTheme()) AppleColors.Dark.Primary else AppleColors.Light.Primary,
                                shape = CircleShape
                            )
                    )
                }
            }
        },
        label = item.label,
        selected = isSelected,
        onClick = onClick,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = iconTint,
            unselectedIconColor = iconTint,
            selectedTextColor = iconTint,
            unselectedTextColor = iconTint,
            indicatorColor = Color.Transparent
        )
    )
}

/**
 * Apple Navigation Items
 * Clean, recognizable icons
 */
data class AppleNavigationItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

object AppleNavigationItems {
    val items = listOf(
        AppleNavigationItem(
            route = "home",
            label = "Home",
            selectedIcon = Icons.Rounded.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        AppleNavigationItem(
            route = "pharmacy",
            label = "Pharmacies",
            selectedIcon = Icons.Rounded.LocalPharmacy,
            unselectedIcon = Icons.Outlined.LocalPharmacy
        ),
        AppleNavigationItem(
            route = "scanner",
            label = "Scanner",
            selectedIcon = Icons.Rounded.QrCodeScanner,
            unselectedIcon = Icons.Outlined.QrCodeScanner
        ),
        AppleNavigationItem(
            route = "medication",
            label = "Medications",
            selectedIcon = Icons.Rounded.Medication,
            unselectedIcon = Icons.Outlined.Medication
        ),
        AppleNavigationItem(
            route = "profile",
            label = "Profile",
            selectedIcon = Icons.Rounded.Person,
            unselectedIcon = Icons.Outlined.Person
        )
    )

    // Sequential access
    operator fun get(index: Int): AppleNavigationItem = items[index]
    val size: Int get() = items.size
}

@Composable
private fun isDarkTheme(): Boolean {
    return MaterialTheme.colorScheme.background == AppleColors.Dark.Background
}