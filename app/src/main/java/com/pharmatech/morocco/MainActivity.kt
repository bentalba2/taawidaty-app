package com.pharmatech.morocco

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.pharmatech.morocco.core.utils.LanguagePreferenceManager
import com.pharmatech.morocco.ui.theme.TaawidatyThemeProvider
import com.pharmatech.morocco.ui.navigation.PharmaTechNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply saved language preference before setting content
        applyLanguagePreference()

        enableEdgeToEdge()

        setContent {
            TaawidatyThemeProvider {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // TAAWIDATY: Health companion with medication tracking & health tools
                    PharmaTechNavigation()
                }
            }
        }
    }

    private fun applyLanguagePreference() {
        val languagePreferenceManager = LanguagePreferenceManager(applicationContext)
        runBlocking {
            val languageCode = languagePreferenceManager.getLanguage()
            val locale = languagePreferenceManager.getLocaleFromLanguageCode(languageCode)

            Locale.setDefault(locale)
            val config = Configuration(resources.configuration)
            config.setLocale(locale)

            @Suppress("DEPRECATION")
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }
}
