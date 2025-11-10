"""
Pharmacy Data Scraper for TAAWIDATY
Scrapes pharmacy data from annuaire-gratuit.ma and geocodes using Google Geocoding API

Usage:
    python pharmacy_scraper.py

Requirements:
    pip install requests beautifulsoup4 lxml
"""

import requests
from bs4 import BeautifulSoup
import json
import time
import re
from typing import List, Dict, Optional
from dataclasses import dataclass, asdict
from datetime import datetime

@dataclass
class PharmacyRaw:
    name: str
    address: str
    city: str
    phone: str
    page_number: int
    source_url: str

@dataclass
class Pharmacy:
    id: str
    name: str
    name_ar: Optional[str]
    address: str
    address_ar: Optional[str]
    city: str
    latitude: float
    longitude: float
    phone_number: str
    email: Optional[str]
    website: Optional[str]
    opening_hours: str
    is_24_hours: bool
    has_parking: bool
    is_guard_pharmacy: bool
    rating: float
    review_count: int
    image_url: Optional[str]
    services: List[str]
    distance: Optional[float]
    last_updated: str
    geocoded: bool
    geocode_status: str

class PharmacyScraper:
    def __init__(self, google_api_key: str):
        self.api_key = google_api_key
        self.base_url = "https://www.annuaire-gratuit.ma/recherche/pharmacies-ville-kénitra-pg"
        self.geocoding_url = "https://maps.googleapis.com/maps/api/geocode/json"
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
        })
        
    def scrape_page(self, page_number: int) -> List[PharmacyRaw]:
        """Scrape a single page for pharmacy data"""
        url = f"{self.base_url}{page_number}"
        print(f"Scraping page {page_number}: {url}")
        
        try:
            response = self.session.get(url, timeout=10)
            response.raise_for_status()
            soup = BeautifulSoup(response.content, 'html.parser')
            
            pharmacies = []
            
            # Find all listing items with class ag_listing_item
            listings = soup.find_all('li', class_='ag_listing_item')
            
            print(f"Found {len(listings)} listings on page {page_number}")
            
            for listing in listings:
                try:
                    # Get the link to check if it's a pharmacy
                    link = listing.find('a', href=True)
                    if not link or '/pharmacies/' not in link['href']:
                        continue  # Skip non-pharmacy entries
                    
                    # Extract pharmacy name from h3 with itemprop="name"
                    name_elem = listing.find('h3', itemprop='name')
                    if not name_elem:
                        continue
                    name = name_elem.get_text(strip=True)
                    
                    # Extract city from span with itemprop="addressRegion"
                    city_elem = listing.find('span', itemprop='addressRegion')
                    city = city_elem.get_text(strip=True) if city_elem else "Kénitra"
                    city = city.replace('\n', '').replace('\r', '').strip()
                    
                    # Extract phone from span with itemprop="telephone"
                    phone_elem = listing.find('span', itemprop='telephone')
                    phone = ""
                    if phone_elem:
                        phone_text = phone_elem.get_text(strip=True)
                        # Clean phone number
                        phone = re.sub(r'[^\d]', '', phone_text)
                        # Format as 0X XX XX XX XX
                        if len(phone) == 10:
                            phone = f"{phone[:2]} {phone[2:4]} {phone[4:6]} {phone[6:8]} {phone[8:10]}"
                    
                    # Use pharmacy name and city as address for geocoding
                    address = f"{name}, {city}, Morocco"
                    
                    pharmacy = PharmacyRaw(
                        name=name,
                        address=address,
                        city=city,
                        phone=phone,
                        page_number=page_number,
                        source_url=url
                    )
                    pharmacies.append(pharmacy)
                    print(f"  ✓ {name} - {phone}")
                    
                except Exception as e:
                    print(f"  ✗ Error extracting pharmacy: {e}")
                    continue
            
            return pharmacies
            
        except Exception as e:
            print(f"Error scraping page {page_number}: {e}")
            return []
    
    def scrape_all_pages(self, start_page: int = 1, end_page: int = 33) -> List[PharmacyRaw]:
        """Scrape all pages"""
        all_pharmacies = []
        
        for page in range(start_page, end_page + 1):
            pharmacies = self.scrape_page(page)
            all_pharmacies.extend(pharmacies)
            
            print(f"Progress: {len(all_pharmacies)} pharmacies scraped so far")
            
            # Be respectful - add delay between requests
            time.sleep(1)
        
        return all_pharmacies
    
    def geocode_address(self, address: str, city: str) -> Optional[Dict]:
        """Geocode an address using Google Geocoding API"""
        params = {
            'address': f"{address}, {city}, Morocco",
            'key': self.api_key,
            'region': 'ma'  # Morocco region bias
        }
        
        try:
            response = requests.get(self.geocoding_url, params=params, timeout=10)
            response.raise_for_status()
            data = response.json()
            
            if data['status'] == 'OK' and len(data['results']) > 0:
                location = data['results'][0]['geometry']['location']
                return {
                    'latitude': location['lat'],
                    'longitude': location['lng'],
                    'formatted_address': data['results'][0]['formatted_address'],
                    'status': 'OK'
                }
            else:
                return {
                    'latitude': 0.0,
                    'longitude': 0.0,
                    'formatted_address': address,
                    'status': data['status']
                }
                
        except Exception as e:
            print(f"Geocoding error for {address}: {e}")
            return {
                'latitude': 0.0,
                'longitude': 0.0,
                'formatted_address': address,
                'status': 'ERROR'
            }
    
    def geocode_pharmacies(self, pharmacies: List[PharmacyRaw], batch_size: int = 50) -> List[Pharmacy]:
        """Geocode all pharmacies"""
        geocoded = []
        total = len(pharmacies)
        
        for i, raw_pharmacy in enumerate(pharmacies):
            print(f"Geocoding {i+1}/{total}: {raw_pharmacy.name}")
            
            # Generate unique ID
            pharmacy_id = f"pharmacy_kenitra_{i+1:04d}"
            
            # Geocode
            geo_result = self.geocode_address(raw_pharmacy.address, raw_pharmacy.city)
            
            # Create Pharmacy object
            pharmacy = Pharmacy(
                id=pharmacy_id,
                name=raw_pharmacy.name,
                name_ar=None,
                address=raw_pharmacy.address,
                address_ar=None,
                city=raw_pharmacy.city,
                latitude=geo_result['latitude'],
                longitude=geo_result['longitude'],
                phone_number=raw_pharmacy.phone,
                email=None,
                website=None,
                opening_hours="Lun-Ven: 09:00-19:00 | Sam: 09:00-13:00",
                is_24_hours=False,
                has_parking=False,
                is_guard_pharmacy=False,
                rating=0.0,
                review_count=0,
                image_url=None,
                services=[],
                distance=None,
                last_updated=datetime.now().isoformat(),
                geocoded=geo_result['latitude'] != 0.0,
                geocode_status=geo_result['status']
            )
            
            geocoded.append(pharmacy)
            
            # Progress checkpoint every batch
            if (i + 1) % batch_size == 0:
                print(f"Checkpoint: {i+1}/{total} pharmacies processed")
                self.save_checkpoint(geocoded, f"checkpoint_{i+1}.json")
            
            # Rate limiting for Google API (50 requests per second limit)
            time.sleep(0.1)  # 10 requests per second to be safe
        
        return geocoded
    
    def save_checkpoint(self, pharmacies: List[Pharmacy], filename: str):
        """Save progress checkpoint"""
        data = [asdict(p) for p in pharmacies]
        with open(filename, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
        print(f"Checkpoint saved: {filename}")
    
    def save_to_json(self, pharmacies: List[Pharmacy], filename: str = "pharmacies_kenitra.json"):
        """Save pharmacies to JSON file"""
        data = [asdict(p) for p in pharmacies]
        with open(filename, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
        print(f"Saved {len(pharmacies)} pharmacies to {filename}")
    
    def generate_kotlin_data(self, pharmacies: List[Pharmacy], filename: str = "PharmacyData.kt"):
        """Generate Kotlin data file"""
        kotlin_code = '''package com.pharmatech.morocco.features.pharmacy.domain.model

import java.util.Date
import java.text.SimpleDateFormat

/**
 * Auto-generated pharmacy data for Kénitra
 * Generated on: {date}
 * Total pharmacies: {count}
 */
object KenitraPharmacyData {{
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    
    val pharmacies = listOf(
{pharmacy_list}
    )
    
    fun getAllPharmacies(): List<Pharmacy> = pharmacies
    
    fun getPharmacyById(id: String): Pharmacy? = pharmacies.find {{ it.id == id }}
    
    fun searchPharmacies(query: String): List<Pharmacy> {{
        val lowerQuery = query.lowercase()
        return pharmacies.filter {{
            it.name.lowercase().contains(lowerQuery) ||
            it.address.lowercase().contains(lowerQuery) ||
            it.phoneNumber.contains(query)
        }}
    }}
}}
'''.format(
            date=datetime.now().isoformat(),
            count=len(pharmacies),
            pharmacy_list=self._generate_pharmacy_entries(pharmacies)
        )
        
        with open(filename, 'w', encoding='utf-8') as f:
            f.write(kotlin_code)
        print(f"Generated Kotlin data file: {filename}")
    
    def _generate_pharmacy_entries(self, pharmacies: List[Pharmacy]) -> str:
        """Generate Kotlin pharmacy entries"""
        entries = []
        
        for pharmacy in pharmacies:
            entry = f'''        Pharmacy(
            id = "{pharmacy.id}",
            name = "{self._escape_quotes(pharmacy.name)}",
            nameAr = null,
            address = "{self._escape_quotes(pharmacy.address)}",
            addressAr = null,
            city = "{pharmacy.city}",
            latitude = {pharmacy.latitude},
            longitude = {pharmacy.longitude},
            phoneNumber = "{pharmacy.phone_number}",
            email = null,
            website = null,
            openingHours = "{pharmacy.opening_hours}",
            is24Hours = {str(pharmacy.is_24_hours).lower()},
            hasParking = {str(pharmacy.has_parking).lower()},
            isGuardPharmacy = {str(pharmacy.is_guard_pharmacy).lower()},
            rating = {pharmacy.rating},
            reviewCount = {pharmacy.review_count},
            imageUrl = null,
            services = emptyList(),
            distance = null,
            lastUpdated = dateFormat.parse("{pharmacy.last_updated}") ?: Date()
        )'''
            entries.append(entry)
        
        return ',\n'.join(entries)
    
    def _escape_quotes(self, text: str) -> str:
        """Escape quotes for Kotlin string"""
        return text.replace('"', '\\"').replace('\n', ' ').strip()

def main():
    print("=" * 60)
    print("TAAWIDATY Pharmacy Data Scraper")
    print("=" * 60)
    
    # Initialize scraper with Google API key
    API_KEY = "AIzaSyAOX83Yil9oQI_oOaf86LA8gTUyRB-qtFo"
    scraper = PharmacyScraper(API_KEY)
    
    # Step 1: Scrape all pages
    print("\n[Step 1/3] Scraping pharmacy data from website...")
    raw_pharmacies = scraper.scrape_all_pages(start_page=1, end_page=33)
    print(f"✓ Scraped {len(raw_pharmacies)} pharmacies")
    
    # Save raw data
    raw_data = [asdict(p) for p in raw_pharmacies]
    with open('pharmacies_raw.json', 'w', encoding='utf-8') as f:
        json.dump(raw_data, f, indent=2, ensure_ascii=False)
    print(f"✓ Saved raw data to pharmacies_raw.json")
    
    # Step 2: Geocode pharmacies
    print(f"\n[Step 2/3] Geocoding {len(raw_pharmacies)} pharmacies...")
    print("This will use your Google Geocoding API credits")
    print(f"Estimated cost: ${len(raw_pharmacies) * 0.005:.2f}")
    print("Proceeding with geocoding...")
    
    geocoded_pharmacies = scraper.geocode_pharmacies(raw_pharmacies)
    
    # Count successful geocoding
    successful = sum(1 for p in geocoded_pharmacies if p.geocoded)
    print(f"✓ Successfully geocoded: {successful}/{len(geocoded_pharmacies)}")
    
    # Step 3: Save results
    print("\n[Step 3/3] Saving results...")
    scraper.save_to_json(geocoded_pharmacies, "pharmacies_kenitra.json")
    scraper.generate_kotlin_data(geocoded_pharmacies, "KenitraPharmacyData.kt")
    
    # Generate statistics
    print("\n" + "=" * 60)
    print("RESULTS SUMMARY")
    print("=" * 60)
    print(f"Total pharmacies scraped: {len(raw_pharmacies)}")
    print(f"Successfully geocoded: {successful}")
    print(f"Failed geocoding: {len(geocoded_pharmacies) - successful}")
    print(f"Output files:")
    print(f"  - pharmacies_raw.json (raw scraped data)")
    print(f"  - pharmacies_kenitra.json (geocoded data)")
    print(f"  - KenitraPharmacyData.kt (Kotlin data class)")
    print("=" * 60)

if __name__ == "__main__":
    main()
