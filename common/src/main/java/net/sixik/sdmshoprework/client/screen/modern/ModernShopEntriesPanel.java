package net.sixik.sdmshoprework.client.screen.modern;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshoprework.client.screen.basic.panel.AbstractShopEntriesPanel;
import net.sixik.sdmuilib.client.utils.GLHelper;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ModernShopEntriesPanel extends AbstractShopEntriesPanel {

    public ModernShopEntriesPanel(Panel panel) {
        super(panel);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RenderHelper.drawRoundedRectDown(graphics,x,y,w,h,10, RGBA.create(0,0,0, 255/2));
    }


    @Override
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        GLHelper.pushScissor(graphics,x,y,w,h);
        super.draw(graphics, theme, x, y, w, h);
        GLHelper.popScissor(graphics);
    }
}
