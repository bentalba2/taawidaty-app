"""
Full pharmacy geocoding using Google Places API
"""
import json
import requests
import time
from datetime import datetime

API_KEY = "AIzaSyAOX83Yil9oQI_oOaf86LA8gTUyRB-qtFo"
PLACES_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json"

print("="*70)
print("TAAWIDATY Pharmacy Geocoding - Google Places API")
print("="*70)

# Load raw pharmacy data
print("\n[1/3] Loading raw pharmacy data...")
with open('/Users/zakaria/pharmatech-morocco/scripts/pharmacies_raw.json', 'r', encoding='utf-8') as f:
    raw_pharmacies = json.load(f)

print(f"✓ Loaded {len(raw_pharmacies)} pharmacies")

# Geocode all pharmacies
print(f"\n[2/3] Geocoding {len(raw_pharmacies)} pharmacies using Places API...")
print(f"Estimated cost: ${len(raw_pharmacies) * 0.017:.2f}")  # Text Search: $17 per 1000
print("This will take approximately 2-3 minutes...")
print()

geocoded_pharmacies = []
success_count = 0
failed_count = 0

for i, pharmacy_raw in enumerate(raw_pharmacies, 1):
    name = pharmacy_raw['name']
    city = pharmacy_raw['city']
    
    # Search using Places API
    query = f"{name}, {city}, Morocco"
    params = {
        'query': query,
        'key': API_KEY,
        'region': 'ma',
        'type': 'pharmacy'
    }
    
    if i % 50 == 0:
        print(f"Progress: {i}/{len(raw_pharmacies)} ({i*100/len(raw_pharmacies):.1f}%)")
    
    try:
        response = requests.get(PLACES_URL, params=params, timeout=10)
        data = response.json()
        
        if data['status'] == 'OK' and len(data['results']) > 0:
            result = data['results'][0]
            location = result['geometry']['location']
            
            pharmacy = {
                'id': f"pharmacy_kenitra_{i:04d}",
                'name': name,
                'name_ar': None,
                'address': result.get('formatted_address', pharmacy_raw['address']),
                'address_ar': None,
                'city': city,
                'latitude': location['lat'],
                'longitude': location['lng'],
                'phone_number': pharmacy_raw['phone'],
                'email': None,
                'website': None,
                'opening_hours': result.get('opening_hours', {}).get('weekday_text', ["Lun-Ven: 09:00-19:00"])[0] if 'opening_hours' in result else "Lun-Ven: 09:00-19:00 | Sam: 09:00-13:00",
                'is_24_hours': False,
                'has_parking': False,
                'is_guard_pharmacy': False,
                'rating': result.get('rating', 0.0),
                'review_count': result.get('user_ratings_total', 0),
                'image_url': None,
                'services': [],
                'distance': None,
                'last_updated': datetime.now().isoformat(),
                'geocoded': True,
                'geocode_status': 'OK',
                'place_id': result.get('place_id', '')
            }
            success_count += 1
        else:
            # Not found - add with default Kenitra coordinates
            pharmacy = {
                'id': f"pharmacy_kenitra_{i:04d}",
                'name': name,
                'name_ar': None,
                'address': pharmacy_raw['address'],
                'address_ar': None,
                'city': city,
                'latitude': 34.261, # Kenitra center
                'longitude': -6.587,
                'phone_number': pharmacy_raw['phone'],
                'email': None,
                'website': None,
                'opening_hours': "Lun-Ven: 09:00-19:00 | Sam: 09:00-13:00",
                'is_24_hours': False,
                'has_parking': False,
                'is_guard_pharmacy': False,
                'rating': 0.0,
                'review_count': 0,
                'image_url': None,
                'services': [],
                'distance': None,
                'last_updated': datetime.now().isoformat(),
                'geocoded': False,
                'geocode_status': data['status']
            }
            failed_count += 1
            
        geocoded_pharmacies.append(pharmacy)
        
        # Save checkpoint every 100 pharmacies
        if i % 100 == 0:
            with open(f'checkpoint_places_{i}.json', 'w', encoding='utf-8') as f:
                json.dump(geocoded_pharmacies, f, indent=2, ensure_ascii=False)
            print(f"  Checkpoint saved: {i} pharmacies processed")
            
    except Exception as e:
        print(f"  Error at {i}: {e}")
        # Add with default coordinates
        pharmacy = {
            'id': f"pharmacy_kenitra_{i:04d}",
            'name': name,
            'address': pharmacy_raw['address'],
            'city': city,
            'latitude': 34.261,
            'longitude': -6.587,
            'phone_number': pharmacy_raw['phone'],
            'geocoded': False,
            'geocode_status': 'ERROR'
        }
        geocoded_pharmacies.append(pharmacy)
        failed_count += 1
    
    # Rate limiting - Places API has generous limits but let's be safe
    time.sleep(0.15)  # ~6-7 requests per second

