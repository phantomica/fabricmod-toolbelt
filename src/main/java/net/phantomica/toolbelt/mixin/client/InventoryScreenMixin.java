package net.phantomica.toolbelt.mixin.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {
	private static final String[] TOOL_LABELS = {"Sw", "P", "A", "S", "H"};
	private static final Identifier SLOT_SPRITE = Identifier.fromNamespaceAndPath("minecraft", "container/slot");

	@Inject(method = "init", at = @At("HEAD"))
	private void toolbelt$expandInventoryClickArea(CallbackInfo ci) {
		((AbstractContainerScreenAccessor) this).toolbelt$setImageWidth(200);
	}

	@Inject(method = "extractBackground", at = @At("TAIL"))
	private void toolbelt$drawToolSlotPanel(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		AbstractContainerScreenAccessor screen = (AbstractContainerScreenAccessor) this;
		for (int row = 0; row < TOOL_LABELS.length; row++) {
			graphics.blitSprite(
					RenderPipelines.GUI_TEXTURED,
					SLOT_SPRITE,
					screen.toolbelt$getLeftPos() + 178,
					screen.toolbelt$getTopPos() + 8 + row * 18,
					18,
					18
			);
		}
	}

	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void toolbelt$labelEmptyToolSlots(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		AbstractContainerScreenAccessor screen = (AbstractContainerScreenAccessor) this;
		for (int row = 0; row < TOOL_LABELS.length; row++) {
			int toolSlot = InventoryMenu.SHIELD_SLOT + 1 + row;
			if (screen.toolbelt$getMenu().getSlot(toolSlot).hasItem()) {
				continue;
			}

			graphics.text(
					Minecraft.getInstance().font,
					Component.literal(TOOL_LABELS[row]),
					screen.toolbelt$getLeftPos() + 181,
					screen.toolbelt$getTopPos() + 11 + row * 18,
					0xFF3F3F3F,
					false
			);
		}
	}
}
