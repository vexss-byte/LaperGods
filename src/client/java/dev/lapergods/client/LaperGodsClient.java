package dev.lapergods.client;

import net.fabricmc.api.ClientModInitializer;

import dev.lapergods.LaperGods;

/**
 * Entrypoint khusus client. Renderer, HUD, UI, kamera, dan VFX didaftarkan di sini
 * pada fase berikutnya.
 */
public class LaperGodsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		LaperGods.LOGGER.info("LaperGods client Phase 0 dimuat.");
	}
}
