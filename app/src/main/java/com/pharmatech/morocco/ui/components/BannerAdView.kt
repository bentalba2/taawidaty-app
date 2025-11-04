package com.pharmatech.morocco.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * Banner Ad View component for AdMob integration
 * Uses test ad unit ID for development
 * 
 * To use production ads:
 * 1. Create AdMob account at admob.google.com
 * 2. Add app to AdMob
 * 3. Create banner ad unit
 * 4. Replace TEST_AD_UNIT_ID with your production ad unit ID
 */
@Composable
fun BannerAdView(
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        factory = { context ->
            AdView(context).apply {
                // Test ad unit ID for banner ads
                // Production: Replace with your own ad unit ID from AdMob
                adUnitId = "ca-app-pub-3940256099942544/6300978111"
                
                setAdSize(AdSize.BANNER)
                
                // Load the ad
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

/**
 * Large Banner Ad View (728x90)
 * Use this for tablets or landscape mode
 */
@Composable
fun LargeBannerAdView(
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        factory = { context ->
            AdView(context).apply {
                adUnitId = "ca-app-pub-3940256099942544/6300978111" // Test ID
                setAdSize(AdSize.LARGE_BANNER)
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

/**
 * Smart Banner Ad View
 * Automatically adjusts size based on device screen
 */
@Composable
fun SmartBannerAdView(
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        factory = { context ->
            AdView(context).apply {
                adUnitId = "ca-app-pub-3940256099942544/6300978111" // Test ID
                
                @Suppress("DEPRECATION")
                setAdSize(AdSize.SMART_BANNER)
                
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
