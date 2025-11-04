package com.pharmatech.morocco.core.network.models

// Base Response
data class BaseResponse(
    val success: Boolean,
    val message: String? = null
)

// Auth Models
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String,
    val phoneNumber: String? = null
)

data class GoogleAuthRequest(
    val idToken: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class AuthResponse(
    val success: Boolean,
    val token: String,
    val refreshToken: String,
    val user: UserProfileResponse
)

// User Models
data class UserProfileResponse(
    val id: String,
    val email: String,
    val displayName: String,
    val phoneNumber: String? = null,
    val photoUrl: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val bloodType: String? = null,
    val allergies: List<String> = emptyList(),
    val chronicConditions: List<String> = emptyList(),
    val emergencyContact: String? = null,
    val preferredLanguage: String = "fr",
    val isPremium: Boolean = false,
    val createdAt: String,
    val lastUpdated: String
)

data class UpdateProfileRequest(
    val displayName: String? = null,
    val phoneNumber: String? = null,
    val photoUrl: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val bloodType: String? = null,
    val allergies: List<String>? = null,
    val chronicConditions: List<String>? = null,
    val emergencyContact: String? = null,
    val preferredLanguage: String? = null
)

// Pharmacy Models
data class PharmacyResponse(
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
    val distance: Double? = null // in meters
)

data class PharmacyDetailResponse(
    val pharmacy: PharmacyResponse,
    val reviews: List<ReviewResponse> = emptyList(),
    val photos: List<String> = emptyList()
)

data class ReviewResponse(
    val id: String,
    val userId: String,
    val userName: String,
    val rating: Double,
    val comment: String,
    val createdAt: String
)

// Medication Models
data class MedicationResponse(
    val id: String,
    val name: String,
    val nameAr: String? = null,
    val nameFr: String? = null,
    val description: String,
    val category: String,
    val isOTC: Boolean,
    val activeIngredient: String,
    val dosageForm: String,
    val strength: String,
    val manufacturer: String,
    val imageUrl: String? = null,
    val barcode: String? = null,
    val price: Double? = null
)

data class MedicationDetailResponse(
    val medication: MedicationResponse,
    val sideEffects: List<String> = emptyList(),
    val contraindications: List<String> = emptyList(),
    val interactions: List<String> = emptyList(),
    val storageConditions: String,
    val usageInstructions: String,
    val alternativeMedications: List<MedicationResponse> = emptyList()
)

data class PaginatedResponse<T>(
    val data: List<T>,
    val page: Int,
    val totalPages: Int,
    val totalItems: Int
)

data class ImageUploadRequest(
    val imageBase64: String
)

data class MedicationIdentifyResponse(
    val success: Boolean,
    val medication: MedicationResponse? = null,
    val confidence: Double,
    val alternatives: List<MedicationResponse> = emptyList()
)

// User Medication Models
data class AddMedicationRequest(
    val medicationId: String,
    val dosage: String,
    val frequency: String,
    val timeOfDay: List<String>,
    val startDate: String,
    val endDate: String? = null,
    val instructions: String? = null,
    val reminderEnabled: Boolean = true
)

data class UserMedicationResponse(
    val id: String,
    val medicationId: String,
    val medicationName: String,
    val dosage: String,
    val frequency: String,
    val timeOfDay: List<String>,
    val startDate: String,
    val endDate: String? = null,
    val instructions: String? = null,
    val isActive: Boolean = true,
    val reminderEnabled: Boolean = true
)

// Reminder Models
data class CreateReminderRequest(
    val trackerId: String,
    val scheduledTime: String
)

data class UpdateReminderRequest(
    val scheduledTime: String? = null,
    val isTaken: Boolean? = null,
    val isSkipped: Boolean? = null
)

data class ReminderResponse(
    val id: String,
    val trackerId: String,
    val medicationName: String,
    val scheduledTime: String,
    val isTaken: Boolean = false,
    val takenAt: String? = null,
    val isSkipped: Boolean = false
)

// AI Models
data class SymptomCheckRequest(
    val symptoms: List<String>,
    val duration: String,
    val severity: String
)

data class SymptomAnalysisResponse(
    val possibleConditions: List<ConditionInfo>,
    val suggestedMedications: List<MedicationResponse>,
    val recommendations: List<String>,
    val shouldSeeDoctor: Boolean,
    val urgency: String // low, medium, high
)

data class ConditionInfo(
    val name: String,
    val description: String,
    val probability: Double
)

data class InteractionCheckRequest(
    val medicationIds: List<String>
)

data class InteractionAnalysisResponse(
    val hasInteractions: Boolean,
    val interactions: List<InteractionInfo>,
    val warnings: List<String>,
    val recommendations: List<String>
)

data class InteractionInfo(
    val medication1: String,
    val medication2: String,
    val severity: String,
    val description: String
)

data class HealthInsightResponse(
    val id: String,
    val type: String,
    val title: String,
    val titleAr: String? = null,
    val titleFr: String? = null,
    val message: String,
    val messageAr: String? = null,
    val messageFr: String? = null,
    val priority: String,
    val actionUrl: String? = null,
    val createdAt: String
)

// Analytics Models
data class MedicationTakenRequest(
    val reminderId: String,
    val takenAt: String,
    val wasOnTime: Boolean,
    val notes: String? = null,
    val sideEffects: List<String> = emptyList(),
    val effectiveness: Int? = null
)

data class AdherenceStatsResponse(
    val adherenceRate: Double, // percentage
    val totalDoses: Int,
    val takenDoses: Int,
    val missedDoses: Int,
    val onTimeDoses: Int,
    val lateDoses: Int,
    val period: String,
    val dailyStats: List<DailyAdherenceStats>
)

data class DailyAdherenceStats(
    val date: String,
    val totalDoses: Int,
    val takenDoses: Int,
    val adherenceRate: Double
)

