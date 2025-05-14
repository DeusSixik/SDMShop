package net.sixk.sdmshop.shop.modern;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmuilibrary.client.utils.RenderHelper;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixk.sdmshop.shop.Tab.TabPanel;
import net.sixk.sdmshop.shop.widgets.EntryPanel;

public class ModernShopEntriesPanel extends Panel {

    public EntryPanel entryPanel;

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RenderHelper.drawRoundedRectDown(graphics,x,y,w,h,10, RGBA.create(0,0,0, 255/2));
    }


    public ModernShopEntriesPanel(Panel panel) {
        super(panel);
    }

    @Override
    public void addWidgets() {
        add(entryPanel = new EntryPanel(this, TabPanel.selectedTab));
    }

    @Override
    public void alignWidgets() {
        entryPanel.setPosAndSize(0, 0,width, height);
    }
}
