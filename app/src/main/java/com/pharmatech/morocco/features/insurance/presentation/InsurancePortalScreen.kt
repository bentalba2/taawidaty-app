package com.pharmatech.morocco.features.insurance.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pharmatech.morocco.features.medication.domain.model.InsuranceType
import com.pharmatech.morocco.features.medication.domain.model.Medication
import com.pharmatech.morocco.ui.theme.ShifaaColors
import com.pharmatech.morocco.ui.theme.HealthGreen

/**
 * Insurance Reimbursement Calculator Screen
 * 3-Step Flow: Select Insurance → Search Medication → View Results
 * Based on final-taawidaty logic
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsurancePortalScreen(
    navController: NavController,
    preSelectedMedication: String? = null,
    viewModel: InsuranceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(preSelectedMedication) {
        if (preSelectedMedication != null) {
            viewModel.setPreSelectedMedication(preSelectedMedication)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Calculateur de Remboursement",
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                navigationIcon = {
                    if (uiState.currentStep > 1) {
                        IconButton(onClick = { viewModel.previousStep() }) {
                            Icon(Icons.Default.ArrowBack, "Retour")
                        }
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Progress Indicator
            StepProgressIndicator(
                currentStep = uiState.currentStep,
                totalSteps = 3
            )
            
            // Content based on current step
            when (uiState.currentStep) {
                1 -> InsuranceSelectionStep(
                    selectedInsurance = uiState.selectedInsurance,
                    onInsuranceSelected = { viewModel.selectInsurance(it) }
                )
                2 -> MedicationSearchStep(
                    searchQuery = uiState.searchQuery,
                    searchResults = uiState.searchResults,
                    isLoading = uiState.isLoading,
                    selectedInsurance = uiState.selectedInsurance!!,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onMedicationSelected = { viewModel.selectMedication(it) }
                )
                3 -> ReimbursementResultStep(
                    medication = uiState.selectedMedication!!,
                    insuranceType = uiState.selectedInsurance!!,
                    onNewCalculation = { viewModel.reset() }
                )
            }
        }
    }
}

/**
 * Step Progress Indicator
 * Shows current step in 3-step process
 */
@Composable
fun StepProgressIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (step in 1..totalSteps) {
            StepCircle(
                stepNumber = step,
                isActive = currentStep == step,
                isCompleted = currentStep > step,
                label = when(step) {
                    1 -> "Assurance"
                    2 -> "Médicament"
                    3 -> "Résultat"
                    else -> ""
                }
            )
            
            if (step < totalSteps) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(
                            if (currentStep > step) ShifaaColors.Gold else Color.Gray.copy(alpha = 0.3f)
                        )
                )
            }
        }
    }
}

@Composable
fun StepCircle(stepNumber: Int, isActive: Boolean, isCompleted: Boolean, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    when {
                        isActive -> ShifaaColors.Gold
                        isCompleted -> ShifaaColors.Emerald
                        else -> Color.Gray.copy(alpha = 0.3f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = stepNumber.toString(),
                    color = if (isActive) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) ShifaaColors.Gold else Color.Gray
        )
    }
}

/**
 * Step 1: Insurance Selection
 */
