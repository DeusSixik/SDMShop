package net.sixik.sdmshop.mixin.accessors;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ui.EditConfigScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EditConfigScreen.class, remap = false)
public interface EditConfigScreenAccessor {

    @Accessor("changed")
    boolean isChanged();

    @Accessor("group")
    ConfigGroup getGroup();

    @Accessor("group")
    void setGroup(ConfigGroup group);
}
