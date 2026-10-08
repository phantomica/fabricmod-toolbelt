package net.phantomica.toolbelt.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.tags.ItemTags;
import net.phantomica.toolbelt.menu.ToolSlotInventory;

public final class ToolbeltNetworking {
	private ToolbeltNetworking() {
	}

	public static void register() {
		PayloadTypeRegistry.serverboundPlay().register(ToolbeltSelectionPayload.TYPE, ToolbeltSelectionPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ToolbeltSelectionPayload.TYPE, (payload, context) -> {
			boolean clearSelection = payload.mode() == -1 && isToolSlot(payload.toolSlot());
			boolean validSwordSelection = payload.mode() == 0 && isToolSlot(payload.toolSlot());
			boolean validToolSelection = payload.mode() == 1
					&& isToolSlot(payload.toolSlot());
			if (!clearSelection && !validSwordSelection && !validToolSelection) {
				return;
			}

			ToolSlotInventory inventory = (ToolSlotInventory) context.player().getInventory();
			inventory.toolbelt$setActiveTool(payload.mode(), payload.toolSlot());
		});
	}

	private static boolean isToolSlot(int slot) {
		return slot == 0 || slot == 2 || slot == 3 || slot == 4;
	}
}
