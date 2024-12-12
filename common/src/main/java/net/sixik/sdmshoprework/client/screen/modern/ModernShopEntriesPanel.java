package net.sixik.sdmshoprework.client.screen.modern;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.sixik.sdmshoprework.client.screen.basic.panel.AbstractShopEntriesPanel;
import net.sixik.v2.color.RGBA;
import net.sixik.v2.render.GLRenderHelper;
import net.sixik.v2.render.RenderHelper;

public class ModernShopEntriesPanel extends AbstractShopEntriesPanel {

    public ModernShopEntriesPanel(Panel panel) {
        super(panel);
    }

    @Override
    public void drawBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        RenderHelper.drawRoundedRectDown(graphics,x,y,w,h,10, RGBA.create(0,0,0, 255/2));
    }


    @Override
    public void draw(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        GLRenderHelper.pushScissor(graphics,x,y,w,h);
        super.draw(graphics, theme, x, y, w, h);
        GLRenderHelper.popScissor(graphics);
    }
}
