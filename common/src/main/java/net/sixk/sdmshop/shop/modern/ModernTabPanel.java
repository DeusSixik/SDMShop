package net.sixk.sdmshop.shop.modern;

import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmuilibrary.client.utils.misc.RGBA;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.shop.Tab.TabPanel;

public class ModernTabPanel extends Panel {

    public ModernTabPanel(Panel panel) {
        super(panel);

    }

    public TabPanel tabPanel;
    public SimpleButton addTovarTab;

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RGBA.create(0,0,0, 255/2).draw(graphics, x, y, w, h);
    }



    @Override
    public void addWidgets() {
        add(tabPanel = new TabPanel(this));
        tabPanel.setPosAndSize(2,2,width - 3,height - 2);

        if(SDMShop.isEditMode()) {
            add(addTovarTab = new SimpleButton(this, Component.translatable("sdm_shop.shop_page.add_tab"), Icons.ADD, (simpleButton, mouseButton) -> {
                new ModernAddTabPanel().openGui();
            }));
            addTovarTab.setPos(1,tabPanel.getContentHeight());
        }
    }

    @Override
    public void alignWidgets() {

    }
}
