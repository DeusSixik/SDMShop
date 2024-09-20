package net.sixik.sdmshoprework.client.screen.legacy;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshoprework.client.screen.basic.panel.AbstractShopEntriesPanel;
import net.sixik.sdmuilib.client.utils.GLHelper;

public class LegacyShopEntriesPanel extends AbstractShopEntriesPanel {

    public LegacyShopEntriesPanel(Panel panel) {
        super(panel);
    }

    @Override
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        GLHelper.pushScissor(graphics, x,y,w,h);
        super.draw(graphics, theme, x, y, w, h);
        GLHelper.popScissor(graphics);
    }
}
