package com.pharmatech.morocco.features.auth.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pharmatech.morocco.R
import com.pharmatech.morocco.ui.navigation.Screen
import com.pharmatech.morocco.ui.theme.PrimaryGradientEnd
import com.pharmatech.morocco.ui.theme.PrimaryGradientStart
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    var startAnimation by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0) }
    var loadingMessage by remember { mutableStateOf(R.string.loading_initializing) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ), label = "scale"
    )

    val progressAnimation by animateFloatAsState(
        targetValue = progress.toFloat(),
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing
        ),
        label = "progress"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true

        // Simulate loading steps with progress
        val loadingSteps = listOf(
            Pair(20, R.string.loading_initializing),
            Pair(40, R.string.loading_permissions),
            Pair(70, R.string.loading_database),
            Pair(90, R.string.loading_location),
            Pair(100, R.string.loading_complete)
        )

        loadingSteps.forEach { (stepProgress, messageRes) ->
            progress = stepProgress
            loadingMessage = messageRes
            delay(600)
        }

        // Navigate to Home screen
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryGradientStart,
                        PrimaryGradientEnd
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .scale(scale)
                .padding(32.dp)
        ) {
            // App Logo and Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // App icon/emoji
                Text(
                    text = "💊",
                    style = MaterialTheme.typography.displayLarge
                )

                // App name
                Text(
                    text = "TAAWIDATY",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )

                // App tagline
                Text(
                    text = stringResource(id = R.string.app_tagline),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            // Progress Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Loading message
                    Text(
                        text = stringResource(id = loadingMessage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Progress bar container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            )
                    ) {
                        // Progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressAnimation / 100f)
                                .fillMaxHeight()
                                .background(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }

                    // Progress percentage
                    Text(
                        text = "${progress}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Optional loading animation
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        }
    }
}

