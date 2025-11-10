"""
Geocode pharmacies using Google Places API (Text Search)
This is a fallback since Geocoding API is not enabled
"""
import json
import requests
import time

API_KEY = "AIzaSyAOX83Yil9oQI_oOaf86LA8gTUyRB-qtFo"
PLACES_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json"

# Load raw pharmacy data
with open('/Users/zakaria/pharmatech-morocco/scripts/pharmacies_raw.json', 'r', encoding='utf-8') as f:
    raw_pharmacies = json.load(f)

print(f"Loaded {len(raw_pharmacies)} pharmacies")
print("Using Google Places API Text Search...")
print("="*60)

geocoded_pharmacies = []
success_count = 0

for i, pharmacy_raw in enumerate(raw_pharmacies[:10], 1):  # Test with first 10
    name = pharmacy_raw['name']
    city = pharmacy_raw['city']
    
    # Search using Places API
    query = f"{name}, {city}, Morocco"
    params = {
        'query': query,
        'key': API_KEY,
        'region': 'ma'
    }
    
    print(f"{i}/{len(raw_pharmacies)}: Searching for {name}...")
    
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
                'opening_hours': "Lun-Ven: 09:00-19:00 | Sam: 09:00-13:00",
                'is_24_hours': False,
                'has_parking': False,
                'is_guard_pharmacy': False,
                'rating': result.get('rating', 0.0),
                'review_count': result.get('user_ratings_total', 0),
                'image_url': None,
                'services': [],
                'distance': None,
                'last_updated': time.strftime('%Y-%m-%dT%H:%M:%S'),
                'geocoded': True,
                'geocode_status': 'OK',
                'place_id': result.get('place_id', '')
            }
            geocoded_pharmacies.append(pharmacy)
            success_count += 1
            print(f"  ✓ Found at {location['lat']:.6f}, {location['lng']:.6f}")
        else:
            print(f"  ✗ Not found: {data['status']}")
            # Add with 0,0 coordinates
            pharmacy = {
                'id': f"pharmacy_kenitra_{i:04d}",
                'name': name,
                'address': pharmacy_raw['address'],
                'city': city,
                'latitude': 0.0,
                'longitude': 0.0,
                'phone_number': pharmacy_raw['phone'],
                'geocoded': False,
                'geocode_status': data['status']
            }
            geocoded_pharmacies.append(pharmacy)
            
    except Exception as e:
        print(f"  ✗ Error: {e}")
    
    time.sleep(0.2)  # Rate limiting

print("\n" + "="*60)
print(f"Results: {success_count}/{len(raw_pharmacies[:10])} successfully geocoded")
print("="*60)

# Save results
with open('test_places_api.json', 'w', encoding='utf-8') as f:
    json.dump(geocoded_pharmacies, f, indent=2, ensure_ascii=False)

print("Saved to: test_places_api.json")
