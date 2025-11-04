package com.pharmatech.morocco.features.medication.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pharmatech.morocco.features.tracker.presentation.TRACKER_ADD_DOSAGE_KEY
import com.pharmatech.morocco.features.tracker.presentation.TRACKER_ADD_ID_KEY
import com.pharmatech.morocco.features.tracker.presentation.TRACKER_ADD_NAME_KEY
import com.pharmatech.morocco.features.tracker.presentation.TRACKER_DEFAULT_DOSAGE
import com.pharmatech.morocco.features.medication.domain.model.Medication
import com.pharmatech.morocco.features.medication.domain.model.InsuranceType
import com.pharmatech.morocco.ui.theme.ShifaaColors
import com.pharmatech.morocco.ui.theme.HealthGreen
import com.pharmatech.morocco.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationScreen(
    navController: NavController,
    viewModel: MedicationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var detailMedication by remember { mutableStateOf<Medication?>(null) }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            viewModel.searchMedications(searchQuery)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Médicaments", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "4,678 médicaments disponibles",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
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
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Rechercher un médicament...") },
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

            // Results
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    ErrorState(error = uiState.error!!)
                }
                searchQuery.isBlank() -> {
                    EmptySearchState()
                }
                uiState.searchResults.isEmpty() -> {
                    NoResultsState()
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.searchResults) { result ->
                            MedicationCard(
                                medication = result.medication,
                                onShowDetails = { selected ->
                                    detailMedication = selected
                                },
                                onAddToTracker = { selected ->
                                    val dosageDisplay = selected.getDescription().takeIf { it.isNotBlank() }
                                        ?: selected.dosage.takeIf { it.isNotBlank() }
                                        ?: TRACKER_DEFAULT_DOSAGE

                                    val previousHandle = navController.previousBackStackEntry?.savedStateHandle
                                    if (previousHandle != null) {
                                        previousHandle[TRACKER_ADD_NAME_KEY] = selected.name
                                        previousHandle[TRACKER_ADD_DOSAGE_KEY] = dosageDisplay
                                        previousHandle[TRACKER_ADD_ID_KEY] = selected.name
                                        navController.navigateUp()
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Ouvrez le suivi pour ajouter ce médicament")
                                        }
                                    }
                                },
                                onCalculate = { medication ->
                                    navController.navigate(
                                        Screen.Insurance.createRoute(medication.name)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    detailMedication?.let { medication ->
        MedicationDetailsDialog(
            medication = medication,
            onDismiss = { detailMedication = null },
            onAddToTracker = {
                val dosageDisplay = medication.getDescription().takeIf { it.isNotBlank() }
                    ?: medication.dosage.takeIf { it.isNotBlank() }
                    ?: TRACKER_DEFAULT_DOSAGE
                val previousHandle = navController.previousBackStackEntry?.savedStateHandle
                if (previousHandle != null) {
                    previousHandle[TRACKER_ADD_NAME_KEY] = medication.name
                    previousHandle[TRACKER_ADD_DOSAGE_KEY] = dosageDisplay
                    previousHandle[TRACKER_ADD_ID_KEY] = medication.name
                    detailMedication = null
                    navController.navigateUp()
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Ouvrez le suivi pour ajouter ce médicament")
                    }
                }
            }
        )
    }
}

@Composable
fun MedicationCard(
    medication: Medication,
    onShowDetails: (Medication) -> Unit,
    onAddToTracker: (Medication) -> Unit,
    onCalculate: (Medication) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medication.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // French description from DCI
                    Text(
                        text = medication.getDescription(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = ShifaaColors.PharmacyGreen,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Type badge
                Surface(
                    color = if (medication.isGeneric()) 
                        HealthGreen.copy(alpha = 0.1f)
                    else 
                        ShifaaColors.Gold.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = medication.type,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (medication.isGeneric()) HealthGreen else ShifaaColors.Gold,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Price and Reimbursement Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Prix en pharmacie",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "%.2f MAD".format(medication.publicPrice),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = ShifaaColors.Gold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    // CNSS Reimbursement
                    if (medication.cnss.reimbursementRate > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CNSS:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${medication.cnss.reimbursementRate}%",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = ShifaaColors.PharmacyGreen
                            )
                        }
                    }
                    // CNOPS Reimbursement
                    if (medication.cnops.reimbursementRate > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CNOPS:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${medication.cnops.reimbursementRate}%",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = ShifaaColors.PharmacyGreen
                            )
                        }
                    }
                    if (medication.cnss.reimbursementRate == 0 && medication.cnops.reimbursementRate == 0) {
                        Text(
                            text = "Non remboursable",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onShowDetails(medication) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Détails")
                    }
                    OutlinedButton(
                        onClick = { onAddToTracker(medication) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ajouter au suivi")
                    }
                }
                Button(
                    onClick = { onCalculate(medication) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShifaaColors.PharmacyGreen
                    )
                ) {
                    Icon(
                        Icons.Default.Calculate,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Calculer le remboursement")
                }
            }
        }
    }
}

@Composable
private fun MedicationDetailsDialog(
    medication: Medication,
    onDismiss: () -> Unit,
    onAddToTracker: () -> Unit
) {
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onAddToTracker) {
                Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ajouter au suivi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        },
        title = {
            Text(
                text = medication.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 360.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow(
                    label = "DCI",
                    value = medication.dci.ifBlank { "Non renseigné" }
                )
                DetailRow(
                    label = "Dosage",
                    value = medication.dosage.ifBlank { "Non renseigné" }
                )
                DetailRow(
                    label = "Forme",
                    value = medication.forme.ifBlank { "Non renseigné" }
                )
                DetailRow(
                    label = "Présentation",
                    value = medication.presentation.ifBlank { "Non renseigné" }
                )

                HorizontalDivider()

                DetailRow(
                    label = "Prix public",
                    value = "%.2f MAD".format(medication.publicPrice),
                    valueColor = ShifaaColors.Gold
                )

                val cnss = medication.getReimbursementFor(InsuranceType.CNSS)
                val cnops = medication.getReimbursementFor(InsuranceType.CNOPS)

                DetailRow(
                    label = "CNSS",
                    value = if (cnss.reimbursementRate > 0) {
                        "${cnss.reimbursementRate}% remboursé (${String.format("%.2f", cnss.reimbursementAmount)} MAD)"
                    } else {
                        "Non remboursable"
                    },
                    valueColor = if (cnss.reimbursementRate > 0) HealthGreen else MaterialTheme.colorScheme.error
                )

                DetailRow(
                    label = "CNOPS",
                    value = if (cnops.reimbursementRate > 0) {
                        "${cnops.reimbursementRate}% remboursé (${String.format("%.2f", cnops.reimbursementAmount)} MAD)"
                    } else {
                        "Non remboursable"
                    },
                    valueColor = if (cnops.reimbursementRate > 0) HealthGreen else MaterialTheme.colorScheme.error
                )

                if (medication.getDescription().isNotBlank()) {
                    HorizontalDivider()
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = medication.getDescription(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    )
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EmptySearchState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Recherchez un médicament",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Tapez le nom d'un médicament ou son principe actif",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Composable
fun NoResultsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Aucun médicament trouvé",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Essayez une autre recherche",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Composable
fun ErrorState(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Erreur",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}
