package net.phantomica.toolbelt.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.phantomica.toolbelt.menu.ToolSlotInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class HudMixin {
	private static final int[] TOOL_SLOTS = {0, 2, 3, 4};
	private static final net.minecraft.resources.Identifier HOTBAR_BACKGROUND_TEXTURE =
			net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "textures/gui/sprites/hud/hotbar.png");
	private static final net.minecraft.resources.Identifier SELECTION_SPRITE =
			net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "hud/hotbar_selection");

	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void toolbelt$drawToolbeltHotbar(GuiGraphicsExtractor graphics, net.minecraft.client.DeltaTracker deltaTracker, CallbackInfo ci) {
		Minecraft minecraft = Minecraft.getInstance();
		Player player = minecraft.player;
		if (player == null || ((Hud) (Object) this).isHidden()) {
			return;
		}

		ToolSlotInventory inventory = (ToolSlotInventory) player.getInventory();
		ItemStack sword = inventory.toolbelt$getToolSlots().getItem(1);
		ItemStack tool = inventory.toolbelt$getToolSlots().getItem(inventory.toolbelt$getActiveToolSlot());
		boolean showSword = !sword.isEmpty();
		boolean showTools = toolbelt$hasTool(inventory);
		int visibleSlots = (showSword ? 1 : 0) + (showTools ? 1 : 0);
		if (visibleSlots == 0) {
			return;
		}

		int center = graphics.guiWidth() / 2;
		int x = center - 91 - visibleSlots * 20 - 30;
		int y = graphics.guiHeight() - 22;
		int swordSlotX = showSword ? x : -1;
		int toolSlotX = showTools ? x + (showSword ? 20 : 0) : -1;
		boolean swordSelected = inventory.toolbelt$getActiveMode() == 0;
		boolean toolSelected = inventory.toolbelt$getActiveMode() == 1;

		toolbelt$drawHotbarBackground(graphics, x, y, visibleSlots);
		if (showSword && swordSelected) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SELECTION_SPRITE, swordSlotX - 2, y - 1, 24, 23);
		}
		if (showTools && toolSelected) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SELECTION_SPRITE, toolSlotX - 2, y - 1, 24, 23);
		}

		if (showSword) {
			graphics.item(sword, swordSlotX + 3, y + 3);
			graphics.itemDecorations(minecraft.font, sword, swordSlotX + 3, y + 3);
		}
		if (showTools && !tool.isEmpty()) {
			graphics.item(tool, toolSlotX + 3, y + 3);
			graphics.itemDecorations(minecraft.font, tool, toolSlotX + 3, y + 3);
		}
	}

	@Redirect(
			method = "extractItemHotbar",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
					ordinal = 1
			)
	)
	private void toolbelt$hideVanillaHotbarSelection(GuiGraphicsExtractor graphics, RenderPipeline pipeline, net.minecraft.resources.Identifier sprite, int x, int y, int width, int height) {
		Player player = Minecraft.getInstance().player;
		if (player == null || !toolbelt$hasActiveTool((ToolSlotInventory) player.getInventory())) {
			graphics.blitSprite(pipeline, sprite, x, y, width, height);
		}
	}

	private static boolean toolbelt$hasTool(ToolSlotInventory inventory) {
		for (int slot : TOOL_SLOTS) {
			if (!inventory.toolbelt$getToolSlots().getItem(slot).isEmpty()) {
				return true;
			}
		}

		return false;
	}

	private static boolean toolbelt$hasActiveTool(ToolSlotInventory inventory) {
		return switch (inventory.toolbelt$getActiveMode()) {
			case 0 -> !inventory.toolbelt$getToolSlots().getItem(1).isEmpty();
			case 1 -> !inventory.toolbelt$getToolSlots().getItem(inventory.toolbelt$getActiveToolSlot()).isEmpty();
			default -> false;
		};
	}

	private static void toolbelt$drawHotbarBackground(GuiGraphicsExtractor graphics, int x, int y, int slots) {
		int width = 22 + (slots - 1) * 20;
		graphics.blit(
				RenderPipelines.GUI_TEXTURED,
				HOTBAR_BACKGROUND_TEXTURE,
				x,
				y,
				0.0F,
				0.0F,
				width,
				22,
				182,
				22
		);
	}
}
