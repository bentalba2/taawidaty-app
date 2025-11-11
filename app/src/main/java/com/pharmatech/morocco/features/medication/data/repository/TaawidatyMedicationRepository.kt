package com.pharmatech.morocco.features.medication.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for TAAWIDATY medication database
 * Contains 5,709+ medications from CNOPS database with full pricing and reimbursement data
 */
@Singleton
class TaawidatyMedicationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private var medicationsCache: List<TaawidatyMedication>? = null
    
    /**
     * Load medications from JSON asset
     */
    suspend fun loadMedications(): Result<List<TaawidatyMedication>> = withContext(Dispatchers.IO) {
        try {
            if (medicationsCache != null) {
                return@withContext Result.success(medicationsCache!!)
            }
            
            Timber.d("Loading TAAWIDATY medication database...")
            val inputStream = context.assets.open("data/medications.json")
            val reader = InputStreamReader(inputStream)
            
            val type = object : TypeToken<List<TaawidatyMedication>>() {}.type
            val medications: List<TaawidatyMedication> = gson.fromJson(reader, type)
            
            reader.close()
            inputStream.close()
            
            medicationsCache = medications
            Timber.d("Loaded ${medications.size} medications from TAAWIDATY database")
            
            Result.success(medications)
        } catch (e: Exception) {
            Timber.e(e, "Failed to load medications")
            Result.failure(e)
        }
    }
    
    /**
     * Search medications by name or DCI
     */
    suspend fun searchMedications(query: String): Result<List<TaawidatyMedication>> {
        return try {
            val allMedications = loadMedications().getOrThrow()
            val lowerQuery = query.lowercase()
            
            val filtered = allMedications.filter { med ->
                med.name.lowercase().contains(lowerQuery) ||
                med.dci.lowercase().contains(lowerQuery)
            }
            
            Timber.d("Search '$query' returned ${filtered.size} results")
            Result.success(filtered)
        } catch (e: Exception) {
            Timber.e(e, "Search failed")
            Result.failure(e)
        }
    }
    
    /**
     * Filter medications
     */
    suspend fun filterMedications(filters: TaawidatyFilters): Result<List<TaawidatyMedication>> {
        return try {
            val allMedications = loadMedications().getOrThrow()
            
            val filtered = allMedications.filter { med ->
                // Filter by insurance type
                val insuranceMatch = filters.insurance?.let {
                    med.insurance.equals(it, ignoreCase = true)
                } ?: true
                
                // Filter by medication type
                val typeMatch = filters.type?.let {
                    med.type?.contains(it, ignoreCase = true) == true
                } ?: true
                
                // Filter by minimum reimbursement rate
                val reimbMatch = filters.minReimbursementRate?.let {
                    med.tauxRemb >= it
                } ?: true
                
                // Filter by max price
                val priceMatch = filters.maxPrice?.let {
                    med.ppv <= it
                } ?: true
                
                insuranceMatch && typeMatch && reimbMatch && priceMatch
            }
            
            Timber.d("Filters returned ${filtered.size} results")
            Result.success(filtered)
        } catch (e: Exception) {
            Timber.e(e, "Filter failed")
            Result.failure(e)
        }
    }
    
    /**
     * Get medication by ID
     */
    suspend fun getMedicationById(id: Int): Result<TaawidatyMedication?> {
        return try {
            val allMedications = loadMedications().getOrThrow()
            val medication = allMedications.find { it.id == id }
            Result.success(medication)
        } catch (e: Exception) {
            Timber.e(e, "Failed to get medication by ID")
            Result.failure(e)
        }
    }
    
    /**
     * Get medications by therapeutic class
     */
    suspend fun getMedicationsByClass(className: String): Result<List<TaawidatyMedication>> {
        return try {
            val allMedications = loadMedications().getOrThrow()
            val filtered = allMedications.filter { med ->
                med.classe_therapeutique?.contains(className, ignoreCase = true) == true
            }
            Result.success(filtered)
        } catch (e: Exception) {
            Timber.e(e, "Failed to get medications by class")
            Result.failure(e)
        }
    }
    
    /**
     * Get database statistics
     */
    suspend fun getStatistics(): Result<TaawidatyStats> {
        return try {
            val allMedications = loadMedications().getOrThrow()
            
            val stats = TaawidatyStats(
                totalCount = allMedications.size,
                cnopsCount = allMedications.count { it.insurance == "CNOPS" },
                cnssCount = allMedications.count { it.insurance == "CNSS" },
                genericCount = allMedications.count { it.type?.contains("Generic", true) == true },
                princepsCount = allMedications.count { it.type?.contains("Princeps", true) == true },
                averagePrice = allMedications.map { it.ppv }.average(),
                averageReimbursementRate = allMedications.map { it.tauxRemb }.average(),
                therapeuticClasses = allMedications
                    .mapNotNull { it.classe_therapeutique }
                    .distinct()
                    .sorted()
            )
            
            Result.success(stats)
        } catch (e: Exception) {
            Timber.e(e, "Failed to get statistics")
            Result.failure(e)
        }
    }
}

/**
 * TAAWIDATY Medication data model (matching JSON structure)
 */
data class TaawidatyMedication(
    val id: Int,
    val name: String,
    val dci: String,
    val dosage: String? = null,
    val forme: String? = null,
    val presentation: String? = null,
    val ppv: Double,
    val prix_br: Double? = null,
    val ph: Double? = null,
    val prix_br_ph: Double? = null,
    val taux_remb: Int,
    val reimbursement_amount: Double,
    val patient_pays: Double,
    val classe_therapeutique: String? = null,
    val type: String? = null,
    val insurance: String,
    val barcode: String? = null
) {
    val tauxRemb: Int get() = taux_remb
    
    fun calculateReimbursement(customPrice: Double? = null): ReimbursementResult {
        val price = customPrice ?: ppv
        val reimbursed = price * (taux_remb / 100.0)
        val patientCost = price - reimbursed
        
        return ReimbursementResult(
            publicPrice = price,
            reimbursementRate = taux_remb,
            reimbursedAmount = reimbursed,
            patientPays = patientCost
        )
    }
    
    fun getDisplayName(): String {
        return buildString {
            append(name)
            dosage?.let { append(" $it") }
            forme?.let { append(" - $it") }
        }
    }
    
    fun getInsuranceDisplayName(): String {
        return when (insurance.uppercase()) {
            "CNOPS" -> "CNOPS (Fonctionnaires)"
            "CNSS" -> "CNSS (Secteur Privé)"
            else -> insurance
        }
    }
}

data class ReimbursementResult(
    val publicPrice: Double,
    val reimbursementRate: Int,
    val reimbursedAmount: Double,
    val patientPays: Double
)

data class TaawidatyFilters(
    val insurance: String? = null,
    val type: String? = null,
    val minReimbursementRate: Int? = null,
    val maxPrice: Double? = null
)

data class TaawidatyStats(
    val totalCount: Int,
    val cnopsCount: Int,
    val cnssCount: Int,
    val genericCount: Int,
    val princepsCount: Int,
    val averagePrice: Double,
    val averageReimbursementRate: Double,
    val therapeuticClasses: List<String>
)
