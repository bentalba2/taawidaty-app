#!/usr/bin/env python3
"""
Auto-completion handler - monitors scraper and performs integration
"""
import time
import subprocess
import os
import sys

def is_scraper_running():
    result = subprocess.run(['pgrep', '-f', 'pharmacy_scraper.py'], capture_output=True)
    return result.returncode == 0

def get_progress():
    log_file = "/Users/zakaria/pharmatech-morocco/scripts/scraper_output.log"
    if os.path.exists(log_file):
        with open(log_file, 'r') as f:
            lines = f.readlines()
        for line in reversed(lines[-50:]):
            if "Geocoding" in line and "/" in line:
                return line.strip()
            elif "Successfully geocoded" in line:
                return "COMPLETED: " + line.strip()
    return "Unknown"

def check_output_files():
    files = {
        'pharmacies_kenitra.json': '/Users/zakaria/pharmatech-morocco/scripts/pharmacies_kenitra.json',
        'KenitraPharmacyData.kt': '/Users/zakaria/pharmatech-morocco/scripts/KenitraPharmacyData.kt'
    }
    results = {}
    for name, path in files.items():
        results[name] = os.path.exists(path)
    return results

def integrate_kotlin_file():
    """Copy Kotlin file to app"""
    source = "/Users/zakaria/pharmatech-morocco/scripts/KenitraPharmacyData.kt"
    dest_dir = "/Users/zakaria/pharmatech-morocco/app/src/main/java/com/pharmatech/morocco/features/pharmacy/domain/model/"
    dest = os.path.join(dest_dir, "KenitraPharmacyData.kt")
    
    if os.path.exists(source):
        os.makedirs(dest_dir, exist_ok=True)
        subprocess.run(['cp', source, dest], check=True)
        print(f"✓ Copied Kotlin file to: {dest}")
        return True
    return False

def show_statistics():
    """Show final statistics"""
    import json
    json_file = "/Users/zakaria/pharmatech-morocco/scripts/pharmacies_kenitra.json"
    if os.path.exists(json_file):
        with open(json_file, 'r') as f:
            data = json.load(f)
        
        total = len(data)
        geocoded = sum(1 for p in data if p['geocoded'])
        with_phone = sum(1 for p in data if p['phone_number'])
        
        print("\n" + "="*60)
        print("PHARMACY DATA SCRAPING - COMPLETED!")
        print("="*60)
        print(f"Total Pharmacies: {total}")
        print(f"Successfully Geocoded: {geocoded} ({geocoded*100/total:.1f}%)")
        print(f"With Phone Numbers: {with_phone} ({with_phone*100/total:.1f}%)")
        print(f"Failed Geocoding: {total - geocoded}")
        print("="*60)
        
        # Show sample
        print("\nSample Pharmacy Data:")
        for i, pharmacy in enumerate(data[:3], 1):
            print(f"\n{i}. {pharmacy['name']}")
            print(f"   Phone: {pharmacy['phone_number']}")
            print(f"   Location: {pharmacy['latitude']:.6f}, {pharmacy['longitude']:.6f}")
            print(f"   Geocoded: {'✓' if pharmacy['geocoded'] else '✗'}")
        
        return True
    return False

print("Monitoring scraper completion...")
print("="*60)

# Wait for scraper to finish
last_progress = ""
while is_scraper_running():
    progress = get_progress()
    if progress != last_progress:
        print(f"[{time.strftime('%H:%M:%S')}] {progress}")
        last_progress = progress
    time.sleep(10)

print("\n✓ Scraper finished!")
print("\nChecking output files...")

# Wait a moment for files to be written
time.sleep(3)

# Check files
files = check_output_files()
for name, exists in files.items():
    status = "✓" if exists else "✗"
    print(f"{status} {name}: {'Found' if exists else 'Missing'}")

if not all(files.values()):
    print("\n⚠ Some files are missing. Check scraper_output.log for errors.")
    sys.exit(1)

# Show statistics
print("\nGenerating statistics...")
if show_statistics():
    print("\n✓ Statistics generated")

# Integrate Kotlin file
print("\nIntegrating Kotlin file into app...")
if integrate_kotlin_file():
    print("✓ Integration complete!")
else:
    print("✗ Integration failed - Kotlin file not found")
    sys.exit(1)

print("\n" + "="*60)
print("ALL DONE! Ready to build and test your app.")
print("="*60)
print("\nNext steps:")
print("1. Update PharmacyMapScreen.kt to use KenitraPharmacyData")
print("2. Build the app: ./gradlew assembleDebug")
print("3. Test on emulator")
print("\nSee PHARMACY_INTEGRATION.md for detailed instructions.")
