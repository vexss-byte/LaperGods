import dev.lapergods.core.CooldownTracker;
import dev.lapergods.core.GodDefinition;
import dev.lapergods.core.GodRegistry;
import dev.lapergods.core.PassiveModifier;
import dev.lapergods.core.PlayerGodData;
import dev.lapergods.core.PlayerGodData.BattleModeResult;
import dev.lapergods.core.PlayerGodData.ContractResult;
import dev.lapergods.core.PlayerGodData.LoadoutResult;
import dev.lapergods.core.PlayerGodData.SelectResult;
import dev.lapergods.core.SkillDefinition;
import dev.lapergods.core.SkillUseValidator;
import dev.lapergods.core.SkillUseValidator.UseResult;
import dev.lapergods.core.UltimateDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * Pemeriksaan logika inti tanpa JUnit dan tanpa Minecraft. Dijalankan lewat
 * tools/run_core_selfcheck.sh. Keluar dengan kode 1 jika ada pemeriksaan yang gagal.
 */
public final class CoreSelfCheck {

	private static int passed = 0;
	private static int failed = 0;

	public static void main(String[] args) {
		checkRegistryRules();
		checkContracts();
		checkLoadoutRules();
		checkBattleMode();
		checkSkillUse();
		checkPlayersAreIndependent();

		System.out.println("Lulus: " + passed + ", gagal: " + failed);
		if (failed > 0) {
			System.exit(1);
		}
	}

	// ----- Pembantu -----

	private static void check(boolean condition, String description) {
		if (condition) {
			passed++;
		} else {
			failed++;
			System.out.println("GAGAL: " + description);
		}
	}

	private static GodDefinition makeGod(String id, int skillCount) {
		List<SkillDefinition> skills = new ArrayList<>();
		for (int i = 1; i <= skillCount; i++) {
			skills.add(new SkillDefinition("skill_" + i, "Skill " + i, 5, 100 * i, 5.0, 10.0));
		}
		return new GodDefinition(
				id, id, "Gelar " + id, "Senjata " + id, 0x112233, 0x445566,
				List.of(new PassiveModifier("attack_damage", 0.1, PassiveModifier.Operation.MULTIPLY_BASE, "always")),
				List.of(),
				skills,
				new UltimateDefinition(id + "_ultimate", "Ultimate", 200, 3600, 40.0));
	}

	private static GodRegistry makeRegistry() {
		GodRegistry registry = new GodRegistry();
		registry.register(makeGod("zeus", 6));
		registry.register(makeGod("thor", 6));
		return registry;
	}

	private static boolean registrationRejected(GodRegistry registry, GodDefinition god) {
		try {
			registry.register(god);
			return false;
		} catch (IllegalArgumentException expected) {
			return true;
		}
	}

	// ----- Pemeriksaan -----

	private static void checkRegistryRules() {
		GodRegistry registry = new GodRegistry();
		check(registrationRejected(registry, makeGod("terlalu_sedikit", 5)), "5 skill ditolak");
		check(registrationRejected(registry, makeGod("terlalu_banyak", 9)), "9 skill ditolak");

		registry.register(makeGod("delapan", 8));
		check(registry.find("delapan").isPresent(), "8 skill diterima");
		check(registrationRejected(registry, makeGod("delapan", 6)), "id dewa ganda ditolak");

		SkillDefinition same = new SkillDefinition("sama", "Sama", 5, 100, 5.0, 10.0);
		GodDefinition duplicateSkills = new GodDefinition(
				"ganda", "Ganda", "Gelar", "Senjata", 0, 0, List.of(), List.of(),
				List.of(same, same, same, same, same, same),
				new UltimateDefinition("u", "U", 200, 3600, 40.0));
		check(registrationRejected(registry, duplicateSkills), "id skill ganda dalam satu dewa ditolak");
	}

	private static void checkContracts() {
		PlayerGodData player = new PlayerGodData(makeRegistry());
		check(player.grantContract("zeus") == ContractResult.GRANTED, "kontrak baru diberikan");
		check(player.grantContract("zeus") == ContractResult.ALREADY_OWNED, "kontrak ganda terdeteksi");
		check(player.grantContract("dewa_palsu") == ContractResult.UNKNOWN_GOD, "dewa tak dikenal ditolak");
		check(player.ownedGods().size() == 1, "kontrak tidak terduplikasi");
		check(player.selectActiveGod("thor") == SelectResult.NOT_OWNED, "dewa tanpa kontrak tidak bisa dipilih");
		check(player.selectActiveGod("zeus") == SelectResult.OK, "dewa berkontrak bisa dipilih");
	}

