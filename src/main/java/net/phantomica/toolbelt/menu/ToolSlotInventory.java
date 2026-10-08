package net.phantomica.toolbelt.menu;

import net.minecraft.world.SimpleContainer;

public interface ToolSlotInventory {
    SimpleContainer toolbelt$getToolSlots();

    int toolbelt$getActiveMode();

    int toolbelt$getActiveToolSlot();

    void toolbelt$setActiveTool(int mode, int toolSlot);
}
