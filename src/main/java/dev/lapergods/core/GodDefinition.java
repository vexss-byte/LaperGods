package dev.lapergods.core;

import java.util.List;
import java.util.Objects;

/**
 * Definisi lengkap seorang dewa. Dibuat dari data, sehingga dewa baru bisa ditambahkan
 * tanpa mengubah sistem inti.
 *
 * Warna disimpan sebagai angka RGB (0xRRGGBB).
 */
public record GodDefinition(
		String id,
		String displayName,
		String title,
		String weaponName,
		int primaryColor,
		int secondaryColor,
		List<PassiveModifier> buffs,
		List<PassiveModifier> debuffs,
		List<SkillDefinition> skills,
		UltimateDefinition ultimate) {

	public GodDefinition {
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(displayName, "displayName");
		Objects.requireNonNull(title, "title");
		Objects.requireNonNull(weaponName, "weaponName");
		Objects.requireNonNull(ultimate, "ultimate");
		// Salinan tak-bisa-diubah agar definisi aman dibagikan antar sistem.
		buffs = List.copyOf(buffs);
		debuffs = List.copyOf(debuffs);
		skills = List.copyOf(skills);
	}

	/** Mencari skill berdasarkan id lokal (id hanya unik di dalam satu dewa). */
	public java.util.Optional<SkillDefinition> findSkill(String skillId) {
		for (SkillDefinition skill : skills) {
			if (skill.id().equals(skillId)) {
				return java.util.Optional.of(skill);
			}
		}
		return java.util.Optional.empty();
	}
}
