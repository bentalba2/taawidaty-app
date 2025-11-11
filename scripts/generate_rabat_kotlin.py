#!/usr/bin/env python3
"""
Generate RabatPharmacyData.kt from geocoded Rabat pharmacies with realistic ratings
"""

import json
import random
from pathlib import Path

# Paths
RABAT_JSON = Path(__file__).parent / "rabat_pharmacies.json"
OUTPUT_DIR = Path(__file__).parent.parent / "app/src/main/java/com/pharmatech/morocco/features/pharmacy/domain/model"
OUTPUT_FILE = OUTPUT_DIR / "RabatPharmacyData.kt"

def generate_realistic_rating():
    """Generate realistic pharmacy rating (3.5-5.0)"""
    rand = random.random()
    if rand < 0.70:  # 70% good ratings (4.0-4.8)
        return round(random.uniform(4.0, 4.8), 1)
    elif rand < 0.90:  # 20% excellent ratings (4.8-5.0)
        return round(random.uniform(4.8, 5.0), 1)
    else:  # 10% decent ratings (3.5-4.0)
        return round(random.uniform(3.5, 4.0), 1)

def main():
    print("Loading Rabat pharmacies...")
    with open(RABAT_JSON, 'r', encoding='utf-8') as f:
        pharmacies = json.load(f)
    
    print(f"Loaded {len(pharmacies)} pharmacies")
    
    # Add ratings to pharmacies
    rated_count = 0
    for p in pharmacies:
        if p.get('geocoded', False):
            p['rating'] = generate_realistic_rating()
            p['reviewCount'] = random.randint(1, 50)
            rated_count += 1
    
    print(f"Added ratings to {rated_count} geocoded pharmacies")
    
    # Calculate average rating
    ratings = [p['rating'] for p in pharmacies if 'rating' in p]
    avg_rating = sum(ratings) / len(ratings) if ratings else 0
    print(f"Average rating: {avg_rating:.2f}")
    
    # Generate Kotlin code
    print("Generating Kotlin code...")
    kotlin_code = '''package com.pharmatech.morocco.features.pharmacy.domain.model

import java.util.Date

/**
 * Rabat Pharmacy Data
 * Auto-generated from geocoded data
 * Total: {total} pharmacies
 * Geocoded: {geocoded} ({geocoded_pct}%)
 * Average Rating: {avg_rating:.2f}
 */
object RabatPharmacyData {{
    
    val pharmacies = listOf(
'''.format(
        total=len(pharmacies),
        geocoded=rated_count,
        geocoded_pct=round(rated_count / len(pharmacies) * 100, 1),
        avg_rating=avg_rating
    )
    
    # Generate pharmacy entries
    for idx, p in enumerate(pharmacies, start=1):
        name = p.get('name', 'Unknown').replace('"', '\\"').replace("'", "\\'")
        phone = p.get('phone', '')
        address = p.get('address', name).replace('"', '\\"').replace("'", "\\'")
        city = p.get('city', 'Rabat')
        geocoded = str(p.get('geocoded', False)).lower()
        lat = p.get('latitude', 0.0)
        lon = p.get('longitude', 0.0)
        rating = p.get('rating', 0.0)
        review_count = p.get('reviewCount', 0)
        
        kotlin_code += f'''        Pharmacy(
            id = "pharmacy_rabat_{idx:04d}",
            name = "{name}",
            nameAr = null,
            address = "{address}",
            addressAr = null,
            city = "{city}",
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
        )'''
        
        if idx < len(pharmacies):
            kotlin_code += ',\n'
        else:
            kotlin_code += '\n'
    
    kotlin_code += '''    )
    
    fun getAllPharmacies(): List<Pharmacy> = pharmacies
    
    fun getPharmaciesByCity(city: String): List<Pharmacy> {
        return pharmacies.filter { it.city.equals(city, ignoreCase = true) }
    }
    
    fun getGeocodedPharmacies(): List<Pharmacy> {
        return pharmacies.filter { it.geocoded }
    }
    
    fun searchPharmacies(query: String): List<Pharmacy> {
        val lowerQuery = query.lowercase()
        return pharmacies.filter {
            it.name.lowercase().contains(lowerQuery) ||
            it.address.lowercase().contains(lowerQuery) ||
            it.phone.contains(query)
        }
    }
}
'''
    
    # Write to file
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        f.write(kotlin_code)
    
    print(f"\n✓ Generated: {OUTPUT_FILE}")
    print(f"✓ Total pharmacies: {len(pharmacies)}")
    print(f"✓ Geocoded with ratings: {rated_count}")
    print(f"✓ Average rating: {avg_rating:.2f}")
    print(f"✓ File size: {OUTPUT_FILE.stat().st_size:,} bytes")

if __name__ == "__main__":
    main()
