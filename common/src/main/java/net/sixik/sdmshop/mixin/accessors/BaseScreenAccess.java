package net.sixik.sdmshop.mixin.accessors;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Widget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BaseScreen.class, remap = false)
public interface BaseScreenAccess {

    @Accessor
    @Nullable Widget getFocusedWidget();
}
