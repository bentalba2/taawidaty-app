package com.pharmatech.morocco.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.pharmatech.morocco.core.permissions.rememberLocationPermissionState
import com.pharmatech.morocco.ui.components.CountUpInteger
import com.pharmatech.morocco.ui.components.CountUpPercentage
import com.pharmatech.morocco.ui.components.PulseButton
import com.pharmatech.morocco.ui.components.GlassmorphismCard
import com.pharmatech.morocco.ui.components.GlassElevation
import com.pharmatech.morocco.ui.navigation.Screen
import com.pharmatech.morocco.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val locationPermissionState = rememberLocationPermissionState()

    // Handle location permission changes
    LaunchedEffect(locationPermissionState.hasFineLocation, locationPermissionState.hasCoarseLocation) {
        val granted = locationPermissionState.hasFineLocation || locationPermissionState.hasCoarseLocation
        viewModel.onLocationPermissionResult(granted)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Welcome Back",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "How can we help you today?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                actions = {
                    Row {
                        // Location indicator
                        if (uiState.locationPermissionGranted && uiState.currentLocation != null) {
                            IconButton(
                                onClick = { viewModel.refreshLocation() }
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = "Location enabled",
                                    tint = HealthGreen
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                // Navigate to notifications screen
                                navController.navigate("notifications")
                            }
                        ) {
                            BadgedBox(
                                badge = {
                                    if (uiState.totalMedications > 0 && uiState.remainingCount > 0) {
                                        Badge {
                                            Text(uiState.remainingCount.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick Actions Header
            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Quick Action Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Scan Medication",
                        icon = Icons.Default.QrCodeScanner,
                        gradient = listOf(PrimaryGradientStart, PrimaryGradientEnd),
                        onClick = { navController.navigate(Screen.Scanner.route) }
                    )
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Find Pharmacy",
                        icon = Icons.Default.LocalPharmacy,
                        gradient = listOf(HealthGreen, Color(0xFF059669)),
                        onClick = { navController.navigate(Screen.Pharmacy.route) }
                    )
                }
            }

            // Today's Medications
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Today's Medications",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CountUpInteger(
                                        value = uiState.totalMedications,
                                        textStyle = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        durationMillis = 1000
                                    )
                                    Text(
                                        text = if (uiState.totalMedications == 1) "medication today" else "medications today",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                if (uiState.totalMedications > 0) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CountUpInteger(
                                                value = uiState.takenCount,
                                                textStyle = MaterialTheme.typography.bodyMedium,
                                                color = TaawidatyColors.SuccessGreen500,
                                                durationMillis = 800,
                                                delayMillis = 200
                                            )
                                            Text(
                                                text = "taken",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                        
                                        Text(
                                            text = "•",
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                        )
                                        
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CountUpInteger(
                                                value = uiState.remainingCount,
                                                textStyle = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary,
                                                durationMillis = 800,
                                                delayMillis = 400
                                            )
                                            Text(
                                                text = "remaining",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "You're all set for the day",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                                
                                uiState.nextMedication?.let { next ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        tonalElevation = 0.dp
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "Next dose",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "${next.name} • ${next.dosage}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = next.time,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                }
                            }
                            
                            // Progress indicator with percentage
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(
                                        progress = { uiState.progress.coerceIn(0f, 1f) },
                                        modifier = Modifier.size(64.dp),
                                        color = TaawidatyColors.SuccessGreen500,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                        strokeWidth = 6.dp
                                    )
                                    CountUpPercentage(
                                        percentage = (uiState.progress * 100).coerceIn(0f, 100f),
                                        textStyle = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        durationMillis = 1200,
                                        delayMillis = 300,
                                        decimals = 0
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        PulseButton(
                            onClick = { navController.navigate(Screen.Tracker.route) },
                            modifier = Modifier.fillMaxWidth(),
                            pulseEnabled = uiState.remainingCount > 0
                        ) {
                            Text("View All")
                        }
                    }
                }
            }

            // AI Features
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "AI Health Assistant",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "Symptom Checker",
                        description = "Check your symptoms",
                        icon = Icons.Default.MedicalServices,
                        onClick = { navController.navigate(Screen.AISymptomChecker.route) }
                    )
                    FeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "Health Insights",
                        description = "AI-powered tips",
                        icon = Icons.Default.Insights,
                        onClick = { navController.navigate(Screen.HealthInsights.route) }
                    )
                }
            }

            // Nearby Pharmacies
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nearby Pharmacies",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (uiState.locationPermissionGranted && uiState.currentLocation != null) {
                            Text(
                                text = "Based on your location",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { viewModel.refreshLocation() }
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Refresh location",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        TextButton(onClick = { navController.navigate(Screen.Pharmacy.route) }) {
                            Text("See All")
                        }
                    }
                }
            }

            // Location-based pharmacy content
            when {
                uiState.isLoadingLocation -> {
                    item {
                        GlassmorphismCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(20.dp),
                            elevation = GlassElevation.Medium
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Finding nearby pharmacies...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                !uiState.locationPermissionGranted -> {
                    item {
                        GlassmorphismCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(20.dp),
                            elevation = GlassElevation.Medium
                        ) {
                            Text(
                                text = "Enable location to find nearby pharmacies",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(20.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            PulseButton(
                                onClick = {
                                    if (!locationPermissionState.hasFineLocation && !locationPermissionState.hasCoarseLocation) {
                                        viewModel.requestLocationPermission(context)
                                    } else {
                                        viewModel.loadCurrentLocation()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                pulseEnabled = true
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Enable Location")
                            }
                        }
                    }
                }

                uiState.locationError != null -> {
                    item {
                        GlassmorphismCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                            borderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(20.dp),
                            elevation = GlassElevation.Medium
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = uiState.locationError,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                Button(
                                    onClick = {
                                        viewModel.clearLocationError()
                                        viewModel.loadCurrentLocation()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Try Again")
                                }
                            }
                        }
                    }
                }

                uiState.nearbyPharmacies.isNotEmpty() -> {
                    items(uiState.nearbyPharmacies) { pharmacy ->
                        NearbyPharmacyCard(
                            pharmacy = pharmacy,
                            onClick = {
                                navController.navigate("pharmacy_details/${pharmacy.id}")
                            }
                        )
                    }
                }

                else -> {
                    item {
                        GlassmorphismCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(20.dp),
                            elevation = GlassElevation.Medium
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocalPharmacy,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = "No nearby pharmacies found",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                TextButton(
                                    onClick = { navController.navigate(Screen.Pharmacy.route) }
                                ) {
                                    Text("Browse All Pharmacies")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    gradient: List<androidx.compose.ui.graphics.Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(gradient))
                .padding(16.dp)
        ) {
            Column {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassmorphismCard(
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp),
        elevation = GlassElevation.Soft,
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

