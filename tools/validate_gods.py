#!/usr/bin/env python3
"""Memvalidasi data dewa (JSON) terhadap aturan master prompt. Tanpa dependency eksternal."""
import json
import re
import sys
from pathlib import Path

GODS_DIR = Path(__file__).resolve().parent.parent / "src/main/resources/data/lapergods/gods"
EXPECTED_GODS = {"zeus", "thor", "poseidon", "hades", "ra", "anubis"}
MIN_SKILLS, MAX_SKILLS = 6, 8
COLOR = re.compile(r"^#[0-9A-Fa-f]{6}$")
OPERATIONS = {"ADD", "MULTIPLY_BASE"}

errors = []


def fail(god, message):
    errors.append(f"{god}: {message}")


def check_modifiers(god, kind, modifiers):
    for m in modifiers:
        if m.get("operation") not in OPERATIONS:
            fail(god, f"{kind} operation tidak valid: {m}")
        if not isinstance(m.get("amount"), (int, float)):
            fail(god, f"{kind} amount bukan angka: {m}")
        if not m.get("attribute") or not m.get("condition"):
            fail(god, f"{kind} attribute/condition kosong: {m}")


def check_god(path):
    god = path.stem
    data = json.loads(path.read_text(encoding="utf-8"))
    if data.get("id") != god:
        fail(god, f"id '{data.get('id')}' tidak sama dengan nama file")
    for field in ("display_name", "title", "weapon_name"):
        if not data.get(field):
            fail(god, f"field '{field}' kosong")
    for field in ("primary_color", "secondary_color"):
        if not COLOR.match(str(data.get(field, ""))):
            fail(god, f"{field} bukan format #RRGGBB")
    check_modifiers(god, "buff", data.get("buffs", []))
    check_modifiers(god, "debuff", data.get("debuffs", []))
    if not data.get("buffs") or not data.get("debuffs"):
        fail(god, "harus punya minimal satu buff dan satu debuff (trade-off)")

    skills = data.get("skills", [])
    if not MIN_SKILLS <= len(skills) <= MAX_SKILLS:
        fail(god, f"jumlah skill {len(skills)} di luar {MIN_SKILLS}-{MAX_SKILLS}")
    ids = [s.get("id") for s in skills]
    if len(ids) != len(set(ids)):
        fail(god, "id skill ganda")
    for s in skills:
        if s.get("cooldown_ticks", 0) <= 0:
            fail(god, f"cooldown skill '{s.get('id')}' harus positif")
        if s.get("windup_ticks", -1) < 0 or s.get("damage", -1) < 0 or s.get("range", -1) < 0:
            fail(god, f"angka negatif pada skill '{s.get('id')}'")

    ult = data.get("ultimate", {})
    if not ult.get("id") or ult.get("duration_ticks", 0) <= 0 or ult.get("cooldown_ticks", 0) <= 0:
        fail(god, "ultimate tidak valid")
    return data


def main():
    files = sorted(GODS_DIR.glob("*.json"))
    found = {p.stem for p in files}
    if found != EXPECTED_GODS:
        errors.append(f"file dewa tidak sesuai. Ada: {sorted(found)}, diharapkan: {sorted(EXPECTED_GODS)}")
    loaded = [check_god(p) for p in files]

    weapons = [g["weapon_name"] for g in loaded]
    if len(weapons) != len(set(weapons)):
        errors.append("ada dua dewa dengan senjata yang sama")
    ultimates = [g["ultimate"]["id"] for g in loaded]
    if len(ultimates) != len(set(ultimates)):
        errors.append("ada dua dewa dengan id ultimate yang sama")

    if errors:
        print("VALIDASI GAGAL:")
        for e in errors:
            print(" -", e)
        sys.exit(1)
    print(f"Validasi lulus: {len(loaded)} dewa, "
          f"{sum(len(g['skills']) for g in loaded)} skill total.")


if __name__ == "__main__":
    main()