	private static void checkLoadoutRules() {
		PlayerGodData player = new PlayerGodData(makeRegistry());
		check(player.equipSkills("zeus", List.of("skill_1")) == LoadoutResult.NOT_OWNED,
				"loadout tanpa kontrak ditolak");

		player.grantContract("zeus");
		check(player.equipSkills("zeus", List.of("skill_1", "skill_2", "skill_3")) == LoadoutResult.OK,
				"tiga skill diterima");
		check(player.equipSkills("zeus", List.of("skill_1", "skill_2", "skill_3", "skill_4")) == LoadoutResult.TOO_MANY,
				"empat skill ditolak");
		check(player.equipSkills("zeus", List.of("skill_1", "skill_1")) == LoadoutResult.DUPLICATE,
				"skill ganda ditolak");
		check(player.equipSkills("zeus", List.of("skill_99")) == LoadoutResult.UNKNOWN_SKILL,
				"skill tak dikenal ditolak");
		check(player.equipSkills("dewa_palsu", List.of()) == LoadoutResult.UNKNOWN_GOD,
				"dewa tak dikenal ditolak pada loadout");
		check(player.equippedSkills("zeus").equals(List.of("skill_1", "skill_2", "skill_3")),
				"loadout lama tidak berubah setelah percobaan gagal");
	}

	private static void checkBattleMode() {
		PlayerGodData player = new PlayerGodData(makeRegistry());
		check(player.setBattleMode(true) == BattleModeResult.NO_ACTIVE_GOD, "Battle Mode butuh dewa aktif");

		player.grantContract("zeus");
		player.grantContract("thor");
		player.selectActiveGod("zeus");
		check(player.setBattleMode(true) == BattleModeResult.ENABLED, "Battle Mode menyala");
		check(player.setBattleMode(true) == BattleModeResult.NO_CHANGE, "menyalakan dua kali tidak berubah");

		player.selectActiveGod("thor");
		check(!player.isBattleModeActive(), "berganti dewa mematikan Battle Mode");

		player.setBattleMode(true);
		player.resetTransientState();
		check(!player.isBattleModeActive(), "reset (mati/disconnect/dimensi) mematikan Battle Mode");
		check(player.setBattleMode(false) == BattleModeResult.NO_CHANGE, "mematikan saat sudah mati tidak berubah");
	}

	private static void checkSkillUse() {
		GodRegistry registry = makeRegistry();
		PlayerGodData player = new PlayerGodData(registry);
		CooldownTracker cooldowns = new CooldownTracker();

		check(SkillUseValidator.tryUse(registry, player, cooldowns, "skill_1", 0) == UseResult.NO_ACTIVE_GOD,
				"tanpa dewa aktif ditolak");

		player.grantContract("zeus");
		player.selectActiveGod("zeus");
		player.equipSkills("zeus", List.of("skill_1", "skill_2"));
		check(SkillUseValidator.tryUse(registry, player, cooldowns, "skill_1", 0) == UseResult.NOT_IN_BATTLE_MODE,
				"di luar Battle Mode ditolak");

		player.setBattleMode(true);
		check(SkillUseValidator.tryUse(registry, player, cooldowns, "skill_99", 0) == UseResult.UNKNOWN_SKILL,
				"skill tak dikenal ditolak");
		check(SkillUseValidator.tryUse(registry, player, cooldowns, "skill_3", 0) == UseResult.SKILL_NOT_EQUIPPED,
				"skill belum dipasang ditolak");

		// skill_1 punya cooldown 100 tick pada makeGod().
		check(SkillUseValidator.tryUse(registry, player, cooldowns, "skill_1", 1000) == UseResult.OK,
				"skill terpasang berhasil dipakai");
		check(SkillUseValidator.tryUse(registry, player, cooldowns, "skill_1", 1000) == UseResult.ON_COOLDOWN,
				"permintaan ganda pada tick yang sama ditolak");
		check(SkillUseValidator.tryUse(registry, player, cooldowns, "skill_1", 1099) == UseResult.ON_COOLDOWN,
				"satu tick sebelum cooldown selesai masih ditolak");
		check(SkillUseValidator.tryUse(registry, player, cooldowns, "skill_1", 1100) == UseResult.OK,
				"tepat saat cooldown selesai berhasil");
		check(SkillUseValidator.tryUse(registry, player, cooldowns, "skill_2", 1100) == UseResult.OK,
				"cooldown skill lain tidak ikut terkunci");
		check(cooldowns.remainingTicks(CooldownTracker.key("zeus", "skill_1"), 1150) == 50,
				"sisa cooldown dihitung benar");
	}

	private static void checkPlayersAreIndependent() {
		GodRegistry registry = makeRegistry();
		PlayerGodData first = new PlayerGodData(registry);
		PlayerGodData second = new PlayerGodData(registry);
		first.grantContract("zeus");
		check(second.ownedGods().isEmpty(), "kontrak satu pemain tidak bocor ke pemain lain");
	}
}
