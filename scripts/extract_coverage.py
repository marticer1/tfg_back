#!/usr/bin/env python3
import os
import xml.etree.ElementTree as ET
import sys

xml_path = os.environ.get("JACOCO_XML", "target/site/jacoco/jacoco.xml")
if not os.path.exists(xml_path):
    # Manejo cuando no existe
    metrics = {m: 0 for m in ["instruction", "line", "method", "class", "branch"]}
else:
    root = ET.parse(xml_path).getroot()
    metrics = {}
    def get_pct(type_name):
        totals = [c for c in root.findall("counter") if c.get("type") == type_name]
        if totals:
            c = totals[-1]
            missed = int(c.get("missed", "0"))
            covered = int(c.get("covered", "0"))
        else:
            missed = covered = 0
        total = missed + covered
        return (covered * 100.0 / (missed + covered)) if (missed + covered) > 0 else 0

    for type_name, out_name in [
        ("INSTRUCTION", "instruction"),
        ("LINE", "line"),
        ("METHOD", "method"),
        ("CLASS", "class"),
        ("BRANCH", "branch"),
    ]:
        metrics[out_name] = round(get_pct(type_name), 2)

# Imprimir para GitHub Actions
for k, v in metrics.items():
    print(f"{k}={v}")