@Composable
fun InsuranceSelectionStep(
    selectedInsurance: InsuranceType?,
    onInsuranceSelected: (InsuranceType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sélectionnez votre assurance",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Text(
            text = "Choisissez votre organisme d'assurance maladie pour calculer le remboursement",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // CNSS Card
        InsuranceOptionCard(
            title = "CNSS",
            subtitle = "Caisse Nationale de Sécurité Sociale",
            description = "Couverture pour les salariés du secteur privé",
            icon = Icons.Default.BusinessCenter,
            isSelected = selectedInsurance == InsuranceType.CNSS,
            onClick = { onInsuranceSelected(InsuranceType.CNSS) }
        )

        // CNOPS Card
        InsuranceOptionCard(
            title = "CNOPS",
            subtitle = "Caisse Nationale des Organismes de Prévoyance Sociale",
            description = "Couverture pour les fonctionnaires et agents de l'État",
            icon = Icons.Default.AccountBalance,
            isSelected = selectedInsurance == InsuranceType.CNOPS,
            onClick = { onInsuranceSelected(InsuranceType.CNOPS) }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Continue button (auto-advances when insurance selected)
        AnimatedVisibility(
            visible = selectedInsurance != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ShifaaColors.Emerald.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ShifaaColors.Emerald)
                    Text(
                        "Assurance sélectionnée - Passez à la recherche →",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ShifaaColors.Emerald,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun InsuranceOptionCard(
    title: String,
    subtitle: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ShifaaColors.Gold.copy(alpha = 0.2f) 
                          else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) 
            androidx.compose.foundation.BorderStroke(2.dp, ShifaaColors.Gold) 
        else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ShifaaColors.TealDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = ShifaaColors.Gold
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = ShifaaColors.Gold,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

/**
 * Step 2: Medication Search
 */
@Composable
fun MedicationSearchStep(
    searchQuery: String,
    searchResults: List<Medication>,
    isLoading: Boolean,
    selectedInsurance: InsuranceType,
    onSearchQueryChange: (String) -> Unit,
    onMedicationSelected: (Medication) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Rechercher un médicament",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Search TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            placeholder = { Text("Nom du médicament...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Effacer")
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ShifaaColors.Gold,
                focusedLeadingIconColor = ShifaaColors.Gold
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Results
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ShifaaColors.Gold)
                }
            }
            searchQuery.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Recherchez un médicament",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = "Tapez le nom pour commencer",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
            searchResults.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Aucun résultat",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = "Essayez une autre recherche",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults) { medication ->
                        MedicationSearchResultCard(
                            medication = medication,
                            selectedInsurance = selectedInsurance,
                            onClick = { onMedicationSelected(medication) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MedicationSearchResultCard(
    medication: Medication,
    selectedInsurance: InsuranceType,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medication.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = medication.getDescription(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                
                Surface(
                    color = if (medication.isGeneric()) 
                        HealthGreen.copy(alpha = 0.2f) 
                    else 
                        ShifaaColors.TealDark.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = medication.type,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (medication.isGeneric()) HealthGreen else ShifaaColors.TealDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${medication.publicPrice} DH",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ShifaaColors.Gold
                )
                
                if (medication.isReimbursable(selectedInsurance)) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Remboursable",
                        tint = HealthGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * Step 3: Reimbursement Result
 */
@Composable
fun ReimbursementResultStep(
    medication: Medication,
    insuranceType: InsuranceType,
    onNewCalculation: () -> Unit
) {
    val reimbursement = medication.getReimbursementFor(insuranceType)
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Résultat du calcul",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Medication Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = ShifaaColors.TealDark
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = medication.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ShifaaColors.GoldLight
                    )
                    Text(
                        text = medication.getDescription(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = ShifaaColors.IvoryWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Assurance: ${insuranceType.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ShifaaColors.Gold
                    )
                }
            }
        }

        // Reimbursement Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Détails du remboursement",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Divider(color = Color.Gray.copy(alpha = 0.3f))
                    
                    // Original Price
                    ResultRow(
                        label = "Prix public",
                        value = "${medication.publicPrice} DH",
                        isHighlight = false
                    )
                    
                    // Reimbursement Rate
                    ResultRow(
                        label = "Taux de remboursement",
                        value = "${reimbursement?.reimbursementRate ?: 0}%",
                        isHighlight = false
                    )
                    
                    // Reimbursement Amount
                    ResultRow(
                        label = "Montant remboursé",
                        value = "${reimbursement?.reimbursementAmount ?: 0} DH",
                        valueColor = HealthGreen,
                        isHighlight = false
                    )
                    
                    Divider(
                        color = ShifaaColors.Gold,
                        thickness = 2.dp
                    )
                    
                    // Patient Pays (Highlighted)
                    ResultRow(
                        label = "À votre charge",
                        value = "${reimbursement?.patientPays ?: medication.publicPrice} DH",
                        valueColor = ShifaaColors.Gold,
                        isHighlight = true
                    )
                }
            }
        }

        // Savings Info
        if (reimbursement != null && reimbursement.reimbursementRate > 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = HealthGreen.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Savings,
                            contentDescription = null,
                            tint = HealthGreen,
                            modifier = Modifier.size(40.dp)
                        )
                        Column {
                            Text(
                                text = "Vous économisez",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = "${reimbursement.reimbursementAmount} DH (${reimbursement.reimbursementRate}%)",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = HealthGreen
                            )
                        }
                    }
                }
            }
        }

        // New Calculation Button
        item {
            Button(
                onClick = onNewCalculation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ShifaaColors.TealDark,
                    contentColor = ShifaaColors.GoldLight
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nouveau calcul",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ResultRow(
    label: String,
    value: String,
    valueColor: Color = Color.Black,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isHighlight) 
                MaterialTheme.typography.titleLarge 
            else 
                MaterialTheme.typography.bodyLarge,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            style = if (isHighlight) 
                MaterialTheme.typography.headlineSmall 
            else 
                MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
