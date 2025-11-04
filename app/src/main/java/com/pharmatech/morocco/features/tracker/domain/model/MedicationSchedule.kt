package com.pharmatech.morocco.features.tracker.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing a medication entry in the daily tracker.
 */
@Serializable
data class MedicationSchedule(
    val id: String,
    val medicationId: String? = null,
    val medicationName: String,
    val dosage: String,
    val time: String,
    val isTaken: Boolean = false,
    val notes: String? = null,
    val frequencyPerDay: Int = 1
)
