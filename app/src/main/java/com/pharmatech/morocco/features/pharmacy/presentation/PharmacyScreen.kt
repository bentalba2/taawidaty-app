package com.pharmatech.morocco.features.pharmacy.presentation

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pharmatech.morocco.ui.theme.ShifaaColors
import com.pharmatech.morocco.ui.theme.HealthGreen

data class Pharmacy(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val phone: String,
    val isOpen: Boolean,
    val isOnCall: Boolean,
    val distance: Double,
    val rating: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacyScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }

    val mockPharmacies = remember {
        listOf(
            Pharmacy(
                id = "1",
                name = "Pharmacie du Centre",
                address = "12 Avenue Mohammed V",
                city = "Casablanca",
                phone = "+212 522 123 456",
                isOpen = true,
                isOnCall = true,
                distance = 0.5,
                rating = 4.5f
            ),
            Pharmacy(
                id = "2",
                name = "Pharmacie Al Amal",
                address = "34 Rue de la Liberté",
                city = "Casablanca",
                phone = "+212 522 234 567",
                isOpen = true,
                isOnCall = false,
                distance = 1.2,
                rating = 4.8f
            ),
            Pharmacy(
                id = "3",
                name = "Pharmacie de Nuit",
                address = "78 Boulevard Zerktouni",
                city = "Casablanca",
                phone = "+212 522 345 678",
                isOpen = true,
                isOnCall = true,
                distance = 2.1,
                rating = 4.3f
            ),
            Pharmacy(
                id = "4",
                name = "Pharmacie Salam",
                address = "56 Rue Allal Ben Abdellah",
                city = "Rabat",
                phone = "+212 537 456 789",
                isOpen = false,
                isOnCall = false,
                distance = 3.5,
                rating = 4.6f
            ),
            Pharmacy(
                id = "5",
                name = "Pharmacie Atlas",
                address = "23 Avenue Hassan II",
                city = "Marrakech",
                phone = "+212 524 567 890",
                isOpen = true,
                isOnCall = false,
                distance = 4.2,
                rating = 4.7f
            )
        )
    }

    val filteredPharmacies = mockPharmacies.filter { pharmacy ->
        val matchesSearch = pharmacy.name.contains(searchQuery, ignoreCase = true) ||
                pharmacy.address.contains(searchQuery, ignoreCase = true) ||
                pharmacy.city.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Open" -> pharmacy.isOpen
            "On Call" -> pharmacy.isOnCall
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Pharmacies", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "${filteredPharmacies.size} pharmacies found",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = if (showFilters) ShifaaColors.Gold else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { /* TODO: Map view */ }) {
                        Icon(Icons.Default.Map, contentDescription = "Map")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search pharmacies...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            if (showFilters) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "All",
                        onClick = { selectedFilter = "All" },
                        label = { Text("All") },
                        leadingIcon = if (selectedFilter == "All") {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        } else null
                    )
                    FilterChip(
                        selected = selectedFilter == "Open",
                        onClick = { selectedFilter = "Open" },
                        label = { Text("Open") },
                        leadingIcon = if (selectedFilter == "Open") {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        } else null
                    )
                    FilterChip(
                        selected = selectedFilter == "On Call",
                        onClick = { selectedFilter = "On Call" },
                        label = { Text("On Call") },
                        leadingIcon = if (selectedFilter == "On Call") {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        } else null
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filteredPharmacies.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No pharmacies found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    items(filteredPharmacies) { pharmacy ->
                        PharmacyCard(pharmacy = pharmacy)
                    }
                }
            }
        }
    }
}

@Composable
fun PharmacyCard(pharmacy: Pharmacy) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pharmacy.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = pharmacy.address,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = pharmacy.city,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (pharmacy.isOpen) HealthGreen.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.LocalPharmacy,
                        contentDescription = null,
                        tint = if (pharmacy.isOpen) HealthGreen else Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (pharmacy.isOnCall) {
                        StatusChip(
                            text = "On Call",
                            icon = Icons.Default.AccessTime,
                            color = ShifaaColors.Gold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = ShifaaColors.Gold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${pharmacy.rating}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${pharmacy.phone}")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Unable to open dialer",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call")
                }
                Button(
                    onClick = {
                        try {
                            // For now, use hardcoded coordinates from PharmacyData.kenitiraPharmacy
                            // In real implementation, these would come from pharmacy.latitude/longitude
                            val latitude = 34.24532545335408
                            val longitude = -6.5984582249030925
                            val geoUri = Uri.parse("geo:$latitude,$longitude?q=${Uri.encode(pharmacy.name)}")
                            val intent = Intent(Intent.ACTION_VIEW, geoUri)
                            intent.setPackage("com.google.android.apps.maps")
                            
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Fallback to browser if Google Maps not installed
                                val browserIntent = Intent(Intent.ACTION_VIEW, geoUri)
                                browserIntent.setPackage(null)
                                context.startActivity(browserIntent)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Unable to open maps",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Directions,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${pharmacy.distance} km")
                }
            }
        }
    }
}

@Composable
fun StatusChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = color
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

