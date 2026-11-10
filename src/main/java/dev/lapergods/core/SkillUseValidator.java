package dev.lapergods.core;

import java.util.Optional;

/**
 * Keputusan server: boleh atau tidaknya pemain memakai sebuah skill sekarang.
 * Client hanya mengirim "ingin memakai skill X"; semua pengecekan terjadi di sini.
 */
public final class SkillUseValidator {

	public enum UseResult {
		OK,
		NO_ACTIVE_GOD,
		NOT_IN_BATTLE_MODE,
		UNKNOWN_SKILL,
		SKILL_NOT_EQUIPPED,
		ON_COOLDOWN
	}

	private SkillUseValidator() {
	}

	/**
	 * Memeriksa dan, jika lolos, langsung memulai cooldown agar permintaan ganda
	 * pada tick yang sama tidak bisa lolos dua kali.
	 */
	public static UseResult tryUse(
			GodRegistry registry,
			PlayerGodData player,
			CooldownTracker cooldowns,
			String skillId,
			long nowTick) {

		Optional<String> activeGodId = player.activeGod();
		if (activeGodId.isEmpty()) {
			return UseResult.NO_ACTIVE_GOD;
		}
		if (!player.isBattleModeActive()) {
			return UseResult.NOT_IN_BATTLE_MODE;
		}
		String godId = activeGodId.get();
		Optional<SkillDefinition> skill = registry.find(godId).flatMap(god -> god.findSkill(skillId));
		if (skill.isEmpty()) {
			return UseResult.UNKNOWN_SKILL;
		}
		if (!player.equippedSkills(godId).contains(skillId)) {
			return UseResult.SKILL_NOT_EQUIPPED;
		}
		String cooldownKey = CooldownTracker.key(godId, skillId);
		if (!cooldowns.isReady(cooldownKey, nowTick)) {
			return UseResult.ON_COOLDOWN;
		}
		cooldowns.start(cooldownKey, nowTick, skill.get().cooldownTicks());
		return UseResult.OK;
	}
}
