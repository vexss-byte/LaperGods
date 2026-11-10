package dev.lapergods;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entrypoint umum (server + client) LaperGods.
 *
 * Phase 0: hanya membuktikan bahwa proyek dapat dibangun dan dimuat oleh Fabric.
 * Registry, data dewa, kontrak, dan sistem lain ditambahkan mulai Phase 1.
 */
public class LaperGods implements ModInitializer {
	public static final String MOD_ID = "lapergods";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("LaperGods Phase 0 dimuat. Contract. Fight. Become a God.");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
