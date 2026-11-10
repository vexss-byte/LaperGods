package dev.lapergods.core;

import java.util.Objects;

/**
 * Satu modifier pasif (buff atau debuff) milik dewa.
 *
 * "attribute" adalah kunci logis (misalnya "attack_damage"). Pemetaan ke attribute
 * Minecraft dilakukan oleh adapter di fase berikutnya, jadi kelas ini bebas dari API game.
 * "condition" menentukan kapan modifier berlaku: "always", "daytime", "night", dst.
 * Semua modifier hanya aktif selama Battle Mode.
 */
public record PassiveModifier(String attribute, double amount, Operation operation, String condition) {

	public static final String ALWAYS = "always";

	public enum Operation {
		/** Menambah nilai dasar. */
		ADD,
		/** Mengalikan nilai dasar: 0.10 berarti +10%, -0.15 berarti -15%. */
		MULTIPLY_BASE
	}

	public PassiveModifier {
		Objects.requireNonNull(attribute, "attribute");
		Objects.requireNonNull(operation, "operation");
		Objects.requireNonNull(condition, "condition");
	}
}
