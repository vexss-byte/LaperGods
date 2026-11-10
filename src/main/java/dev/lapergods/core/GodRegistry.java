package dev.lapergods.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Daftar semua dewa yang dikenal mod. Menolak definisi yang tidak valid saat didaftarkan. */
public final class GodRegistry {

	/** Setiap dewa harus punya 6 sampai 8 skill yang bisa dipilih. */
	public static final int MIN_SKILLS_PER_GOD = 6;
	public static final int MAX_SKILLS_PER_GOD = 8;

	private final Map<String, GodDefinition> gods = new LinkedHashMap<>();

	public void register(GodDefinition god) {
		if (gods.containsKey(god.id())) {
			throw new IllegalArgumentException("Dewa sudah terdaftar: " + god.id());
		}
		validate(god);
		gods.put(god.id(), god);
	}

	public Optional<GodDefinition> find(String godId) {
		return Optional.ofNullable(gods.get(godId));
	}

	public Collection<GodDefinition> all() {
		return Collections.unmodifiableCollection(gods.values());
	}

	private static void validate(GodDefinition god) {
		int skillCount = god.skills().size();
		if (skillCount < MIN_SKILLS_PER_GOD || skillCount > MAX_SKILLS_PER_GOD) {
			throw new IllegalArgumentException(
					god.id() + " punya " + skillCount + " skill; harus "
							+ MIN_SKILLS_PER_GOD + " sampai " + MAX_SKILLS_PER_GOD);
		}
		Set<String> seen = new HashSet<>();
		for (SkillDefinition skill : god.skills()) {
			if (!seen.add(skill.id())) {
				throw new IllegalArgumentException(
						god.id() + " punya id skill ganda: " + skill.id());
			}
		}
	}
}
