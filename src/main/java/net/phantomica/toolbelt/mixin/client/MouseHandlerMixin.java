package net.phantomica.toolbelt.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.world.entity.player.Inventory;
import net.phantomica.toolbelt.ToolbeltClient;
import net.phantomica.toolbelt.menu.ToolSlotInventory;
import net.phantomica.toolbelt.network.ToolbeltSelectionPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
	@Shadow
	private Minecraft minecraft;

	@Redirect(
			method = "onScroll",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/ScrollWheelHandler;getNextScrollWheelSelection(DII)I"
			)
	)
	private int toolbelt$scrollAcrossHotbarAndToolbelt(double wheel, int currentSelected, int hotbarSize) {
		Inventory inventory = this.minecraft.player.getInventory();
		ToolSlotInventory toolbelt = (ToolSlotInventory) inventory;
		boolean hasSword = !toolbelt.toolbelt$getToolSlots().getItem(1).isEmpty();
		int firstTool = firstToolSlot(toolbelt);

		int direction = wheel > 0.0 ? -1 : 1;
		if (ToolbeltClient.CYCLE_TOOLS.isDown() && firstTool >= 0) {
			int nextTool = nextToolSlot(toolbelt, toolbelt.toolbelt$getActiveToolSlot(), direction);
			if (nextTool != toolbelt.toolbelt$getActiveToolSlot() || toolbelt.toolbelt$getActiveMode() != 1) {
				toolbelt$selectTool(toolbelt, 1, nextTool);
			}

			return currentSelected;
		}

		int extraSlots = (hasSword ? 1 : 0) + (firstTool >= 0 ? 1 : 0);
		if (extraSlots == 0) {
			return ScrollWheelHandler.getNextScrollWheelSelection(wheel, currentSelected, hotbarSize);
		}

		int currentSelection;
		if (toolbelt.toolbelt$getActiveMode() == 0 && hasSword) {
			currentSelection = 0;
		} else if (toolbelt.toolbelt$getActiveMode() == 1 && firstTool >= 0) {
			currentSelection = hasSword ? 1 : 0;
		} else {
			currentSelection = extraSlots + currentSelected;
		}

		int nextSelection = Math.floorMod(currentSelection + direction, hotbarSize + extraSlots);
		if (nextSelection < extraSlots) {
			if (hasSword && nextSelection == 0) {
				toolbelt$selectTool(toolbelt, 0, toolbelt.toolbelt$getActiveToolSlot());
			} else {
				int toolSlot = toolbelt.toolbelt$getActiveToolSlot();
				if (toolbelt.toolbelt$getToolSlots().getItem(toolSlot).isEmpty()) {
					toolSlot = firstTool;
				}

				toolbelt$selectTool(toolbelt, 1, toolSlot);
			}

			return currentSelected;
		}

		toolbelt$selectTool(toolbelt, -1, toolbelt.toolbelt$getActiveToolSlot());
		return nextSelection - extraSlots;
	}

	private void toolbelt$selectTool(ToolSlotInventory inventory, int mode, int toolSlot) {
		if (ClientPlayNetworking.canSend(ToolbeltSelectionPayload.TYPE)) {
			inventory.toolbelt$setActiveTool(mode, toolSlot);
			ClientPlayNetworking.send(new ToolbeltSelectionPayload(mode, toolSlot));
		}
	}

	private static final int[] TOOL_SLOTS = {0, 2, 3, 4};

	private static int firstToolSlot(ToolSlotInventory inventory) {
		for (int slot : TOOL_SLOTS) {
			if (!inventory.toolbelt$getToolSlots().getItem(slot).isEmpty()) {
				return slot;
			}
		}

		return -1;
	}

	private static int nextToolSlot(ToolSlotInventory inventory, int currentTool, int direction) {
		int currentIndex = -1;
		for (int i = 0; i < TOOL_SLOTS.length; i++) {
			if (TOOL_SLOTS[i] == currentTool) {
				currentIndex = i;
				break;
			}
		}

		for (int offset = 1; offset <= TOOL_SLOTS.length; offset++) {
			int index = Math.floorMod(currentIndex + direction * offset, TOOL_SLOTS.length);
			int candidate = TOOL_SLOTS[index];
			if (!inventory.toolbelt$getToolSlots().getItem(candidate).isEmpty()) {
				return candidate;
			}
		}

		return currentTool;
	}
}
