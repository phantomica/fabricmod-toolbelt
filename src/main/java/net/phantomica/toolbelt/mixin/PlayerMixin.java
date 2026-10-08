package net.phantomica.toolbelt.mixin;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.phantomica.toolbelt.menu.ToolSlotInventory;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {
	@Shadow
	public abstract Inventory getInventory();

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void toolbelt$saveToolSlots(ValueOutput output, CallbackInfo ci) {
		SimpleContainer toolSlots = ((ToolSlotInventory) this.getInventory()).toolbelt$getToolSlots();
		ValueOutput.TypedOutputList<ItemStackWithSlot> items = output.list("toolbeltToolSlots", ItemStackWithSlot.CODEC);
		for (int slot = 0; slot < toolSlots.getContainerSize(); slot++) {
			if (!toolSlots.getItem(slot).isEmpty()) {
				items.add(new ItemStackWithSlot(slot, toolSlots.getItem(slot)));
			}
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void toolbelt$loadToolSlots(ValueInput input, CallbackInfo ci) {
		SimpleContainer toolSlots = ((ToolSlotInventory) this.getInventory()).toolbelt$getToolSlots();
		for (int slot = 0; slot < toolSlots.getContainerSize(); slot++) {
			toolSlots.setItem(slot, net.minecraft.world.item.ItemStack.EMPTY);
		}

		for (ItemStackWithSlot item : input.listOrEmpty("toolbeltToolSlots", ItemStackWithSlot.CODEC)) {
			if (item.isValidInContainer(toolSlots.getContainerSize())) {
				toolSlots.setItem(item.slot(), item.stack());
			}
		}
	}
}
