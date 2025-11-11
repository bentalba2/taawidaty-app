"""
HTML Inspector - Analyze website structure before full scraping
"""

import requests
from bs4 import BeautifulSoup

def inspect_page(url: str):
    """Inspect a single page to understand HTML structure"""
    print(f"Inspecting: {url}\n")
    
    headers = {
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
    }
    
    try:
        response = requests.get(url, headers=headers, timeout=10)
        response.raise_for_status()
        soup = BeautifulSoup(response.content, 'html.parser')
        
        # Save HTML for manual inspection
        with open('page_sample.html', 'w', encoding='utf-8') as f:
            f.write(soup.prettify())
        print("✓ HTML saved to page_sample.html\n")
        
        # Find all div classes
        print("=== DIV CLASSES FOUND ===")
        divs = soup.find_all('div', class_=True)
        classes = set()
        for div in divs:
            for cls in div.get('class', []):
                classes.add(cls)
        for cls in sorted(classes)[:30]:  # Show first 30
            print(f"  - {cls}")
        
        # Find headings
        print("\n=== HEADINGS (H1-H4) ===")
        for tag in ['h1', 'h2', 'h3', 'h4']:
            elements = soup.find_all(tag)
            if elements:
                print(f"{tag.upper()}: {len(elements)} found")
                for elem in elements[:3]:  # Show first 3
                    print(f"  - {elem.get_text(strip=True)[:60]}")
        
        # Find links with "pharmacie"
        print("\n=== PHARMACY LINKS ===")
        links = soup.find_all('a')
        pharmacy_links = [a for a in links if 'pharmacie' in a.get_text().lower()]
        print(f"Found {len(pharmacy_links)} pharmacy links")
        for link in pharmacy_links[:5]:
            print(f"  - {link.get_text(strip=True)[:60]}")
        
        # Find phone numbers
        print("\n=== PHONE PATTERNS ===")
        import re
        text = soup.get_text()
        phones = re.findall(r'0\d{1}[\s\-]?\d{2}[\s\-]?\d{2}[\s\-]?\d{2}[\s\-]?\d{2}', text)
        print(f"Found {len(phones)} phone numbers")
        for phone in phones[:5]:
            print(f"  - {phone}")
        
        # Find potential pharmacy containers
        print("\n=== POTENTIAL CONTAINERS ===")
        # Try common class patterns
        patterns = ['result', 'item', 'listing', 'card', 'entry', 'pharmacy', 'pharmacie']
        for pattern in patterns:
            elements = soup.find_all(class_=lambda x: x and pattern in x.lower())
            if elements:
                print(f"'{pattern}' pattern: {len(elements)} elements")
        
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    url = "https://www.annuaire-gratuit.ma/recherche/pharmacies-ville-kénitra-pg1"
    inspect_page(url)
