package dev.lapergods.core;

import java.util.Objects;

/** Definisi ultimate sebuah dewa. Waktu dalam tick (20 tick = 1 detik). */
public record UltimateDefinition(
		String id,
		String displayName,
		int durationTicks,
		int cooldownTicks,
		double damage) {

	public UltimateDefinition {
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(displayName, "displayName");
		if (durationTicks <= 0 || cooldownTicks <= 0) {
			throw new IllegalArgumentException("durationTicks dan cooldownTicks harus positif: " + id);
		}
		if (damage < 0) {
			throw new IllegalArgumentException("damage tidak boleh negatif: " + id);
		}
	}
}
