package net.sixik.sdmshop.mixin.accessors;

import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ConfigGroup.class, remap = false)
public interface ConfigGroupAccess {

    @Accessor("savedCallback")
    ConfigCallback getSavedCallback();

    @Accessor("id")
    String getId();
}
