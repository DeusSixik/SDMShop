package net.sixik.sdmshop.mixin.accessors;

import dev.ftb.mods.ftblibrary.ui.Panel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Panel.class, remap = false)
public interface PanelAccessor {

    @Accessor("offsetX")
    int offsetX();

    @Accessor("offsetY")
    int offsetY();
}
