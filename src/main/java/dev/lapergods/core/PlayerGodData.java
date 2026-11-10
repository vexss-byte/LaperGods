package dev.lapergods.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Data dewa milik satu pemain: kontrak, dewa aktif, loadout skill, dan status Battle Mode.
 *
 * Semua aturan divalidasi di sini (server-authoritative). Setiap operasi yang gagal
 * tidak mengubah data sama sekali.
 *
 * Keputusan arsitektur: loadout disimpan PER DEWA, jadi berganti dewa tidak menimpa
 * pilihan skill dewa lain. Status Battle Mode sengaja tidak untuk disimpan permanen;
 * ia selalu mulai dari "mati" saat login atau server restart.
 */
public final class PlayerGodData {

	/** Maksimal skill aktif pada satu waktu. */
	public static final int MAX_EQUIPPED_SKILLS = 3;

	public enum ContractResult { GRANTED, ALREADY_OWNED, UNKNOWN_GOD }

	public enum SelectResult { OK, NOT_OWNED }

	public enum LoadoutResult { OK, UNKNOWN_GOD, NOT_OWNED, TOO_MANY, DUPLICATE, UNKNOWN_SKILL }

	public enum BattleModeResult { ENABLED, DISABLED, NO_CHANGE, NO_ACTIVE_GOD }

	private final GodRegistry registry;
	private final Set<String> ownedGods = new LinkedHashSet<>();
	private final Map<String, List<String>> equippedSkillsByGod = new HashMap<>();
	private String activeGod; // null jika belum ada dewa aktif
	private boolean battleMode;

	public PlayerGodData(GodRegistry registry) {
		this.registry = registry;
	}

	// ----- Kontrak -----

	public ContractResult grantContract(String godId) {
		if (registry.find(godId).isEmpty()) {
			return ContractResult.UNKNOWN_GOD;
		}
		if (!ownedGods.add(godId)) {
			return ContractResult.ALREADY_OWNED;
		}
		return ContractResult.GRANTED;
	}

	public Set<String> ownedGods() {
		return Collections.unmodifiableSet(ownedGods);
	}

	// ----- Dewa aktif -----

	public SelectResult selectActiveGod(String godId) {
		if (!ownedGods.contains(godId)) {
			return SelectResult.NOT_OWNED;
		}
		if (!godId.equals(activeGod)) {
			// Berganti dewa saat Battle Mode aktif harus membersihkan status dewa lama.
			battleMode = false;
			activeGod = godId;
		}
		return SelectResult.OK;
	}

	public Optional<String> activeGod() {
		return Optional.ofNullable(activeGod);
	}

	// ----- Loadout skill -----

	/** Memasang daftar skill aktif untuk satu dewa. Hanya berhasil jika semua aturan terpenuhi. */
	public LoadoutResult equipSkills(String godId, List<String> skillIds) {
		Optional<GodDefinition> god = registry.find(godId);
		if (god.isEmpty()) {
			return LoadoutResult.UNKNOWN_GOD;
		}
		if (!ownedGods.contains(godId)) {
			return LoadoutResult.NOT_OWNED;
		}
		if (skillIds.size() > MAX_EQUIPPED_SKILLS) {
			return LoadoutResult.TOO_MANY;
		}
		Set<String> seen = new HashSet<>();
		for (String skillId : skillIds) {
			if (!seen.add(skillId)) {
				return LoadoutResult.DUPLICATE;
			}
			if (god.get().findSkill(skillId).isEmpty()) {
				return LoadoutResult.UNKNOWN_SKILL;
			}
		}
		equippedSkillsByGod.put(godId, new ArrayList<>(skillIds));
		return LoadoutResult.OK;
	}

	public List<String> equippedSkills(String godId) {
		return List.copyOf(equippedSkillsByGod.getOrDefault(godId, List.of()));
	}

	// ----- Battle Mode -----

	public BattleModeResult setBattleMode(boolean enabled) {
		if (enabled && activeGod == null) {
			return BattleModeResult.NO_ACTIVE_GOD;
		}
		if (battleMode == enabled) {
			return BattleModeResult.NO_CHANGE;
		}
		battleMode = enabled;
		return enabled ? BattleModeResult.ENABLED : BattleModeResult.DISABLED;
	}

	public boolean isBattleModeActive() {
		return battleMode;
	}

	/**
	 * Membersihkan status sementara. Dipanggil saat pemain mati, disconnect,
	 * pindah dimensi, atau server restart, agar tidak ada efek combat yang tertinggal.
	 */
	public void resetTransientState() {
		battleMode = false;
	}
}
