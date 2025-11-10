#!/usr/bin/env python3
"""
Watch scraper progress in real-time
"""
import time
import subprocess
import os

def check_progress():
    log_file = "/Users/zakaria/pharmatech-morocco/scripts/scraper_output.log"
    
    if os.path.exists(log_file):
        with open(log_file, 'r') as f:
            lines = f.readlines()
            
        # Find last progress line
        for line in reversed(lines[-50:]):
            if "Progress:" in line or "Geocoding" in line or "Successfully geocoded" in line:
                print(f"[{time.strftime('%H:%M:%S')}] {line.strip()}")
                break
    
    # Check if process still running
    result = subprocess.run(['pgrep', '-f', 'pharmacy_scraper.py'], capture_output=True)
    if result.returncode != 0:
        print(f"[{time.strftime('%H:%M:%S')}] Scraper finished!")
        return False
    return True

if __name__ == "__main__":
    print("Monitoring scraper progress... (Ctrl+C to stop)")
    print("=" * 60)
    
    try:
        while check_progress():
            time.sleep(30)  # Check every 30 seconds
    except KeyboardInterrupt:
        print("\nMonitoring stopped.")
