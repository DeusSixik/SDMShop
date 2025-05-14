package net.sixk.sdmshop.shop.modern;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;


public class ShopPageModern extends BaseScreen {

    public ModernShopTitelPanel top = new ModernShopTitelPanel(this);
    public ModernTabPanel tabPanel ;
    public ModernShopPageWalletPanel walletPanel = new ModernShopPageWalletPanel(this);
    public ModernSearchPanel topSearch = new ModernSearchPanel(this);
    public ModernShopEntriesPanel tovarPanel = new ModernShopEntriesPanel(this);

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

    }

    @Override
    public boolean onInit() {

        setWidth(getScreen().getGuiScaledWidth());
        setHeight(getScreen().getGuiScaledHeight());
        refreshWidgets();

        return true;
    }

    @Override
    public boolean drawDefaultBackground(GuiGraphics graphics) {
        return false;
    }

    @Override
    public void addWidgets() {
        add(top);
        add(tabPanel = new ModernTabPanel(this));
        tabPanel.setPosAndSize(width/8, height/11 + height/11 + 3,  width/5, height - height/4 - 3 - height/7);
        add(walletPanel);
        add(topSearch);
        topSearch.setPosAndSize(width/8 + width/5 + 3, height/11,  width - (width/4 + width/5) + 7, height/11);
        add(tovarPanel);
        tovarPanel.setPosAndSize(width/8 + width/5 + 3,height/11 + height/11 + 3,width - (width/4 + width/5) + 7 ,height - height/4);
    }

    @Override
    public void alignWidgets() {

        top.setPosAndSize(width/8, height/11,  width/5, height/11);


        walletPanel.setPosAndSize(width/8, height/11*2 + height - height/4 - height/7 + 3,  width/5, height/7);



    }
}
