package net.sixk.sdmshop.shop;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixk.sdmshop.shop.Tab.TabPanel;
import net.sixk.sdmshop.shop.widgets.EntryPanel;



public class ShopEntriesPanel extends Panel {

    public EntryPanel entryPanel;

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

    }


    public ShopEntriesPanel(Panel panel) {
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