print(f"\n✓ Geocoding complete!")
print(f"  Success: {success_count}/{len(raw_pharmacies)} ({success_count*100/len(raw_pharmacies):.1f}%)")
print(f"  Failed: {failed_count}")

# Save final results
print("\n[3/3] Saving results...")

# Save JSON
with open('pharmacies_kenitra_final.json', 'w', encoding='utf-8') as f:
    json.dump(geocoded_pharmacies, f, indent=2, ensure_ascii=False)
print("✓ Saved: pharmacies_kenitra_final.json")

# Generate Kotlin file
print("Generating Kotlin data file...")

kotlin_code = f'''package com.pharmatech.morocco.features.pharmacy.domain.model

import java.util.Date
import java.text.SimpleDateFormat

/**
 * Kenitra Pharmacy Data - Generated from Google Places API
 * Generated: {datetime.now().isoformat()}
 * Total: {len(geocoded_pharmacies)} pharmacies
 * Geocoded: {success_count} ({success_count*100/len(geocoded_pharmacies):.1f}%)
 */
object KenitraPharmacyData {{
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    
    val pharmacies = listOf(
'''

# Add each pharmacy
for i, p in enumerate(geocoded_pharmacies):
    # Escape strings properly
    name_escaped = p['name'].replace('"', '\\"')
    address_escaped = p['address'].replace('"', '\\"')
    opening_escaped = p['opening_hours'].replace('"', '\\"')
    comma = "," if i < len(geocoded_pharmacies) - 1 else ""
    
    kotlin_code += f'''        Pharmacy(
            id = "{p['id']}",
            name = "{name_escaped}",
            nameAr = null,
            address = "{address_escaped}",
            addressAr = null,
            city = "{p['city']}",
            latitude = {p['latitude']},
            longitude = {p['longitude']},
            phoneNumber = "{p['phone_number']}",
            email = null,
            website = null,
            openingHours = "{opening_escaped}",
            is24Hours = {str(p['is_24_hours']).lower()},
            hasParking = {str(p['has_parking']).lower()},
            isGuardPharmacy = {str(p['is_guard_pharmacy']).lower()},
            rating = {p['rating']}f,
            reviewCount = {p['review_count']},
            imageUrl = null,
            services = emptyList(),
            distance = null,
            lastUpdated = dateFormat.parse("{p['last_updated']}") ?: Date()
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
    
    fun getNearbyPharmacies(latitude: Double, longitude: Double, radiusKm: Double = 5.0): List<Pharmacy> {
        return pharmacies.filter {
            val distance = calculateDistance(latitude, longitude, it.latitude, it.longitude)
            distance <= radiusKm
        }.sortedBy { it.distance }
    }
    
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }
}
'''

with open('KenitraPharmacyData_final.kt', 'w', encoding='utf-8') as f:
    f.write(kotlin_code)
print("✓ Generated: KenitraPharmacyData_final.kt")

print("\n" + "="*70)
print("RESULTS SUMMARY")
print("="*70)
print(f"Total pharmacies: {len(geocoded_pharmacies)}")
print(f"Successfully geocoded: {success_count} ({success_count*100/len(geocoded_pharmacies):.1f}%)")
print(f"Failed geocoding: {failed_count}")
print(f"API cost estimate: ${len(geocoded_pharmacies) * 0.017:.2f}")
print()
print("Output files:")
print("  - pharmacies_kenitra_final.json (complete JSON data)")
print("  - KenitraPharmacyData_final.kt (Kotlin data class)")
print("="*70)
print("\n✓ All done! Copy the Kotlin file to your app:")
print("cp KenitraPharmacyData_final.kt ../app/src/main/java/com/pharmatech/morocco/features/pharmacy/domain/model/KenitraPharmacyData.kt")
