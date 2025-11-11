package com.pharmatech.morocco.features.pharmacy.presentation

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.pharmatech.morocco.features.pharmacy.domain.model.KenitraPharmacyData
import com.pharmatech.morocco.features.pharmacy.domain.model.RabatPharmacyData
import android.util.Log

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
    var selectedCity by remember { mutableStateOf("All Cities") }

    // Load real pharmacy data from both Kénitra and Rabat
    val mockPharmacies = remember {
        Log.d("PharmacyScreen", "Loading pharmacy data...")
        
        // Load Kénitra pharmacies
        val kenitraData = KenitraPharmacyData.getAllPharmacies()
        Log.d("PharmacyScreen", "Loaded ${kenitraData.size} pharmacies from Kénitra")
        
        // Load Rabat pharmacies
        val rabatData = RabatPharmacyData.getAllPharmacies()
        Log.d("PharmacyScreen", "Loaded ${rabatData.size} pharmacies from Rabat")
        
        // Combine both datasets
        val allPharmacies = (kenitraData + rabatData)
        Log.d("PharmacyScreen", "Total combined pharmacies: ${allPharmacies.size}")
        
        // Convert to PharmacyScreen's Pharmacy format
        allPharmacies.filter { it.geocoded }.map { pharmacy ->
            Pharmacy(
                id = pharmacy.id,
                name = pharmacy.name,
                address = pharmacy.address,
                city = pharmacy.city,
                phone = pharmacy.phoneNumber,
                isOpen = true,  // Assume open (we don't have real-time data)
                isOnCall = pharmacy.isGuardPharmacy,
                distance = pharmacy.distance ?: 0.0,
                rating = pharmacy.rating.toFloat()
            )
        }
    }
    
    Log.d("PharmacyScreen", "Displaying ${mockPharmacies.size} pharmacies")
    
    // Get unique cities for filter
    val cities = remember {
        listOf("All Cities") + mockPharmacies.map { it.city }.distinct().sorted()
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
        val matchesCity = selectedCity == "All Cities" || pharmacy.city == selectedCity
        matchesSearch && matchesFilter && matchesCity
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
                    IconButton(onClick = { 
                        navController.navigate("pharmacy_map")
                    }) {
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Status filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                    
                    // City filter - scrollable row
                    Text(
                        "Filter by City:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cities.size) { index ->
                            val city = cities[index]
                            FilterChip(
                                selected = selectedCity == city,
                                onClick = { selectedCity = city },
                                label = { Text(city) },
                                leadingIcon = if (selectedCity == city) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                } else null
                            )
                        }
                    }
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

