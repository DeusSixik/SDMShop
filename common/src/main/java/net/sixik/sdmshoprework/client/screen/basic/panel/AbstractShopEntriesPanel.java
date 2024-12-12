package net.sixik.sdmshoprework.client.screen.basic.panel;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.sixik.sdmshoprework.SDMShopClient;
import net.sixik.sdmshoprework.client.screen.basic.AbstractShopPanel;

public class AbstractShopEntriesPanel extends AbstractShopPanel {

    public AbstractShopEntriesPanel(Panel panel) {
        super(panel);
    }

    @Override
    public void addWidgets() {

    }

    @Override
    public void alignWidgets() {

    }

    @Override
    public void drawBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        SDMShopClient.getTheme().drawHollow(graphics, x, y, w, h);
    }
}
