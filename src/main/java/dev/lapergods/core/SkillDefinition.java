package dev.lapergods.core;

import java.util.Objects;

/**
 * Definisi satu skill. Semua angka balance berasal dari data (JSON), bukan hardcode di logika.
 * Waktu dinyatakan dalam tick (20 tick = 1 detik).
 */
public record SkillDefinition(
		String id,
		String displayName,
		int windupTicks,
		int cooldownTicks,
		double damage,
		double range) {

	public SkillDefinition {
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(displayName, "displayName");
		if (windupTicks < 0) {
			throw new IllegalArgumentException("windupTicks tidak boleh negatif: " + id);
		}
		if (cooldownTicks <= 0) {
			throw new IllegalArgumentException("cooldownTicks harus positif: " + id);
		}
		if (damage < 0 || range < 0) {
			throw new IllegalArgumentException("damage dan range tidak boleh negatif: " + id);
		}
	}
}
