# PHASE_REPORT — Phase 0 (Audit toolchain)

## 1. File yang dibuat
`settings.gradle`, `gradle.properties`, `build.gradle`, wrapper Gradle (`gradlew`, `gradlew.bat`, `gradle/wrapper/*`, disalin dari template resmi Fabric), `fabric.mod.json`, `dev.lapergods.LaperGods` (main), `dev.lapergods.client.LaperGodsClient` (client), `lang/en_us.json`, `.github/workflows/build.yml`, `README.md`, `ASSET_STATUS.md`.

## 2. Tujuan
Membuat project minimal yang memakai toolchain yang benar-benar dipakai template resmi Fabric untuk 26.3, sebagai dasar Phase 1.

## 3. Verifikasi yang dilakukan
- Mengkloning `FabricMC/fabric-example-mod` (commit `bb432ba`, 2026-11-07) dan membaca `gradle.properties`, `build.gradle`, `gradle-wrapper.properties`, `fabric.mod.json`, workflow, dan contoh entrypoint.
- Versi yang dipakai: Minecraft 26.3, Loader 0.20.1, Loom 1.19-SNAPSHOT, Fabric API 0.165.2+26.3, Gradle 9.8.2, Java 25.
- Dari template: tanpa blok `mappings` (runtime non-obfuscated), class identifier bernama `net.minecraft.resources.Identifier` (dipakai di `LaperGods.id()`), plugin id `net.fabricmc.fabric-loom`.
- Validasi sintaks `fabric.mod.json` dan `lang/en_us.json` (JSON valid).

## 4. Command build/test yang dijalankan
Tidak ada build yang berhasil dijalankan. Di lingkungan penyusun hanya ada Java 21, dan akses jaringan ke `maven.fabricmc.net`, `services.gradle.org`, dan server Mojang diblokir (HTTP 000), sehingga Loom tidak bisa mengunduh Minecraft 26.3.

## 5. Error yang tersisa
- **Build belum terbukti.** Phase 0 baru selesai setelah `./gradlew build --no-daemon` berhasil dengan JDK 25 (lokal atau lewat GitHub Actions) dan log-nya ditunjukkan.
- Loom `1.19-SNAPSHOT` adalah snapshot; jika gagal di-resolve, cek versi stabil terbaru di fabricmc.net/develop.
- Lisensi mod diisi `All-Rights-Reserved` sementara; perlu keputusan pemilik.

## 6. Yang benar-benar berjalan
Belum ada yang terbukti berjalan.

## 7. Placeholder / belum selesai
Semua fitur gameplay, semua aset (lihat `ASSET_STATUS.md`), dan item patung `/give`.

---

# PHASE_REPORT — Phase 1 (sebagian: logika inti dan data)

Status: **sebagian**. Hanya bagian yang bebas dari API Minecraft yang dikerjakan, karena Phase 0 belum terbukti build dan nama class 26.3 tidak boleh ditebak.

## 1. File yang dibuat
- `src/main/java/dev/lapergods/core/`: `GodDefinition`, `SkillDefinition`, `UltimateDefinition`, `PassiveModifier`, `GodRegistry`, `PlayerGodData`, `CooldownTracker`, `SkillUseValidator` (tanpa import `net.minecraft`).
- `src/main/resources/data/lapergods/gods/*.json`: data enam dewa (Zeus, Thor, Poseidon, Hades, Ra, Anubis).
- `tools/selfcheck/CoreSelfCheck.java`, `tools/run_core_selfcheck.sh`, `tools/validate_gods.py`.
- `.github/workflows/build.yml`: ditambah langkah validasi data dan self-check sebelum build.

## 2. Tujuan
Menetapkan aturan gameplay yang diputuskan server (kontrak, maksimal tiga skill aktif, Battle Mode, cooldown) dan data dewa yang data-driven, sebelum dihubungkan ke Minecraft.

## 3. Command yang dijalankan
- `bash tools/run_core_selfcheck.sh` (JDK 21 di sandbox).
- `python3 tools/validate_gods.py`.
- Uji mutasi: mengubah batas skill dari 3 ke 4 membuat self-check gagal (2 pemeriksaan), lalu dikembalikan.

## 4. Hasil aktual
- Self-check: 35 lulus, 0 gagal.
- Validator data: 6 dewa, 36 skill, lulus.
- Gradle build: **belum dijalankan** (lihat Phase 0).

## 5. Keputusan desain
- Loadout disimpan per dewa. Battle Mode tidak disimpan permanen. Berganti dewa mematikan Battle Mode.
- Skill dicek berurutan: dewa aktif, Battle Mode, skill dikenal, skill terpasang, cooldown. Cooldown langsung dimulai saat lolos, jadi permintaan ganda ditolak.
- Anubis memakai Judgment Staff (satu senjata utama, sesuai concept sheet). Ra memakai Solar Khopesh.

## 6. Belum selesai
- Pemuat JSON ke `GodRegistry` (butuh Gson atau Codec; dikerjakan setelah build terbukti).
- Registry item/entity, config, networking, penyimpanan data pemain, dan adapter modifier ke attribute Minecraft.
- Semua angka balance dan passive pada JSON berstatus `untuned`.
- Self-check dijalankan di JDK 21; langkah CI akan menjalankannya di JDK 25 untuk pertama kali.
- Skill bernama sama antar dewa (misalnya Storm Dash) harus dibedakan secara mekanik di Phase 6.
