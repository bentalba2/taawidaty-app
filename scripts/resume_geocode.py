#!/usr/bin/env python3
import json, requests, sys, time
sys.stdout = open(sys.stdout.fileno(), mode='w', buffering=1)

# Load original full list
with open('/Users/zakaria/pharmatech-morocco/scripts/pharmacies_raw.json', 'r') as f:
    raw_pharmacies = json.load(f)

# Load completed from checkpoint
with open('/Users/zakaria/pharmatech-morocco/scripts/quick_checkpoint_400.json', 'r') as f:
    completed = json.load(f)

# Merge: first 400 from checkpoint, rest from raw
pharmacies = completed + raw_pharmacies[400:]

print(f"Loaded: {len(pharmacies)} pharmacies")
print(f"Already geocoded: {sum(1 for p in pharmacies[:400] if p.get('geocoded'))}/400")
print(f"Resuming from pharmacy 401...")

API_KEY = 'AIzaSyAOX83Yil9oQI_oOaf86LA8gTUyRB-qtFo'
PLACES_URL = 'https://maps.googleapis.com/maps/api/place/textsearch/json'
success = sum(1 for p in pharmacies[:400] if p.get('geocoded'))

for i in range(400, len(pharmacies)):
    p = pharmacies[i]
    try:
        params = {'query': f"{p['name']}, Kenitra, Morocco", 'key': API_KEY}
        r = requests.get(PLACES_URL, params=params, timeout=5)
        data = r.json()
        
        if data['status'] == 'OK':
            loc = data['results'][0]['geometry']['location']
            p['latitude'] = loc['lat']
            p['longitude'] = loc['lng']
            p['geocoded'] = True
            success += 1
        
        if (i+1) % 50 == 0:
            print(f"{i+1}/{len(pharmacies)} - {success} successful")
            with open(f'/Users/zakaria/pharmatech-morocco/scripts/quick_checkpoint_{i+1}.json', 'w') as f:
                json.dump(pharmacies, f)
        
        time.sleep(0.15)
    except:
        pass

# Save final
with open('/Users/zakaria/pharmatech-morocco/scripts/pharmacies_quick.json', 'w') as f:
    json.dump(pharmacies, f, indent=2, ensure_ascii=False)

print(f"\nDone: {success}/{len(pharmacies)} ({success*100/len(pharmacies):.1f}%)")
