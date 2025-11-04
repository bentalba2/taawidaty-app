package com.pharmatech.morocco.features.medication.domain.model

import androidx.room.Embedded
import com.google.gson.annotations.SerializedName

/**
 * Medication Data Model - Complete Moroccan Pharmaceutical Database
 * Source: allmeds.json (4,678 medications with CNSS & CNOPS reimbursement)
 * 
 * @property name Commercial medication name
 * @property dci Active ingredient (DCI - Dénomination Commune Internationale)
 * @property dosage Medication dosage (e.g., "1000 MG")
 * @property forme Pharmaceutical form (e.g., "COMPRIME", "SIROP")
 * @property presentation Packaging description
 * @property publicPrice Public sale price (PPV) in MAD
 * @property prixBr Base reimbursement reference price in MAD
 * @property type "Princeps" (brand) or "Générique" (generic)
 * @property cnss CNSS insurance reimbursement details
 * @property cnops CNOPS insurance reimbursement details
 */
data class Medication(
    val name: String,
    val dci: String,
    val dosage: String,
    val forme: String,
    val presentation: String,
    
    @SerializedName("publicPrice")
    val publicPrice: Double,
    
    @SerializedName("prix_br")
    val prixBr: Double,
    
    val type: String,
    
    @Embedded(prefix = "cnss_")
    val cnss: InsuranceReimbursement,
    
    @Embedded(prefix = "cnops_")
    val cnops: InsuranceReimbursement
) {
    /**
     * Get reimbursement info for specified insurance type
     */
    fun getReimbursementFor(insuranceType: InsuranceType): InsuranceReimbursement {
        return when (insuranceType) {
            InsuranceType.CNSS -> cnss
            InsuranceType.CNOPS -> cnops
        }
    }
    
    /**
     * Check if medication is reimbursable for specified insurance
     */
    fun isReimbursable(insuranceType: InsuranceType): Boolean {
        return getReimbursementFor(insuranceType).reimbursementRate > 0
    }
    
    /**
     * Get French description combining DCI and pharmaceutical form
     */
    fun getDescription(): String {
        return buildString {
            if (dci.isNotBlank()) {
                append(dci.lowercase().replaceFirstChar { it.uppercase() })
                if (dosage.isNotBlank()) {
                    append(" $dosage")
                }
            }
            if (forme.isNotBlank()) {
                if (isNotEmpty()) append(" - ")
                append(forme.lowercase().replaceFirstChar { it.uppercase() })
            }
        }
    }
    
    /**
     * Check if this is a generic medication
     */
    fun isGeneric(): Boolean = type.equals("Générique", ignoreCase = true)
    
    /**
     * Check if this is a brand medication (Princeps)
     */
    fun isPrinceps(): Boolean = type.equals("Princeps", ignoreCase = true)
}

/**
 * Insurance reimbursement information
 */
data class InsuranceReimbursement(
    val reimbursementRate: Int,           // Percentage (0-100)
    val reimbursementAmount: Double,      // Amount reimbursed in MAD
    val patientPays: Double               // Amount patient must pay in MAD
)

/**
 * Insurance type for reimbursement calculation
 */
enum class InsuranceType(val displayName: String, val displayNameAr: String) {
    CNSS("CNSS - Secteur Privé", "الصندوق الوطني للضمان الاجتماعي"),
    CNOPS("CNOPS - Secteur Public", "الصندوق الوطني للمنظمات الاجتماعية")
}

/**
 * Search result with match information
 */
data class MedicationSearchResult(
    val medication: Medication,
    val matchScore: Int = 0,               // Higher = better match
    val matchedField: String = ""          // Which field matched (name, dci, etc.)
)

// Old models below - kept for compatibility during migration
// TODO: Remove after full migration to new data structure

/**
 * Therapeutic Classification System
 * Based on Anatomical Therapeutic Chemical (ATC) Classification
 */
enum class TherapeuticClass(val displayName: String, val displayNameAr: String) {
    CARDIOVASCULAR("Cardiovasculaire", "القلب والأوعية الدموية"),
    ANTIBIOTICS("Antibiotiques", "المضادات الحيوية"),
    ANALGESICS("Analgésiques", "المسكنات"),
    ANTI_INFLAMMATORY("Anti-inflammatoires", "مضادات الالتهاب"),
    RESPIRATORY("Respiratoire", "الجهاز التنفسي"),
    DIGESTIVE("Digestif", "الجهاز الهضمي"),
    NERVOUS_SYSTEM("Système Nerveux", "الجهاز العصبي"),
    ENDOCRINE("Endocrinien", "الغدد الصماء"),
    DERMATOLOGY("Dermatologie", "الأمراض الجلدية"),
    OPHTHALMOLOGY("Ophtalmologie", "طب العيون"),
    VITAMINS("Vitamines et Minéraux", "الفيتامينات والمعادن"),
    OTHER("Autre", "أخرى")
}
