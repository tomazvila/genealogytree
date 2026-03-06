#!/usr/bin/env python3
"""Explore the Excel file structure to understand the genealogy data format."""

import zipfile
import xml.etree.ElementTree as ET

excel_path = "/Users/lilvilla/Programming/Geneinator/GENEALOGINIS MEDIS.xlsx"

# Extract drawing XML for detailed analysis
with zipfile.ZipFile(excel_path, 'r') as z:
    print("=== DRAWING1.XML CONTENT ===")
    content = z.read('xl/drawings/drawing1.xml').decode('utf-8')

    # Parse XML
    root = ET.fromstring(content)

    # Get all namespaces used
    print("Root tag:", root.tag)

    # Print first 5000 chars of XML for inspection
    print("\n=== RAW XML (first 8000 chars) ===")
    print(content[:8000])

    # Try different namespace patterns
    print("\n=== SEARCHING FOR TEXT ELEMENTS ===")
    # Find all elements with 't' tag (text)
    for elem in root.iter():
        if elem.text and elem.text.strip():
            tag = elem.tag.split('}')[-1] if '}' in elem.tag else elem.tag
            if tag == 't':
                print(f"  Text element: {elem.text}")
