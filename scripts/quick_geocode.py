#!/usr/bin/env python3
"""
Simple Places API geocoder - no buffering issues
"""
import json
import requests
import time
import sys
from datetime import datetime

# Disable buffering
sys.stdout = open(sys.stdout.fileno(), mode='w', buffering=1)
sys.stderr = open(sys.stderr.fileno(), mode='w', buffering=1)

API_KEY = "AIzaSyAOX83Yil9oQI_oOaf86LA8gTUyRB-qtFo"
PLACES_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json"

print("Loading pharmacies...")
with open('pharmacies_raw.json', 'r', encoding='utf-8') as f:
    raw_pharmacies = json.load(f)
print(f"Loaded: {len(raw_pharmacies)}")

geocoded = []
success = 0

for i, pharm in enumerate(raw_pharmacies, 1):
    params = {
        'query': f"{pharm['name']}, Kenitra, Morocco",
        'key': API_KEY
    }
    
    try:
        r = requests.get(PLACES_URL, params=params, timeout=5)
        data = r.json()
        
        if data['status'] == 'OK' and data['results']:
            loc = data['results'][0]['geometry']['location']
            pharm['latitude'] = loc['lat']
            pharm['longitude'] = loc['lng']
            pharm['geocoded'] = True
            success += 1
        else:
            pharm['latitude'] = 34.261
            pharm['longitude'] = -6.587
            pharm['geocoded'] = False
        
        geocoded.append(pharm)
        
        if i % 50 == 0:
            print(f"{i}/{len(raw_pharmacies)} - {success} successful")
            with open(f'quick_checkpoint_{i}.json', 'w') as f:
                json.dump(geocoded, f)
        
        time.sleep(0.1)
        
    except Exception as e:
        print(f"Error {i}: {e}")
        pharm['latitude'] = 34.261
        pharm['longitude'] = -6.587
        pharm['geocoded'] = False
        geocoded.append(pharm)

print(f"\nDone: {success}/{len(raw_pharmacies)}")

with open('pharmacies_quick.json', 'w', encoding='utf-8') as f:
    json.dump(geocoded, f, indent=2, ensure_ascii=False)

print("Saved: pharmacies_quick.json")
