package dev.lapergods.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Pelacak cooldown berbasis tick server. Satu instance per pemain.
 * Waktu "sekarang" selalu diberikan dari server; client tidak pernah menentukannya.
 */
public final class CooldownTracker {

	private final Map<String, Long> readyAtTick = new HashMap<>();

	/** Kunci cooldown unik per dewa dan skill (id skill hanya unik di dalam satu dewa). */
	public static String key(String godId, String skillId) {
		return godId + ":" + skillId;
	}

	public boolean isReady(String key, long nowTick) {
		return remainingTicks(key, nowTick) == 0;
	}

	public long remainingTicks(String key, long nowTick) {
		Long readyAt = readyAtTick.get(key);
		if (readyAt == null || readyAt <= nowTick) {
			return 0;
		}
		return readyAt - nowTick;
	}

	public void start(String key, long nowTick, int cooldownTicks) {
		readyAtTick.put(key, nowTick + cooldownTicks);
	}

	public void clear() {
		readyAtTick.clear();
	}
}
