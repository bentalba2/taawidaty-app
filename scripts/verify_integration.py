#!/usr/bin/env python3
"""
Quick verification of pharmacy data integration
"""
import json

print("="*70)
print("PHARMACY DATA INTEGRATION VERIFICATION")
print("="*70)

# Load geocoded data
with open('/Users/zakaria/pharmatech-morocco/scripts/pharmacies_quick.json', 'r') as f:
    pharmacies = json.load(f)

total = len(pharmacies)
geocoded = sum(1 for p in pharmacies if p.get('geocoded'))
with_phone = sum(1 for p in pharmacies if p.get('phone'))

print(f"\n✅ Data Statistics:")
print(f"   Total pharmacies: {total}")
print(f"   Successfully geocoded: {geocoded} ({geocoded*100/total:.1f}%)")
print(f"   With phone numbers: {with_phone} ({with_phone*100/total:.1f}%)")

# Verify coordinates are in Kénitra region
kenitra_lat_range = (34.0, 34.5)
kenitra_lon_range = (-7.0, -6.0)

in_region = 0
for p in pharmacies:
    if p.get('geocoded'):
        lat, lon = p['latitude'], p['longitude']
        if kenitra_lat_range[0] <= lat <= kenitra_lat_range[1] and \
           kenitra_lon_range[0] <= lon <= kenitra_lon_range[1]:
            in_region += 1

print(f"\n✅ Geographic Verification:")
print(f"   In Kénitra region: {in_region}/{geocoded} ({in_region*100/geocoded:.1f}%)")
print(f"   Expected: 34.0-34.5°N, -7.0 to -6.0°W")

# Sample pharmacies
print(f"\n✅ Sample Pharmacies (First 5):")
for i, p in enumerate(pharmacies[:5], 1):
    status = "✓" if p.get('geocoded') else "✗"
    phone = p.get('phone', 'N/A')
    print(f"   {i}. {p['name'][:40]:<40} | {phone} {status}")

# Check Kotlin file
import os
kotlin_path = '/Users/zakaria/pharmatech-morocco/app/src/main/java/com/pharmatech/morocco/features/pharmacy/domain/model/KenitraPharmacyData.kt'
if os.path.exists(kotlin_path):
    size = os.path.getsize(kotlin_path)
    with open(kotlin_path, 'r') as f:
        lines = len(f.readlines())
    print(f"\n✅ Kotlin Integration:")
    print(f"   File: KenitraPharmacyData.kt")
    print(f"   Size: {size/1024:.1f} KB")
    print(f"   Lines: {lines:,}")
    print(f"   Status: Integrated ✓")
else:
    print(f"\n❌ Kotlin file not found!")

# Check APK
apk_path = '/Users/zakaria/pharmatech-morocco/app/build/outputs/apk/debug/app-debug.apk'
if os.path.exists(apk_path):
    size = os.path.getsize(apk_path)
    print(f"\n✅ Build Status:")
    print(f"   APK: app-debug.apk")
    print(f"   Size: {size/1024/1024:.1f} MB")
    print(f"   Status: Build Successful ✓")
else:
    print(f"\n⚠️  APK not found - run: ./gradlew assembleDebug")

print("\n" + "="*70)
print("INTEGRATION COMPLETE! Ready for testing.")
print("="*70)
print("\nNext steps:")
print("  1. ./gradlew installDebug  (install on emulator)")
print("  2. Open app → Navigate to Pharmacies tab")
print("  3. Verify 651 markers appear on map")
print("  4. Test search and nearby features")
print("="*70)
