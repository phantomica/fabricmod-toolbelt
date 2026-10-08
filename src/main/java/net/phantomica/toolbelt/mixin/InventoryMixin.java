package net.phantomica.toolbelt.mixin;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.phantomica.toolbelt.menu.ToolSlotInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public abstract class InventoryMixin implements ToolSlotInventory {
	@Unique
	private SimpleContainer toolbelt$toolSlots;

	@Unique
	private int toolbelt$activeMode = -1;

	@Unique
	private int toolbelt$activeToolSlot = 4;

	@Override
	public SimpleContainer toolbelt$getToolSlots() {
		if (this.toolbelt$toolSlots == null) {
			this.toolbelt$toolSlots = new SimpleContainer(5);
		}

		return this.toolbelt$toolSlots;
	}

	@Override
	public int toolbelt$getActiveMode() {
		return this.toolbelt$activeMode;
	}

	@Override
	public int toolbelt$getActiveToolSlot() {
		return this.toolbelt$activeToolSlot;
	}

	@Override
	public void toolbelt$setActiveTool(int mode, int toolSlot) {
		this.toolbelt$activeMode = mode;
		this.toolbelt$activeToolSlot = toolSlot;
	}

	@Inject(method = "getSelectedItem", at = @At("HEAD"), cancellable = true)
	private void toolbelt$selectToolbeltItem(CallbackInfoReturnable<ItemStack> cir) {
		if (this.toolbelt$activeMode >= 0) {
			int toolSlot = this.toolbelt$activeMode == 0 ? 1 : this.toolbelt$activeToolSlot;
			cir.setReturnValue(this.toolbelt$getToolSlots().getItem(toolSlot));
		}
	}

	@Inject(method = "setSelectedItem", at = @At("HEAD"), cancellable = true)
	private void toolbelt$setToolbeltItem(ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {
		if (this.toolbelt$activeMode >= 0) {
			SimpleContainer toolSlots = this.toolbelt$getToolSlots();
			int toolSlot = this.toolbelt$activeMode == 0 ? 1 : this.toolbelt$activeToolSlot;
			ItemStack previous = toolSlots.getItem(toolSlot);
			toolSlots.setItem(toolSlot, itemStack);
			cir.setReturnValue(previous);
		}
	}

	@Inject(method = "removeFromSelected", at = @At("HEAD"), cancellable = true)
	private void toolbelt$removeFromToolbelt(final boolean all, CallbackInfoReturnable<ItemStack> cir) {
		if (this.toolbelt$activeMode >= 0) {
			SimpleContainer toolSlots = this.toolbelt$getToolSlots();
			int toolSlot = this.toolbelt$activeMode == 0 ? 1 : this.toolbelt$activeToolSlot;
			ItemStack selected = toolSlots.getItem(toolSlot);
			cir.setReturnValue(selected.isEmpty() ? ItemStack.EMPTY : toolSlots.removeItem(toolSlot, all ? selected.getCount() : 1));
		}
	}

	@Inject(method = "setSelectedSlot", at = @At("HEAD"))
	private void toolbelt$deactivateToolbeltOnHotbarSelection(int selected, CallbackInfo ci) {
		if (selected != ((Inventory) (Object) this).getSelectedSlot()) {
			this.toolbelt$activeMode = -1;
		}
	}

	@Inject(method = "replaceWith", at = @At("TAIL"))
	private void toolbelt$copyToolSlots(Inventory other, CallbackInfo ci) {
		SimpleContainer source = ((ToolSlotInventory) other).toolbelt$getToolSlots();
		SimpleContainer destination = this.toolbelt$getToolSlots();
		for (int slot = 0; slot < destination.getContainerSize(); slot++) {
			destination.setItem(slot, source.getItem(slot).copy());
		}
		ToolSlotInventory sourceInventory = (ToolSlotInventory) other;
		this.toolbelt$setActiveTool(-1, sourceInventory.toolbelt$getActiveToolSlot());
	}

	@Inject(method = "dropAll", at = @At("TAIL"))
	private void toolbelt$dropToolSlots(CallbackInfo ci) {
		Inventory inventory = (Inventory) (Object) this;
		SimpleContainer toolSlots = this.toolbelt$getToolSlots();
		for (int slot = 0; slot < toolSlots.getContainerSize(); slot++) {
			ItemStack itemStack = toolSlots.getItem(slot);
			if (!itemStack.isEmpty()) {
				ItemEntity itemEntity = inventory.player.createItemStackToDrop(itemStack, true, false);
				if (itemEntity != null) {
					inventory.player.level().addFreshEntity(itemEntity);
				}

				toolSlots.setItem(slot, ItemStack.EMPTY);
			}
		}
	}
}
