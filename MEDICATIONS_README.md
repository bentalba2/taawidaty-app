# Moroccan Pharmaceutical Database
## All Medications with CNSS & CNOPS Reimbursement Rates

**Created:** November 4, 2025  
**Author:** BENTALBA ZAKARIA  
**Source:** Taawidaty Medication Calculator Database

---

## 📊 Database Statistics

- **Total Medications:** 4,678
- **Coverage:** 100% with pricing data
- **Reimbursement Coverage:**
  - CNSS: 4,325 medications (92.5%)
  - CNOPS: 4,325 medications (92.5%)
  - Both systems: 4,325 medications (92.5%)

- **Medication Types:**
  - Princeps (Brand): 3,462 (74.0%)
  - Générique (Generic): 1,216 (26.0%)

- **Average Reimbursement Rates:**
  - CNSS: 70.0%
  - CNOPS: 70.0%

---

## 📁 File Structure

### `allmeds.json`
**Size:** 2.1 MB  
**Format:** JSON  
**Encoding:** UTF-8

Complete database of all Moroccan medications with:
- Medication names
- Active ingredients (DCI)
- Dosage and pharmaceutical form
- Public prices (PPV)
- CNSS reimbursement rates and amounts
- CNOPS reimbursement rates and amounts
- Patient out-of-pocket costs
- Brand vs Generic classification

---

## 🔍 Data Structure

Each medication entry contains:

```json
{
  "name": "Medication Name",
  "dci": "Active Ingredient (INN)",
  "dosage": "Dosage",
  "forme": "Pharmaceutical Form",
  "presentation": "Presentation/Package",
  "publicPrice": 0.00,
  "prix_br": 0.00,
  "type": "Princeps or Générique",
  "cnss": {
    "reimbursementRate": 70,
    "reimbursementAmount": 0.00,
    "patientPays": 0.00
  },
  "cnops": {
    "reimbursementRate": 70,
    "reimbursementAmount": 0.00,
    "patientPays": 0.00
  }
}
```

---

## 💡 Usage Examples

### Python
```python
import json

# Load all medications
with open('allmeds.json', 'r', encoding='utf-8') as f:
    medications = json.load(f)

# Find a specific medication
doliprane = [m for m in medications if 'DOLIPRANE' in m['name']]

# Filter by reimbursement rate
high_reimbursement = [m for m in medications 
                      if m['cnss']['reimbursementRate'] >= 100]

# Calculate total cost for a prescription
prescription = ['AMOXICILLINE', 'PARACETAMOL']
total_cnss = sum(m['cnss']['patientPays'] 
                 for m in medications 
                 if any(drug in m['name'] for drug in prescription))
```

### JavaScript/Node.js
```javascript
const fs = require('fs');

// Load all medications
const medications = JSON.parse(
  fs.readFileSync('allmeds.json', 'utf-8')
);

// Search by name
const results = medications.filter(m => 
  m.name.includes('AMOXICILLINE')
);

// Compare CNSS vs CNOPS cost
medications.forEach(med => {
  const cnssCost = med.cnss.patientPays;
  const cnopsCost = med.cnops.patientPays;
  const difference = Math.abs(cnssCost - cnopsCost);
  if (difference > 0) {
    console.log(`${med.name}: Diff ${difference} dhs`);
  }
});
```

---

## 📈 Key Insights

1. **Standardization:** Most medications have identical reimbursement rates between CNSS and CNOPS (70%)

2. **Coverage:** 92.5% of medications are eligible for reimbursement under both systems

3. **Generics:** 26% of available medications are generic alternatives, offering cost savings

4. **Pricing:** All medications have established public prices (PPV)

---

## 🔄 Data Updates

This database represents a snapshot as of **November 2025**. For the most current data:
- Visit the Taawidaty calculator at https://taawidaty.ma
- Check official ANAM (Agence Nationale de l'Assurance Maladie) sources
- Monitor updates to CNSS and CNOPS reimbursement policies

---

## 📝 License & Attribution

This data is compiled from official Moroccan pharmaceutical databases for the **Taawidaty** project.

**Project:** Taawidaty - Moroccan Medication Reimbursement Calculator  
**Repository:** github.com/bentalba/final-taawidaty  
**Website:** https://taawidaty.ma

---

## 🛠️ Technical Notes

- **Sorted:** Medications are alphabetically sorted by name
- **Encoding:** UTF-8 for Arabic and French character support
- **Validation:** All prices and rates validated against official sources
- **Format:** Standard JSON for maximum compatibility

---

## 📧 Contact

For questions, updates, or contributions:
- **Developer:** BENTALBA ZAKARIA
- **Project:** Taawidaty
- **GitHub:** github.com/bentalba

---

*Last updated: November 4, 2025*
