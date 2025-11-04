package com.pharmatech.morocco.ui.navigation

sealed class Screen(val route: String) {
    // Auth
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")

    // Main
    object Home : Screen("home")
    object Pharmacy : Screen("pharmacy")
    object Hospital : Screen("hospital")
    object Medication : Screen("medication")
    object Insurance : Screen("insurance?medication={medication}") {
        fun createRoute(medicationName: String? = null): String {
            return if (medicationName != null) {
                "insurance?medication=$medicationName"
            } else {
                "insurance"
            }
        }
    }
    object Tracker : Screen("tracker")
    object Profile : Screen("profile")

    // Details
    object PharmacyDetail : Screen("pharmacy_detail/{pharmacyId}") {
        fun createRoute(pharmacyId: String) = "pharmacy_detail/$pharmacyId"
    }
    object MedicationDetail : Screen("medication_detail/{medicationId}") {
        fun createRoute(medicationId: String) = "medication_detail/$medicationId"
    }

    // Features
    object AddMedication : Screen("add_medication")
    object Scanner : Screen("scanner")
    object AISymptomChecker : Screen("ai_symptom_checker")
    object ARViewer : Screen("ar_viewer/{medicationId}") {
        fun createRoute(medicationId: String) = "ar_viewer/$medicationId"
    }
    object HealthInsights : Screen("health_insights")
    object Reminders : Screen("reminders")
    object Settings : Screen("settings")
}

