package net.phantomica.toolbelt;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.phantomica.toolbelt.Toolbelt;

public class ToolbeltClient implements ClientModInitializer {
	private static final KeyMapping.Category TOOLBELT_CATEGORY = KeyMapping.Category.register(Toolbelt.id("toolbelt"));

	public static KeyMapping CYCLE_TOOLS;

	@Override
	public void onInitializeClient() {
		CYCLE_TOOLS = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.toolbelt.cycle_tools",
				InputConstants.Type.KEYBOARD,
				InputConstants.KEY_LCONTROL,
				TOOLBELT_CATEGORY
		));
	}
}
