package net.sixik.sdmshop.api.screen;

import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;

public interface ShopUIRenderComponent {

    void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h);
}
