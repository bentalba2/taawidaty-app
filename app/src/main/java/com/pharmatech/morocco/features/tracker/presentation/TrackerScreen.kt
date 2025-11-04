package com.pharmatech.morocco.features.tracker.presentation

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pharmatech.morocco.features.tracker.domain.model.MedicationSchedule
import com.pharmatech.morocco.ui.theme.HealthGreen
import com.pharmatech.morocco.ui.theme.ShifaaColors
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen(
    navController: NavController,
    viewModel: TrackerViewModel = hiltViewModel()
) {
    val scheduleList by viewModel.scheduleList.collectAsState()
    val takenCount = remember(scheduleList) { viewModel.getTakenCount(scheduleList) }
    val totalCount = remember(scheduleList) { viewModel.getTotalCount(scheduleList) }
    val adherenceRate = remember(scheduleList) { viewModel.getAdherenceRate(scheduleList) }
    val progressFraction = remember(scheduleList) {
        if (totalCount > 0) takenCount.coerceAtMost(totalCount).toFloat() / totalCount else 0f
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var pendingMedicationName by remember { mutableStateOf("") }
    var pendingMedicationDosage by remember { mutableStateOf(TRACKER_DEFAULT_DOSAGE) }
    var pendingMedicationId by remember { mutableStateOf<String?>(null) }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getStateFlow<String?>(TRACKER_ADD_NAME_KEY, null)?.collectLatest { name ->
            if (!name.isNullOrBlank()) {
                val dosage = savedStateHandle.get<String>(TRACKER_ADD_DOSAGE_KEY)
                val medicationId = savedStateHandle.get<String>(TRACKER_ADD_ID_KEY)
                pendingMedicationName = name
                pendingMedicationDosage = dosage?.takeIf { it.isNotBlank() } ?: TRACKER_DEFAULT_DOSAGE
                pendingMedicationId = medicationId
                showAddDialog = true

                savedStateHandle[TRACKER_ADD_NAME_KEY] = null
                savedStateHandle[TRACKER_ADD_DOSAGE_KEY] = null
                savedStateHandle[TRACKER_ADD_ID_KEY] = null
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Medication Tracker", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "$takenCount of $totalCount doses taken today",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    // Navigate to medication screen to add a medication
                    navController.navigate(com.pharmatech.morocco.ui.navigation.Screen.Medication.route)
                },
                containerColor = ShifaaColors.Gold
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Medication")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 120.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ShifaaColors.PharmacyGreen
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Today's Progress",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ShifaaColors.GoldLight
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$adherenceRate%",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = ShifaaColors.GoldLight,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Adherence Rate",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ShifaaColors.IvoryWhite
                                )
                            }
                            HorizontalDivider(
                                modifier = Modifier
                                    .height(48.dp)
                                    .width(1.dp),
                                color = ShifaaColors.GoldLight.copy(alpha = 0.3f)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$takenCount/$totalCount",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ShifaaColors.IvoryWhite
                                )
                                Text(
                                    text = "Doses Taken",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ShifaaColors.IvoryWhite
                                )
                            }
                        }
                    }
                }
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
                                Text(
                                    text = "Weekly Streak",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Keep up the great work!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Text(
                                text = "5 days",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = ShifaaColors.Gold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { progressFraction.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = ShifaaColors.Gold,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Today's Schedule",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(scheduleList) { schedule ->
                MedicationScheduleCard(
                    schedule = schedule,
                    onTaken = {
                        viewModel.toggleMedicationTaken(schedule.id)
                    },
                    onRemove = {
                        viewModel.removeMedication(schedule.id)
                        scope.launch {
                            snackbarHostState.showSnackbar("${schedule.medicationName} retiré du suivi")
                        }
                    }
                )
            }
        }
    }

    if (showAddDialog && pendingMedicationName.isNotBlank()) {
        AddMedicationDialog(
            medicationName = pendingMedicationName,
            initialDosage = pendingMedicationDosage,
            onDismiss = {
                showAddDialog = false
                pendingMedicationName = ""
                pendingMedicationDosage = TRACKER_DEFAULT_DOSAGE
                pendingMedicationId = null
            },
            onConfirm = { dosage, times, notes ->
                viewModel.addMedication(
                    medicationName = pendingMedicationName,
                    dosage = dosage,
                    times = times,
                    medicationId = pendingMedicationId,
                    notes = notes
                )
                showAddDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("$pendingMedicationName ajouté au suivi")
                }
                pendingMedicationName = ""
                pendingMedicationDosage = TRACKER_DEFAULT_DOSAGE
                pendingMedicationId = null
            }
        )
    }
}

