package net.sdm.sdmshoprework.client.screen.legacy;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sdm.sdmshoprework.SDMShopClient;
import net.sdm.sdmshoprework.client.screen.basic.panel.AbstractShopTabPanel;

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
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
//        SDMShopClient.getTheme().drawHollow(graphics, x, y, w, h);
    }


}
