#!/usr/bin/env python3
import requests
from bs4 import BeautifulSoup
import json
import time
import sys

sys.stdout = open(sys.stdout.fileno(), mode='w', buffering=1)
sys.stderr = open(sys.stderr.fileno(), mode='w', buffering=1)

BASE_URL = "https://www.annuaire-gratuit.ma/pharmacies/_rabat"
TOTAL_PAGES = 33

print("="*70)
print("RABAT PHARMACY SCRAPER")
print("="*70)
print(f"\nTarget: {TOTAL_PAGES} pages\n")

pharmacies = []

for page_num in range(1, TOTAL_PAGES + 1):
    url = BASE_URL if page_num == 1 else f"{BASE_URL}-pg{page_num}"
    print(f"Page {page_num}/{TOTAL_PAGES}: {url}")
    
    try:
        response = requests.get(url, timeout=10)
        soup = BeautifulSoup(response.content, 'html.parser')
        listings = soup.find_all('li', class_='ag_listing_item')
        
        count = 0
        for listing in listings:
            link = listing.find('a', href=True)
            if link and '/pharmacies/' in link['href']:
                name = listing.find('h3', itemprop='name')
                phone = listing.find('span', itemprop='telephone')
                
                if name:
                    pharmacies.append({
                        'name': name.get_text(strip=True),
                        'phone': phone.get_text(strip=True) if phone else '',
                        'address': f"{name.get_text(strip=True)}, Rabat, Morocco",
                        'city': 'Rabat',
                        'geocoded': False,
                        'latitude': 0.0,
                        'longitude': 0.0
                    })
                    count += 1
        
        print(f"  Found: {count}")
        time.sleep(1)
    except Exception as e:
        print(f"  Error: {e}")

print(f"\nTotal: {len(pharmacies)} pharmacies")
print(f"With phones: {sum(1 for p in pharmacies if p['phone'])}")

with open('/Users/zakaria/pharmatech-morocco/scripts/rabat_pharmacies_raw.json', 'w') as f:
    json.dump(pharmacies, f, indent=2, ensure_ascii=False)

print("\nSaved: rabat_pharmacies_raw.json")
print("\nSample:")
for i, p in enumerate(pharmacies[:5], 1):
    print(f"  {i}. {p['name']:<40} | {p['phone']}")
