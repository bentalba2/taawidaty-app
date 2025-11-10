#!/bin/bash
# Monitor pharmacy scraper progress

echo "==================================="
echo "PHARMACY SCRAPER PROGRESS MONITOR"
echo "==================================="
echo

# Check if process is running
if ps aux | grep -v grep | grep "pharmacy_scraper.py" > /dev/null; then
    echo "✓ Scraper is RUNNING"
else
    echo "✗ Scraper is NOT running"
fi
echo

# Show last 20 lines of output
echo "--- Last 20 lines of output ---"
tail -20 /Users/zakaria/pharmatech-morocco/scripts/scraper_output.log
echo

# Count pharmacies geocoded so far
if [ -f "/Users/zakaria/pharmatech-morocco/scripts/checkpoint_*.json" ]; then
    LATEST_CHECKPOINT=$(ls -t /Users/zakaria/pharmatech-morocco/scripts/checkpoint_*.json 2>/dev/null | head -1)
    if [ -n "$LATEST_CHECKPOINT" ]; then
        COUNT=$(python3 -c "import json; print(len(json.load(open('$LATEST_CHECKPOINT'))))" 2>/dev/null)
        echo "Progress: $COUNT pharmacies geocoded (checkpoint saved)"
    fi
fi
echo

# Check for output files
echo "--- Output Files ---"
ls -lh /Users/zakaria/pharmatech-morocco/scripts/*.json 2>/dev/null | tail -5
ls -lh /Users/zakaria/pharmatech-morocco/scripts/*.kt 2>/dev/null | tail -2
