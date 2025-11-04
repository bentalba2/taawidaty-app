package com.pharmatech.morocco.features.medication.domain.usecase

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pharmatech.morocco.features.medication.domain.model.Medication
import com.pharmatech.morocco.features.medication.domain.model.MedicationSearchResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Loads and searches medications from allmeds.json
 * 
 * Database: 4,678 medications with complete CNSS & CNOPS reimbursement data
 */
@Singleton
class MedicationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    
    private var medications: List<Medication>? = null
    private val lock = Any()
    
    /**
     * Load all medications from JSON asset file
     * Thread-safe singleton pattern with caching
     */
    suspend fun loadMedications(): List<Medication> = withContext(Dispatchers.IO) {
        // Return cached data if available
        medications?.let { return@withContext it }
        
        synchronized(lock) {
            // Double-check after acquiring lock
            medications?.let { return@withContext it }
            
            try {
                Timber.d("Loading medications database from assets...")
                val jsonString = context.assets.open("data/allmeds.json")
                    .bufferedReader()
                    .use { it.readText() }
                
                val type = object : TypeToken<List<Medication>>() {}.type
                val loadedMedications: List<Medication> = gson.fromJson(jsonString, type)
                
                medications = loadedMedications
                Timber.i("Successfully loaded ${loadedMedications.size} medications")
                
                loadedMedications
            } catch (e: Exception) {
                Timber.e(e, "Failed to load medications database")
                emptyList()
            }
        }
    }
    
    /**
     * Search medications by name or DCI (active ingredient)
     * 
     * @param query Search query
     * @param limit Maximum number of results
     * @return List of search results sorted by relevance
     */
    suspend fun searchMedications(
        query: String,
        limit: Int = 50
    ): List<MedicationSearchResult> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        
        val meds = medications ?: loadMedications()
        val normalizedQuery = query.trim().lowercase()
        
        meds.mapNotNull { medication ->
            val nameMatch = medication.name.lowercase().contains(normalizedQuery)
            val dciMatch = medication.dci.lowercase().contains(normalizedQuery)
            
            when {
                nameMatch || dciMatch -> {
                    // Calculate match score
                    val score = when {
                        medication.name.lowercase().startsWith(normalizedQuery) -> 100
                        medication.dci.lowercase().startsWith(normalizedQuery) -> 90
                        medication.name.lowercase().contains(normalizedQuery) -> 50
                        medication.dci.lowercase().contains(normalizedQuery) -> 40
                        else -> 0
                    }
                    
                    val matchedField = when {
                        nameMatch -> "name"
                        dciMatch -> "dci"
                        else -> ""
                    }
                    
                    MedicationSearchResult(
                        medication = medication,
                        matchScore = score,
                        matchedField = matchedField
                    )
                }
                else -> null
            }
        }
            .sortedByDescending { it.matchScore }
            .take(limit)
    }
    
    /**
     * Get medication by exact name match
     */
    suspend fun getMedicationByName(name: String): Medication? = withContext(Dispatchers.IO) {
        val meds = medications ?: loadMedications()
        meds.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
    
    /**
     * Get all generic medications (type = "Générique")
     */
    suspend fun getGenericMedications(): List<Medication> = withContext(Dispatchers.IO) {
        val meds = medications ?: loadMedications()
        meds.filter { it.isGeneric() }
    }
    
    /**
     * Get all brand medications (type = "Princeps")
     */
    suspend fun getBrandMedications(): List<Medication> = withContext(Dispatchers.IO) {
        val meds = medications ?: loadMedications()
        meds.filter { it.isPrinceps() }
    }
    
    /**
     * Get database statistics
     */
    suspend fun getStatistics(): DatabaseStatistics = withContext(Dispatchers.IO) {
        val meds = medications ?: loadMedications()
        
        DatabaseStatistics(
            totalMedications = meds.size,
            genericCount = meds.count { it.isGeneric() },
            princepsCount = meds.count { it.isPrinceps() },
            cnssReimbursable = meds.count { it.isReimbursable(com.pharmatech.morocco.features.medication.domain.model.InsuranceType.CNSS) },
            cnopsReimbursable = meds.count { it.isReimbursable(com.pharmatech.morocco.features.medication.domain.model.InsuranceType.CNOPS) },
            averagePrice = meds.map { it.publicPrice }.average()
        )
    }
}

/**
 * Database statistics data class
 */
data class DatabaseStatistics(
    val totalMedications: Int,
    val genericCount: Int,
    val princepsCount: Int,
    val cnssReimbursable: Int,
    val cnopsReimbursable: Int,
    val averagePrice: Double
)
