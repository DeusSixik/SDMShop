package net.sdm.sdmshopr.client;

import dev.ftb.mods.ftblibrary.ui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.SDMShopRClient;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.tab.ShopTab;

public class MainShopScreen extends BaseScreen {
    @Override public boolean drawDefaultBackground(GuiGraphics graphics) {return false;}

    public ShopTab selectedTab;

    public TabsPanel tabsPanel;
    public EntryPanel entryPanel;
    public TextField moneyInfo;
    public MainShopScreen(){
        selectedTab = Shop.CLIENT.shopTabs.isEmpty() ? null : Shop.CLIENT.shopTabs.get(0);
    }

//    public static void refreshIsOpen(){
//
//    }

    @Override
    public boolean onInit() {
        setWidth(getScreen().getGuiScaledWidth() * 4/5);
        setHeight(getScreen().getGuiScaledHeight() * 4/5);



        return true;
    }

    @Override
    public void addWidgets() {
        add(moneyInfo = new TextField(this));

        moneyInfo.setScale(0.8f);
        moneyInfo.setText(SDMShopR.moneyString(SDMShopR.getClientMoney()));

        add(tabsPanel = new TabsPanel(this, 60, height - 20));
        add(entryPanel = new EntryPanel(this, this.width - 60, height));
    }

    @Override
    public void alignWidgets() {
        moneyInfo.setSize(60, 20);
        moneyInfo.setPos(2,2 + Minecraft.getInstance().font.lineHeight);
        tabsPanel.setPos(0,20);
        entryPanel.setPos(tabsPanel.width,0);
    }


    @Override
    public void drawOffsetBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        theme.drawString(graphics, I18n.get("sdm.shop.money"), (int) (x + (Minecraft.getInstance().font.getSplitter().stringWidth(I18n.get("sdm.shop.money")) / 2)), y + 2);
//        theme.drawString(graphics, SDMShopR.moneyString(SDMShopR.getClientMoney()), x + 2, y + 2 + Minecraft.getInstance().font.lineHeight);
    }

    @Override
    public void drawBackground(GuiGraphics matrixStack, Theme theme, int x, int y, int w, int h) {
        SDMShopRClient.shopTheme.getShadow().draw(matrixStack, x, y, w, h + 4);
        SDMShopRClient.shopTheme.getBackground().draw(matrixStack, x + 1, y + 1, w - 2, h - 2);
        GuiHelper.drawHollowRect(matrixStack, x, y, w, h, SDMShopRClient.shopTheme.getReact(), false);
        GuiHelper.drawHollowRect(matrixStack, x - 1, y - 1, w + 2, h + 5, SDMShopRClient.shopTheme.getStoke(), false);
    }
}
