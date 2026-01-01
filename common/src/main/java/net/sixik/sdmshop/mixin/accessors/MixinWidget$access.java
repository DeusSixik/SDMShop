package net.sixik.sdmshop.mixin.accessors;

import dev.ftb.mods.ftblibrary.ui.Widget;
import net.sixik.sdmshop.utils.mixin.WidgetPath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Widget.class, remap = false)
public abstract class MixinWidget$access implements WidgetPath {

    @Shadow
    abstract boolean shouldRenderInLayer(Widget.DrawLayer layer, int x, int y, int w, int h);

    @Override
    public boolean sdm$shouldRenderInLayer(Widget.DrawLayer layer, int x, int y, int w, int h) {
        return shouldRenderInLayer(layer, x, y, w, h);
    }
}
