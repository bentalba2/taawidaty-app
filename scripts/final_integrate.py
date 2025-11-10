#!/usr/bin/env python3
"""
Final integration - Generate Kotlin file and integrate
"""
import json
import os
from datetime import datetime

print("="*70)
print("FINAL INTEGRATION - TAAWIDATY Pharmacy Data")
print("="*70)

# Wait for geocoding to finish
import time
import subprocess

print("\nWaiting for geocoding to complete...")
while True:
    result = subprocess.run(['pgrep', '-f', 'quick_geocode.py'], capture_output=True)
    if result.returncode != 0:
        break
    print(".", end="", flush=True)
    time.sleep(5)

print("\n✓ Geocoding complete!")

# Load final data
print("\nLoading geocoded data...")
with open('/Users/zakaria/pharmatech-morocco/scripts/pharmacies_quick.json', 'r', encoding='utf-8') as f:
    pharmacies = json.load(f)

print(f"✓ Loaded {len(pharmacies)} pharmacies")

# Count successful geocodes
success = sum(1 for p in pharmacies if p.get('geocoded', False))
print(f"✓ Successfully geocoded: {success}/{len(pharmacies)} ({success*100/len(pharmacies):.1f}%)")

# Generate Kotlin file
print("\nGenerating Kotlin data file...")

kotlin_code = f'''package com.pharmatech.morocco.features.pharmacy.domain.model

import java.util.Date

/**
 * Kenitra Pharmacy Data
 * Generated: {datetime.now().strftime("%Y-%m-%d %H:%M:%S")}
 * Total: {len(pharmacies)} pharmacies
 * Geocoded: {success} ({success*100/len(pharmacies):.1f}%)
 * Source: Google Places API
 */
object KenitraPharmacyData {{
    
    val pharmacies = listOf(
'''

for i, p in enumerate(pharmacies):
    name = p['name'].replace('"', '\\"').replace("'", "\\'")
    addr = p['address'].replace('"', '\\"').replace("'", "\\'")
    phone = p.get('phone', '')
    lat = p.get('latitude', 34.261)
    lon = p.get('longitude', -6.587)
    rating = p.get('rating', 0.0)
    review_count = p.get('reviewCount', 0)
    geocoded = str(p.get('geocoded', True)).lower()
    comma = "," if i < len(pharmacies) - 1 else ""
    
    kotlin_code += f'''        Pharmacy(
            id = "pharmacy_{i+1:04d}",
            name = "{name}",
            nameAr = null,
            address = "{addr}",
            addressAr = null,
            city = "Kenitra",
            latitude = {lat},
            longitude = {lon},
            phoneNumber = "{phone}",
            email = null,
            website = null,
            openingHours = "Lun-Ven: 09:00-19:00",
            is24Hours = false,
            hasParking = false,
            isGuardPharmacy = false,
            rating = {rating},
            reviewCount = {review_count},
            imageUrl = null,
            services = emptyList(),
            distance = null,
            lastUpdated = Date(),
            geocoded = {geocoded}
        ){comma}
'''

kotlin_code += '''    )
    
    fun getAllPharmacies(): List<Pharmacy> = pharmacies
    
    fun getPharmacyById(id: String): Pharmacy? = pharmacies.find { it.id == id }
    
    fun searchPharmacies(query: String): List<Pharmacy> {
        val lowerQuery = query.lowercase()
        return pharmacies.filter {
            it.name.lowercase().contains(lowerQuery) ||
            it.address.lowercase().contains(lowerQuery) ||
            it.phoneNumber.contains(query)
        }
    }
}
'''

# Save Kotlin file
kotlin_path = '/Users/zakaria/pharmatech-morocco/scripts/KenitraPharmacyData.kt'
with open(kotlin_path, 'w', encoding='utf-8') as f:
    f.write(kotlin_code)

print(f"✓ Saved: {kotlin_path}")

# Copy to app
dest_dir = '/Users/zakaria/pharmatech-morocco/app/src/main/java/com/pharmatech/morocco/features/pharmacy/domain/model/'
dest_path = os.path.join(dest_dir, 'KenitraPharmacyData.kt')

os.makedirs(dest_dir, exist_ok=True)
with open(dest_path, 'w', encoding='utf-8') as f:
    f.write(kotlin_code)

print(f"✓ Copied to: {dest_path}")

print("\n" + "="*70)
print("SUCCESS! Pharmacy data integrated into app")
print("="*70)
print(f"\nStatistics:")
print(f"  Total pharmacies: {len(pharmacies)}")
print(f"  Geocoded: {success} ({success*100/len(pharmacies):.1f}%)")
print(f"  With phone: {sum(1 for p in pharmacies if p.get('phone'))}")
print(f"\nFiles generated:")
print(f"  - pharmacies_quick.json (JSON data)")
print(f"  - KenitraPharmacyData.kt (Kotlin file - INTEGRATED)")
print(f"\nNext steps:")
print(f"  1. Update PharmacyMapScreen.kt to use KenitraPharmacyData")
print(f"  2. Build: ./gradlew assembleDebug")
print(f"  3. Test on emulator")
print("="*70)
