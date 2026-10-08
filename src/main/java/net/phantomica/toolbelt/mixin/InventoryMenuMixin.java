package net.phantomica.toolbelt.mixin;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.phantomica.toolbelt.menu.ToolSlotInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	private void toolbelt$addToolSlots(Inventory inventory, boolean active, Player owner, CallbackInfo ci) {
		SimpleContainer toolSlots = ((ToolSlotInventory) owner.getInventory()).toolbelt$getToolSlots();
		AbstractContainerMenuAccessor menu = (AbstractContainerMenuAccessor) (AbstractContainerMenu) (Object) this;
		menu.toolbelt$addSlot(new ToolSlot(toolSlots, 1, 0, ItemTags.SWORDS));
		menu.toolbelt$addSlot(new ToolSlot(toolSlots, 0, 1, ItemTags.PICKAXES));
		menu.toolbelt$addSlot(new ToolSlot(toolSlots, 2, 2, ItemTags.AXES));
		menu.toolbelt$addSlot(new ToolSlot(toolSlots, 3, 3, ItemTags.SHOVELS));
		menu.toolbelt$addSlot(new ToolSlot(toolSlots, 4, 4, ItemTags.HOES));
	}

	private static final class ToolSlot extends Slot {
		private final net.minecraft.tags.TagKey<net.minecraft.world.item.Item> toolTag;

		private ToolSlot(SimpleContainer container, int slot, int row, net.minecraft.tags.TagKey<net.minecraft.world.item.Item> toolTag) {
			super(container, slot, 178, 8 + row * 18);
			this.toolTag = toolTag;
		}

		@Override
		public boolean mayPlace(ItemStack itemStack) {
			return itemStack.is(this.toolTag);
		}
	}
}
