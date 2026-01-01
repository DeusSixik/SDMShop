package net.sixik.sdmshop.utils.mixin;

import dev.ftb.mods.ftblibrary.ui.Widget;

public interface WidgetPath {

    boolean sdm$shouldRenderInLayer(Widget.DrawLayer layer, int x, int y, int w, int h);

}
