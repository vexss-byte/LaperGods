#!/usr/bin/env bash
# Mengompilasi dan menjalankan pemeriksaan logika inti (tanpa Minecraft, tanpa Gradle).
set -euo pipefail
cd "$(dirname "$0")/.."
OUT="$(mktemp -d)"
javac -d "$OUT" src/main/java/dev/lapergods/core/*.java tools/selfcheck/CoreSelfCheck.java
java -cp "$OUT" CoreSelfCheck
