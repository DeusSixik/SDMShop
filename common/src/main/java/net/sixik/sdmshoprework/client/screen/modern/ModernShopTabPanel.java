package net.sixik.sdmshoprework.client.screen.modern;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.sixik.sdmshoprework.client.screen.basic.panel.AbstractShopTabPanel;
import net.sixik.v2.color.RGBA;
import net.sixik.v2.render.GLRenderHelper;

public class ModernShopTabPanel extends AbstractShopTabPanel {

    public ModernShopTabPanel(Panel panel) {
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
        GLRenderHelper.pushScissor(graphics,x,y,w,h);
        super.draw(graphics, theme, x, y, w, h);
        GLRenderHelper.popScissor(graphics);
    }

    @Override
    public void drawBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        RGBA.create(0,0,0, 255 / 2).draw(graphics, x, y, w, h);
//        SDMShopClient.getTheme().draw(graphics, x, y, w, h);
    }
}
