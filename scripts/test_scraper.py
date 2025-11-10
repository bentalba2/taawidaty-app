"""
Test scraper on first 3 pages before full run
"""

import sys
sys.path.append('/Users/zakaria/pharmatech-morocco/scripts')

from pharmacy_scraper import PharmacyScraper

# Test with API key
API_KEY = "AIzaSyAOX83Yil9oQI_oOaf86LA8gTUyRB-qtFo"
scraper = PharmacyScraper(API_KEY)

print("=" * 60)
print("TESTING SCRAPER ON FIRST 3 PAGES")
print("=" * 60)

# Test scraping 3 pages
raw_pharmacies = scraper.scrape_all_pages(start_page=1, end_page=3)

print("\n" + "=" * 60)
print(f"RESULTS: {len(raw_pharmacies)} pharmacies scraped")
print("=" * 60)

# Show sample data
if raw_pharmacies:
    print("\nSample pharmacies:")
    for i, pharmacy in enumerate(raw_pharmacies[:10], 1):
        print(f"{i}. {pharmacy.name}")
        print(f"   Phone: {pharmacy.phone}")
        print(f"   Address: {pharmacy.address}")
        print()

print(f"\nReady to geocode {len(raw_pharmacies)} pharmacies")
print("Estimated API cost: $", len(raw_pharmacies) * 0.005)
