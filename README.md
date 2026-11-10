# LaperGods

**Contract. Fight. Become a God.**
Mod aksi-combat mitologi untuk Minecraft Java 26.3 (Fabric): kontrak dengan dewa, senjata ilahi, Battle Mode, skill, aura, dan ultimate sinematik.

> **Status: Phase 0 selesai disusun (belum terbukti build), Phase 1 baru sebagian (logika inti dan data dewa).** Belum ada gameplay di dalam game. Lihat `PHASE_REPORT.md` untuk apa yang sudah dan belum terbukti.

## Target teknis
| Komponen | Versi |
|---|---|
| Minecraft | 26.3 |
| Fabric Loader | 0.20.1 |
| Fabric API | 0.165.2+26.3 |
| Fabric Loom | 1.19-SNAPSHOT (`net.fabricmc.fabric-loom`) |
| Gradle (wrapper) | 9.8.2 |
| Java | 25 |

Sumber versi: template resmi `FabricMC/fabric-example-mod` (commit `bb432ba`, 2026-11-07). Cek ulang di <https://fabricmc.net/develop> sebelum membangun, karena versi bisa berubah.
Tidak memakai AzureLib maupun Player Animation Library.

## Build
Butuh JDK 25.

```
./gradlew build --no-daemon
```

JAR runtime ada di `build/libs/lapergods-<versi>.jar` (bukan `*-sources.jar`).
Build otomatis: push ke GitHub, lalu ambil artifact `lapergods-runtime-jar` dari tab Actions (`.github/workflows/build.yml`).

## Pemeriksaan tanpa Gradle
```
python3 tools/validate_gods.py
bash tools/run_core_selfcheck.sh
```

## Instalasi
1. Pasang Minecraft 26.3 dengan Fabric Loader 0.20.1 atau lebih baru dan Java 25.
2. Taruh Fabric API (0.165.2+26.3) dan JAR LaperGods di folder `mods/`.

## Command pemberian patung (direncanakan, Phase 2)
Patung diberikan operator lewat `/give` vanilla. **Item ini belum terdaftar di Phase 0.**
```
/give @p lapergods:zeus_statue
/give @p lapergods:thor_statue
/give @p lapergods:poseidon_statue
/give @p lapergods:hades_statue
/give @p lapergods:ra_statue
/give @p lapergods:anubis_statue
```
Untuk pemain lain, ganti `@p` dengan nama pemain atau target selector.

## Keterbatasan yang diketahui
- Build belum pernah dijalankan di lingkungan penyusun (lihat `PHASE_REPORT.md`).
- Belum ada fitur gameplay, model, tekstur, atau audio. Daftar aset ada di `ASSET_STATUS.md`.
