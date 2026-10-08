package net.phantomica.toolbelt.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.phantomica.toolbelt.menu.ToolSlotInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Inject(method = "getItemBySlot", at = @At("HEAD"), cancellable = true)
	private void toolbelt$getActiveTool(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
		if (slot == EquipmentSlot.MAINHAND && (Object) this instanceof Player player) {
			ToolSlotInventory inventory = (ToolSlotInventory) player.getInventory();
			if (inventory.toolbelt$getActiveMode() >= 0) {
				int toolSlot = inventory.toolbelt$getActiveMode() == 0 ? 1 : inventory.toolbelt$getActiveToolSlot();
				cir.setReturnValue(inventory.toolbelt$getToolSlots().getItem(toolSlot));
			}
		}
	}

	@Inject(method = "setItemSlot", at = @At("HEAD"), cancellable = true)
	private void toolbelt$setActiveTool(EquipmentSlot slot, ItemStack itemStack, CallbackInfo ci) {
		if (slot == EquipmentSlot.MAINHAND && (Object) this instanceof Player player) {
			ToolSlotInventory inventory = (ToolSlotInventory) player.getInventory();
			if (inventory.toolbelt$getActiveMode() >= 0) {
				int toolSlot = inventory.toolbelt$getActiveMode() == 0 ? 1 : inventory.toolbelt$getActiveToolSlot();
				inventory.toolbelt$getToolSlots().setItem(toolSlot, itemStack);
				ci.cancel();
			}
		}
	}
}
