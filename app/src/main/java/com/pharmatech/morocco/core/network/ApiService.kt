package com.pharmatech.morocco.core.network

import com.pharmatech.morocco.core.network.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Authentication
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/google")
    suspend fun googleAuth(@Body request: GoogleAuthRequest): Response<AuthResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>

    // Pharmacy
    @GET("pharmacies")
    suspend fun getPharmacies(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("radius") radius: Int = 5000, // meters
        @Query("is24h") is24Hours: Boolean? = null
    ): Response<List<PharmacyResponse>>

    @GET("pharmacies/{id}")
    suspend fun getPharmacyDetails(@Path("id") pharmacyId: String): Response<PharmacyDetailResponse>

    @GET("pharmacies/search")
    suspend fun searchPharmacies(
        @Query("q") query: String,
        @Query("city") city: String? = null
    ): Response<List<PharmacyResponse>>

    // Medications
    @GET("medications")
    suspend fun getMedications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("category") category: String? = null,
        @Query("isOTC") isOTC: Boolean? = null
    ): Response<PaginatedResponse<MedicationResponse>>

    @GET("medications/{id}")
    suspend fun getMedicationDetails(@Path("id") medicationId: String): Response<MedicationDetailResponse>

    @GET("medications/search")
    suspend fun searchMedications(
        @Query("q") query: String,
        @Query("type") searchType: String = "name" // name, ingredient, barcode
    ): Response<List<MedicationResponse>>

    @POST("medications/identify")
    suspend fun identifyMedication(@Body image: ImageUploadRequest): Response<MedicationIdentifyResponse>

    // User Profile
    @GET("user/profile")
    suspend fun getUserProfile(): Response<UserProfileResponse>

    @PUT("user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserProfileResponse>

    @POST("user/medications")
    suspend fun addUserMedication(@Body request: AddMedicationRequest): Response<UserMedicationResponse>

    @GET("user/medications")
    suspend fun getUserMedications(): Response<List<UserMedicationResponse>>

    @DELETE("user/medications/{id}")
    suspend fun removeUserMedication(@Path("id") medicationId: String): Response<BaseResponse>

    // Reminders
    @POST("reminders")
    suspend fun createReminder(@Body request: CreateReminderRequest): Response<ReminderResponse>

    @GET("reminders")
    suspend fun getReminders(): Response<List<ReminderResponse>>

    @PUT("reminders/{id}")
    suspend fun updateReminder(
        @Path("id") reminderId: String,
        @Body request: UpdateReminderRequest
    ): Response<ReminderResponse>

    @DELETE("reminders/{id}")
    suspend fun deleteReminder(@Path("id") reminderId: String): Response<BaseResponse>

    // AI Features
    @POST("ai/symptom-checker")
    suspend fun checkSymptoms(@Body request: SymptomCheckRequest): Response<SymptomAnalysisResponse>

    @POST("ai/interaction-checker")
    suspend fun checkInteractions(@Body request: InteractionCheckRequest): Response<InteractionAnalysisResponse>

    @GET("ai/health-insights")
    suspend fun getHealthInsights(): Response<List<HealthInsightResponse>>

    // Analytics
    @POST("analytics/medication-taken")
    suspend fun logMedicationTaken(@Body request: MedicationTakenRequest): Response<BaseResponse>

    @GET("analytics/adherence")
    suspend fun getAdherenceStats(
        @Query("period") period: String = "week" // week, month, year
    ): Response<AdherenceStatsResponse>
}

