package com.pharmatech.morocco.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "pharmacies")
data class PharmacyEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val nameAr: String? = null,
    val address: String,
    val addressAr: String? = null,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val phoneNumber: String,
    val email: String? = null,
    val website: String? = null,
    val openingHours: String,
    val is24Hours: Boolean = false,
    val hasParking: Boolean = false,
    val isGuardPharmacy: Boolean = false,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val imageUrl: String? = null,
    val services: List<String> = emptyList(),
    val lastUpdated: Date
)

/**
 * Extension function to check if pharmacy is currently open
 */
fun PharmacyEntity.isCurrentlyOpen(): Boolean {
    // If 24 hours, always open
    if (is24Hours) return true

    // For now, return true (requires proper time parsing implementation)
    // In production, parse openingHours string and check against current time
    return true
}

