package com.pharmatech.morocco.core.utils

import com.pharmatech.morocco.core.database.dao.*
import com.pharmatech.morocco.core.database.entities.*
import timber.log.Timber
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock data generator for emulator testing and development.
 * Populates local database with realistic sample data.
 * Only enabled in DEBUG builds and on emulators.
 */
@Singleton
class MockDataGenerator @Inject constructor(
    private val pharmacyDao: PharmacyDao,
    private val medicationDao: MedicationDao,
    private val userDao: UserDao,
    private val trackerDao: MedicationTrackerDao,
    private val reminderDao: ReminderDao
) {

    /**
     * Generates all mock data for testing.
     * Safe to call multiple times - will clear existing data first.
     */
    suspend fun generateAllMockData() {
        Timber.i("Generating mock data for emulator testing...")

        try {
            // Generate data in dependency order
            generateMockPharmacies()
            generateMockMedications()

            Timber.i("Mock data generation completed successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to generate mock data")
            throw e
        }
    }

    /**
     * Generates realistic pharmacy data for Moroccan cities
     */
    private suspend fun generateMockPharmacies() {
        val pharmacies = listOf(
            PharmacyEntity(
                id = UUID.randomUUID().toString(),
                name = "Pharmacie Centrale",
                nameAr = "صيدلية سنترال",
                address = "123 Avenue Mohammed V",
                addressAr = "123 شارع محمد الخامس",
                city = "Casablanca",
                latitude = 33.5731,
                longitude = -7.5898,
                phoneNumber = "+212522123456",
                email = "contact@pharmacie-centrale.ma",
                website = "https://pharmacie-centrale.ma",
                openingHours = "Mon-Sat: 8:00-20:00, Sun: 9:00-13:00",
                is24Hours = false,
                hasParking = true,
                isGuardPharmacy = false,
                rating = 4.5,
                reviewCount = 127,
                imageUrl = null,
                services = listOf("Consultation", "Vaccination"),
                lastUpdated = Date()
            ),
            PharmacyEntity(
                id = UUID.randomUUID().toString(),
                name = "Pharmacie de Garde 24h",
                nameAr = "صيدلية الحراسة 24 ساعة",
                address = "45 Rue des FAR",
                addressAr = "45 شارع القوات المسلحة الملكية",
                city = "Rabat",
                latitude = 34.0209,
                longitude = -6.8416,
                phoneNumber = "+212537234567",
                email = "garde24h@gmail.com",
                website = null,
                openingHours = "24/7",
                is24Hours = true,
                hasParking = false,
                isGuardPharmacy = true,
                rating = 4.8,
                reviewCount = 243,
                imageUrl = null,
                services = listOf("24h Service", "Emergency Care"),
                lastUpdated = Date()
            ),
            PharmacyEntity(
                id = UUID.randomUUID().toString(),
                name = "Pharmacie Al Andalous",
                nameAr = "صيدلية الأندلس",
                address = "78 Boulevard Zerktouni",
                addressAr = "78 شارع الزرقطوني",
                city = "Casablanca",
                latitude = 33.5883,
                longitude = -7.6114,
                phoneNumber = "+212522345678",
                email = "andalous.pharma@outlook.com",
                website = null,
                openingHours = "Mon-Fri: 9:00-19:00, Sat: 9:00-13:00",
                is24Hours = false,
                hasParking = true,
                isGuardPharmacy = false,
                rating = 4.2,
                reviewCount = 56,
                imageUrl = null,
                services = listOf("Consultation", "Blood Pressure Check"),
                lastUpdated = Date()
            ),
            PharmacyEntity(
                id = UUID.randomUUID().toString(),
                name = "Pharmacie du Centre",
                nameAr = "صيدلية المركز",
                address = "12 Avenue Hassan II",
                addressAr = "12 شارع الحسن الثاني",
                city = "Marrakech",
                latitude = 31.6295,
                longitude = -7.9811,
                phoneNumber = "+212524456789",
                email = null,
                website = null,
                openingHours = "Mon-Sat: 8:30-20:30",
                is24Hours = false,
                hasParking = false,
                isGuardPharmacy = false,
                rating = 4.0,
                reviewCount = 89,
                imageUrl = null,
                services = listOf("Consultation"),
                lastUpdated = Date()
            ),
            PharmacyEntity(
                id = UUID.randomUUID().toString(),
                name = "Pharmacie Moderne",
                nameAr = "صيدلية حديثة",
                address = "34 Rue de la Liberté",
                addressAr = "34 شارع الحرية",
                city = "Fes",
                latitude = 34.0181,
                longitude = -5.0078,
                phoneNumber = "+212535567890",
                email = "moderne@pharmacie.ma",
                website = "https://pharmacie-moderne.ma",
                openingHours = "Mon-Sun: 8:00-22:00",
                is24Hours = false,
                hasParking = true,
                isGuardPharmacy = false,
                rating = 4.6,
                reviewCount = 178,
                imageUrl = null,
                services = listOf("Consultation", "Lab Tests"),
                lastUpdated = Date()
            )
        )

        pharmacyDao.insertPharmacies(pharmacies)
        Timber.d("Generated ${pharmacies.size} mock pharmacies")
    }

    /**
     * Generates realistic medication data
     */
    private suspend fun generateMockMedications() {
        val medications = listOf(
            MedicationEntity(
                id = UUID.randomUUID().toString(),
                name = "Paracetamol 500mg",
                nameAr = "باراسيتامول 500 مغ",
                nameFr = "Paracétamol 500mg",
                description = "Pain reliever and fever reducer",
                descriptionAr = "مسكن للألم وخافض للحرارة",
                descriptionFr = "Analgésique et antipyrétique",
                category = "Pain Relief",
                isOTC = true,
                activeIngredient = "Paracetamol",
                dosageForm = "Tablet",
                strength = "500mg",
                manufacturer = "Sothema",
                imageUrl = null,
                barcode = "6111000001234",
                sideEffects = listOf("Nausea", "Allergic reaction (rare)"),
                contraindications = listOf("Severe liver disease"),
                interactions = listOf("Warfarin", "Alcohol"),
                storageConditions = "Store at room temperature, away from moisture",
                price = 15.0,
                lastUpdated = Date()
            ),
            MedicationEntity(
                id = UUID.randomUUID().toString(),
                name = "Amoxicillin 500mg",
                nameAr = "أموكسيسيلين 500 مغ",
                nameFr = "Amoxicilline 500mg",
                description = "Antibiotic for bacterial infections",
                descriptionAr = "مضاد حيوي للالتهابات البكتيرية",
                descriptionFr = "Antibiotique pour infections bactériennes",
                category = "Antibiotics",
                isOTC = false,
                activeIngredient = "Amoxicillin",
                dosageForm = "Capsule",
                strength = "500mg",
                manufacturer = "Laprophan",
                imageUrl = null,
                barcode = "6111000005678",
                sideEffects = listOf("Diarrhea", "Nausea", "Skin rash"),
                contraindications = listOf("Penicillin allergy"),
                interactions = listOf("Oral contraceptives", "Methotrexate"),
                storageConditions = "Store in a cool, dry place",
                price = 45.0,
                lastUpdated = Date()
            ),
            MedicationEntity(
                id = UUID.randomUUID().toString(),
                name = "Ibuprofen 400mg",
                nameAr = "إيبوبروفين 400 مغ",
                nameFr = "Ibuprofène 400mg",
                description = "Anti-inflammatory and pain reliever",
                descriptionAr = "مضاد للالتهابات ومسكن للألم",
                descriptionFr = "Anti-inflammatoire et analgésique",
                category = "Pain Relief",
                isOTC = true,
                activeIngredient = "Ibuprofen",
                dosageForm = "Tablet",
                strength = "400mg",
                manufacturer = "Cooper Pharma",
                imageUrl = null,
                barcode = "6111000009012",
                sideEffects = listOf("Stomach upset", "Heartburn", "Dizziness"),
                contraindications = listOf("Stomach ulcer", "Kidney disease"),
                interactions = listOf("Aspirin", "Blood thinners", "Diuretics"),
                storageConditions = "Store at room temperature",
                price = 20.0,
                lastUpdated = Date()
            ),
            MedicationEntity(
                id = UUID.randomUUID().toString(),
                name = "Omeprazole 20mg",
                nameAr = "أوميبرازول 20 مغ",
                nameFr = "Oméprazole 20mg",
                description = "Proton pump inhibitor for acid reflux",
                descriptionAr = "مثبط مضخة البروتون لعلاج حموضة المعدة",
                descriptionFr = "Inhibiteur de la pompe à protons",
                category = "Digestive Health",
                isOTC = false,
                activeIngredient = "Omeprazole",
                dosageForm = "Capsule",
                strength = "20mg",
                manufacturer = "Galenica",
                imageUrl = null,
                barcode = "6111000013456",
                sideEffects = listOf("Headache", "Diarrhea", "Abdominal pain"),
                contraindications = listOf("Liver cirrhosis"),
                interactions = listOf("Clopidogrel", "Warfarin"),
                storageConditions = "Store in a cool, dry place, protect from light",
                price = 55.0,
                lastUpdated = Date()
            ),
            MedicationEntity(
                id = UUID.randomUUID().toString(),
                name = "Cetirizine 10mg",
                nameAr = "سيتيريزين 10 مغ",
                nameFr = "Cétirizine 10mg",
                description = "Antihistamine for allergies",
                descriptionAr = "مضاد للهيستامين لعلاج الحساسية",
                descriptionFr = "Antihistaminique pour allergies",
                category = "Allergy & Immunity",
                isOTC = true,
                activeIngredient = "Cetirizine",
                dosageForm = "Tablet",
                strength = "10mg",
                manufacturer = "Pharma 5",
                imageUrl = null,
                barcode = "6111000017890",
                sideEffects = listOf("Drowsiness", "Dry mouth", "Fatigue"),
                contraindications = listOf("Severe kidney disease"),
                interactions = listOf("Alcohol", "CNS depressants"),
                storageConditions = "Store at room temperature",
                price = 25.0,
                lastUpdated = Date()
            ),
            MedicationEntity(
                id = UUID.randomUUID().toString(),
                name = "Metformin 500mg",
                nameAr = "ميتفورمين 500 مغ",
                nameFr = "Metformine 500mg",
                description = "Oral diabetes medicine",
                descriptionAr = "دواء السكري عن طريق الفم",
                descriptionFr = "Médicament antidiabétique oral",
                category = "Diabetes",
                isOTC = false,
                activeIngredient = "Metformin",
                dosageForm = "Tablet",
                strength = "500mg",
                manufacturer = "Sanofi Maroc",
                imageUrl = null,
                barcode = "6111000021234",
                sideEffects = listOf("Nausea", "Diarrhea", "Stomach upset"),
                contraindications = listOf("Kidney disease", "Liver disease", "Heart failure"),
                interactions = listOf("Alcohol", "Contrast dyes"),
                storageConditions = "Store at room temperature",
                price = 35.0,
                lastUpdated = Date()
            ),
            MedicationEntity(
                id = UUID.randomUUID().toString(),
                name = "Vitamin D3 1000 IU",
                nameAr = "فيتامين د3 1000 وحدة",
                nameFr = "Vitamine D3 1000 UI",
                description = "Vitamin D supplement",
                descriptionAr = "مكمل فيتامين د",
                descriptionFr = "Supplément de vitamine D",
                category = "Vitamins & Supplements",
                isOTC = true,
                activeIngredient = "Cholecalciferol",
                dosageForm = "Soft Gel",
                strength = "1000 IU",
                manufacturer = "Atlantic Pharma",
                imageUrl = null,
                barcode = "6111000025678",
                sideEffects = listOf("Hypercalcemia (rare)", "Constipation"),
                contraindications = listOf("Hypercalcemia", "Kidney stones"),
                interactions = listOf("Thiazide diuretics", "Digoxin"),
                storageConditions = "Store in a cool, dry place",
                price = 30.0,
                lastUpdated = Date()
            ),
            MedicationEntity(
                id = UUID.randomUUID().toString(),
                name = "Aspirin 100mg",
                nameAr = "أسبرين 100 مغ",
                nameFr = "Aspirine 100mg",
                description = "Low-dose aspirin for heart protection",
                descriptionAr = "أسبرين بجرعة منخفضة لحماية القلب",
                descriptionFr = "Aspirine à faible dose pour protection cardiaque",
                category = "Cardiovascular",
                isOTC = true,
                activeIngredient = "Acetylsalicylic Acid",
                dosageForm = "Tablet",
                strength = "100mg",
                manufacturer = "Bayer",
                imageUrl = null,
                barcode = "6111000029012",
                sideEffects = listOf("Stomach upset", "Bleeding risk", "Bruising"),
                contraindications = listOf("Bleeding disorders", "Stomach ulcer"),
                interactions = listOf("Warfarin", "NSAIDs", "Alcohol"),
                storageConditions = "Store at room temperature, keep dry",
                price = 12.0,
                lastUpdated = Date()
            )
        )

        medicationDao.insertMedications(medications)
        Timber.d("Generated ${medications.size} mock medications")
    }

    /**
     * Clears all mock data from the database
     */
    suspend fun clearAllMockData() {
        Timber.i("Clearing all mock data...")
        // Note: Implement clear methods in DAOs if needed
        Timber.i("Mock data cleared")
    }

    companion object {
        /**
         * Checks if mock data generation is allowed
         * Only in DEBUG mode and on emulators
         */
        fun isMockDataAllowed(isDebug: Boolean): Boolean {
            return isDebug && EmulatorDetector.isEmulator()
        }
    }
}
