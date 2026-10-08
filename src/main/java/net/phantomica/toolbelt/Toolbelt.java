package net.phantomica.toolbelt;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import net.phantomica.toolbelt.network.ToolbeltNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Toolbelt implements ModInitializer {
	public static final String MOD_ID = "toolbelt";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing "  + MOD_ID);
		ToolbeltNetworking.register();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
