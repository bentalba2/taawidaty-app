package com.pharmatech.morocco.features.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pharmatech.morocco.ui.theme.ShifaaColors

/**
 * Coming Soon Screen for AI Features
 * Used for AI Symptom Checker and Health Insights
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AISymptomCheckerScreen(navController: NavController) {
    AIFeatureComingSoon(
        navController = navController,
        title = "AI Symptom Checker",
        icon = Icons.Default.MedicalServices,
        description = "Our AI-powered symptom checker will help you understand your symptoms and provide personalized health guidance.",
        features = listOf(
            "Describe your symptoms in natural language",
            "Get instant preliminary assessment",
            "Receive medication recommendations",
            "Find nearby healthcare providers",
            "Track symptom history"
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthInsightsScreen(navController: NavController) {
    AIFeatureComingSoon(
        navController = navController,
        title = "Health Insights",
        icon = Icons.Default.Insights,
        description = "Get AI-powered personalized health tips, medication reminders, and wellness recommendations tailored to your needs.",
        features = listOf(
            "Personalized health recommendations",
            "Medication interaction warnings",
            "Wellness tips and advice",
            "Health trend analysis",
            "Preventive care reminders"
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AIFeatureComingSoon(
    navController: NavController,
    title: String,
    icon: ImageVector,
    description: String,
    features: List<String>
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ShifaaColors.TealDark,
                    titleContentColor = ShifaaColors.GoldLight,
                    navigationIconContentColor = ShifaaColors.GoldLight
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon
            Card(
                modifier = Modifier.size(120.dp),
                shape = RoundedCornerShape(60.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ShifaaColors.Gold.copy(alpha = 0.1f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = ShifaaColors.Gold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Coming Soon Badge
            Surface(
                color = ShifaaColors.Gold,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Coming Soon",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Description
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Features Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = ShifaaColors.TealDark.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Upcoming Features:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    features.forEach { feature ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = ShifaaColors.Emerald
                            )
                            Text(
                                text = feature,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info message
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = ShifaaColors.Gold,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "We're working hard to bring this feature to you soon!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
