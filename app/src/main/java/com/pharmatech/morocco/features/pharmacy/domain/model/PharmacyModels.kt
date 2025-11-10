package com.pharmatech.morocco.features.pharmacy.domain.model

import java.util.Date

data class Pharmacy(
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
    val distance: Double? = null, // in meters
    val lastUpdated: Date,
    val geocoded: Boolean = true // Whether coordinates are from real geocoding
)

data class PharmacyReview(
    val id: String,
    val userId: String,
    val userName: String,
    val pharmacyId: String,
    val rating: Double,
    val comment: String,
    val createdAt: Date
)

// Sample pharmacy data for Kenitra
object PharmacyData {
    val kenitiraPharmacy = Pharmacy(
        id = "pharmacy_kenitra_bir_rami_001",
        name = "Pharmacie Bir Rami",
        address = "Villa N°699, Rue Mehjoubi Mohamed, Lotissement Bir Rami Est",
        city = "KÉNITRA",
        latitude = 34.24532545335408,
        longitude = -6.5984582249030925,
        phoneNumber = "08 08 68 49 98",
        openingHours = "Lun-Ven: 09:00-12:30, 15:00-19:30 | Sam: 09:00-13:00",
        hasParking = false,
        is24Hours = false,
        isGuardPharmacy = false,
        rating = 4.5,
        reviewCount = 0,
        lastUpdated = Date()
    )
    
    fun getAllPharmacies(): List<Pharmacy> {
        return listOf(kenitiraPharmacy)
    }
}