@Composable
fun MedicationScheduleCard(
    schedule: MedicationSchedule,
    onTaken: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (schedule.isTaken)
                HealthGreen.copy(alpha = 0.05f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (schedule.isTaken) HealthGreen.copy(alpha = 0.1f) else ShifaaColors.Gold.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (schedule.isTaken) Icons.Default.CheckCircle else Icons.Default.Schedule,
                        contentDescription = null,
                        tint = if (schedule.isTaken) HealthGreen else ShifaaColors.Gold,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column {
                    Text(
                        text = schedule.medicationName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = schedule.dosage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = schedule.time,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove medication",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                if (schedule.isTaken) {
                    Surface(
                        color = HealthGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { onTaken() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = HealthGreen
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Taken",
                                style = MaterialTheme.typography.labelMedium,
                                color = HealthGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onTaken,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ShifaaColors.Gold
                        )
                    ) {
                        Text("Take")
                    }
                }
            }
        }
    }
}

@Composable
private fun AddMedicationDialog(
    medicationName: String,
    initialDosage: String,
    onDismiss: () -> Unit,
    onConfirm: (dosage: String, times: List<String>, notes: String?) -> Unit
) {
    var dosage by rememberSaveable(medicationName) { mutableStateOf(initialDosage) }
    var notes by rememberSaveable(medicationName) { mutableStateOf("") }
    var frequency by rememberSaveable(medicationName) { mutableStateOf(1) }

    val timePattern = remember { Regex("^([01]?\\d|2[0-3]):[0-5]\\d$") }
    val timeInputs = remember(medicationName) { mutableStateListOf(TRACKER_DEFAULT_TIME) }
    val timeErrors = remember(medicationName) { mutableStateListOf(false) }
    val frequencyOptions = remember { listOf(1, 2, 3, 4) }

    LaunchedEffect(frequency) {
        while (timeInputs.size < frequency) {
            timeInputs.add(TRACKER_DEFAULT_TIME)
            timeErrors.add(false)
        }
        while (timeInputs.size > frequency) {
            timeInputs.removeLastOrNull()
            timeErrors.removeLastOrNull()
        }
        if (timeInputs.isEmpty()) {
            timeInputs.add(TRACKER_DEFAULT_TIME)
            timeErrors.add(false)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val sanitizedDosage = dosage.trim().ifBlank { TRACKER_DEFAULT_DOSAGE }
                val sanitizedTimes = timeInputs.mapIndexed { index, value ->
                    val trimmed = value.trim()
                    val isValid = timePattern.matches(trimmed)
                    timeErrors[index] = !isValid
                    trimmed
                }

                if (timeErrors.any { it }) {
                    return@Button
                }

                onConfirm(sanitizedDosage, sanitizedTimes, notes.trim().ifBlank { null })
            }) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
        title = {
            Text("Ajouter au suivi")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = medicationName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage") },
                    singleLine = true
                )

                Text(
                    text = "Nombre de prises par jour",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    frequencyOptions.forEach { option ->
                        FilterChip(
                            selected = frequency == option,
                            onClick = { frequency = option },
                            label = { Text("$option×/jour") }
                        )
                    }
                }

                timeInputs.forEachIndexed { index, value ->
                    OutlinedTextField(
                        value = value,
                        onValueChange = {
                            timeInputs[index] = it
                            if (timeErrors.getOrNull(index) == true) {
                                timeErrors[index] = false
                            }
                        },
                        label = { Text("Heure #${index + 1} (HH:MM)") },
                        isError = timeErrors.getOrElse(index) { false },
                        supportingText = {
                            if (timeErrors.getOrElse(index) { false }) {
                                Text("Format attendu : 08:30")
                            }
                        },
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optionnel)") },
                    minLines = 2
                )
            }
        }
    )
}