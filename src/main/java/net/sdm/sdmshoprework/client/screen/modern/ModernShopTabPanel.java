package net.sdm.sdmshoprework.client.screen.modern;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sdm.sdmshoprework.client.screen.basic.panel.AbstractShopTabPanel;
import net.sixik.sdmuilibrary.client.utils.GLHelper;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import org.lwjgl.opengl.GL;

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
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        GLHelper.pushScissor(graphics,x,y,w,h);
        super.draw(graphics, theme, x, y, w, h);
        GLHelper.popScissor(graphics);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RGBA.create(0,0,0, 255 / 2).draw(graphics, x, y, w, h, 0);
//        SDMShopClient.getTheme().draw(graphics, x, y, w, h);
    }
}
