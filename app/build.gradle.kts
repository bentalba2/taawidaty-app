import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version "1.9.0"
}

// Load local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

fun String.escapeForBuildConfig(): String = this
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")

val mapsApiKey: String = localProperties.getProperty("MAPS_API_KEY") 
    ?: (project.findProperty("MAPS_API_KEY") as? String)
    ?: ""

val aiApiKey: String = localProperties.getProperty("AI_API_KEY")
    ?: (project.findProperty("AI_API_KEY") as? String)
    ?: ""

android {
    namespace = "com.pharmatech.morocco"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pharmatech.morocco"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Enable MultiDex for large libraries like Apache POI
        multiDexEnabled = true

        vectorDrawables {
            useSupportLibrary = true
        }

        // BuildConfig fields - API Base URL only
        buildConfigField("String", "API_BASE_URL", "\"https://api.pharmatech.ma/v1/\"")
        
    // Use manifest placeholder for Maps API key (more secure than BuildConfig)
    val resolvedMapsKey = if (mapsApiKey.isNotBlank()) mapsApiKey else "YOUR_API_KEY_HERE"
    manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = resolvedMapsKey

    // Provide AI API key via BuildConfig (safe fallback to empty string)
    val sanitizedAiKey = if (aiApiKey.isNotBlank()) aiApiKey.escapeForBuildConfig() else ""
    buildConfigField("String", "AI_API_KEY", "\"$sanitizedAiKey\"")
        
        // Room schema export configuration
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas"
                )
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            // Removed applicationIdSuffix to match Firebase configuration
            versionNameSuffix = "-DEBUG"
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE*"
            excludes += "/META-INF/NOTICE*"
            excludes += "META-INF/versions/9/previous-compilation-data.bin"
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Java 8+ API desugaring support for LocalDate
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.animation:animation")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-work:1.1.0")
    kapt("androidx.hilt:hilt-compiler:1.1.0")

    // Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Firebase - Using BoM 32.7.0 (34.5.0 has dependency resolution issues)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // Google Services
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("com.google.android.gms:play-services-ads:22.6.0")

    // Google Maps Compose
    implementation("com.google.maps.android:maps-compose:4.3.0")

    // ML Kit
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation("com.google.mlkit:image-labeling:17.0.8")

    // AR Core
    implementation("com.google.ar:core:1.41.0")

    // Animations
    implementation("com.airbnb.android:lottie-compose:6.3.0")

    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Biometric
    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    // Timber Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Apache POI for Excel file import (optimized)
    implementation("org.apache.poi:poi:5.2.5") {
        exclude(group = "stax", module = "stax-api")
        exclude(group = "xml-apis", module = "xml-apis")
    }
    implementation("org.apache.poi:poi-ooxml:5.2.5") {
        exclude(group = "stax", module = "stax-api")
        exclude(group = "xml-apis", module = "xml-apis")
        exclude(group = "org.apache.xmlbeans", module = "xmlbeans")
    }
    // Use lightweight ooxml-lite instead of full schemas
    implementation("org.apache.poi:poi-ooxml-lite:5.2.5")
    
    // MultiDex support
    implementation("androidx.multidex:multidex:2.0.1")

    // Google Maps Compose
    implementation("com.google.maps.android:maps-compose:4.3.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

