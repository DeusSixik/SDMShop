package net.sixik.sdmshoprework.client.screen.legacy;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.sixik.sdmshoprework.client.screen.basic.panel.AbstractShopEntriesPanel;
import net.sixik.v2.render.GLRenderHelper;

public class LegacyShopEntriesPanel extends AbstractShopEntriesPanel {

    public LegacyShopEntriesPanel(Panel panel) {
        super(panel);
    }

    @Override
    public void draw(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        GLRenderHelper.pushScissor(graphics, x,y,w,h);
        super.draw(graphics, theme, x, y, w, h);
        GLRenderHelper.popScissor(graphics);
    }
}
