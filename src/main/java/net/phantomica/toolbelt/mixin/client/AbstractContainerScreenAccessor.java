package net.phantomica.toolbelt.mixin.client;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Mutable
    @Final
    @Accessor("imageWidth")
    void toolbelt$setImageWidth(int width);

    @Accessor("leftPos")
    int toolbelt$getLeftPos();

    @Accessor("topPos")
    int toolbelt$getTopPos();

    @Accessor("menu")
    AbstractContainerMenu toolbelt$getMenu();
}
