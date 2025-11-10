#!/usr/bin/env python3
import json
import requests
import sys
import time

sys.stdout = open(sys.stdout.fileno(), mode='w', buffering=1)

API_KEY = 'AIzaSyAOX83Yil9oQI_oOaf86LA8gTUyRB-qtFo'
PLACES_URL = 'https://maps.googleapis.com/maps/api/place/textsearch/json'

print("Loading Rabat pharmacies...")
with open('/Users/zakaria/pharmatech-morocco/scripts/rabat_pharmacies_raw.json', 'r') as f:
    pharmacies = json.load(f)

print(f"Loaded: {len(pharmacies)}")

success = 0
for i, p in enumerate(pharmacies, 1):
    try:
        params = {'query': f"{p['name']}, Rabat, Morocco", 'key': API_KEY}
        r = requests.get(PLACES_URL, params=params, timeout=5)
        data = r.json()
        
        if data['status'] == 'OK':
            loc = data['results'][0]['geometry']['location']
            p['latitude'] = loc['lat']
            p['longitude'] = loc['lng']
            p['geocoded'] = True
            success += 1
        
        if i % 50 == 0:
            print(f"{i}/{len(pharmacies)} - {success} successful")
            with open(f'/Users/zakaria/pharmatech-morocco/scripts/rabat_checkpoint_{i}.json', 'w') as f:
                json.dump(pharmacies, f)
        
        time.sleep(0.15)
    except:
        pass

with open('/Users/zakaria/pharmatech-morocco/scripts/rabat_pharmacies.json', 'w') as f:
    json.dump(pharmacies, f, indent=2, ensure_ascii=False)

print(f"\nDone: {success}/{len(pharmacies)} ({success*100/len(pharmacies):.1f}%)")
