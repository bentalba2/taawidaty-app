package com.pharmatech.morocco.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Enhanced bottom navigation with better animations and icons
 */
@Composable
fun EnhancedBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        containerColor = MaterialTheme.colorScheme.surface.copy(
            alpha = 0.95f
        ),
        tonalElevation = 8.dp
    ) {
        BottomNavigationItems.forEach { item ->
            EnhancedNavigationItem(
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

/**
 * Enhanced navigation item with better animations and icons
 */
@Composable
private fun RowScope.EnhancedNavigationItem(
    item: BottomNavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "navScale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 0f else 360f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "navRotation"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        },
        animationSpec = tween(150),
        label = "navIconColor"
    )

    NavigationBarItem(
        icon = {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        rotationZ = rotation
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.label,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )

                // Animated indicator dot
                if (isSelected) {
                    Canvas(
                        modifier = Modifier
                            .size(6.dp)
                            .align(Alignment.BottomCenter)
                            .offset(y = 16.dp)
                    ) {
                        drawCircle(
                            color = MaterialTheme.colorScheme.primary,
                            radius = 3.dp.toPx()
                        )
                    }
                }
            }
        },
        label = item.label,
        selected = isSelected,
        onClick = onClick,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = iconColor,
            unselectedIconColor = iconColor,
            selectedTextColor = iconColor,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            indicatorColor = Color.Transparent
        )
    )
}

/**
 * Bottom navigation items with clear, recognizable icons
 */
data class BottomNavigationItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

object BottomNavigationItems {
    val items = listOf(
        BottomNavigationItem(
            route = "home",
            label = "Home",
            selectedIcon = Icons.Rounded.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavigationItem(
            route = "pharmacy",
            label = "Pharmacies",
            selectedIcon = Icons.Rounded.LocalPharmacy,
            unselectedIcon = Icons.Outlined.LocalPharmacy
        ),
        BottomNavigationItem(
            route = "scanner",
            label = "Scanner",
            selectedIcon = Icons.Rounded.QrCodeScanner,
            unselectedIcon = Icons.Outlined.QrCodeScanner
        ),
        BottomNavigationItem(
            route = "medication",
            label = "Medications",
            selectedIcon = Icons.Rounded.Medication,
            unselectedIcon = Icons.Outlined.Medication
        ),
        BottomNavigationItem(
            route = "profile",
            label = "Profile",
            selectedIcon = Icons.Rounded.Person,
            unselectedIcon = Icons.Outlined.Person
        )
    )

    // Sequential access for navigation
    operator fun get(index: Int): BottomNavigationItem = items[index]
    val size: Int get() = items.size
}