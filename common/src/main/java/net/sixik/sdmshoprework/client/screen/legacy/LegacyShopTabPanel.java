package net.sixik.sdmshoprework.client.screen.legacy;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.sixik.sdmshoprework.client.screen.basic.panel.AbstractShopTabPanel;
import net.sixik.v2.render.GLRenderHelper;

public class LegacyShopTabPanel extends AbstractShopTabPanel {
    public LegacyShopTabPanel(Panel panel) {
        super(panel);
    }

    @Override
    public void addWidgets() {

    }

    @Override
    public void alignWidgets() {

    }

    @Override
    public void draw(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        GLRenderHelper.pushScissor(graphics, x,y,w,h);
        super.draw(graphics, theme, x, y, w, h);
        GLRenderHelper.popScissor(graphics);
    }

    @Override
    public void drawBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
//        SDMShopClient.getTheme().drawHollow(graphics, x, y, w, h);
    }


}
