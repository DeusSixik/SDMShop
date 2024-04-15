package net.sdm.sdmshopr.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.SDMShopRClient;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.tab.ShopTab;

public class MainShopScreen extends BaseScreen {
    @Override public boolean drawDefaultBackground(PoseStack graphics) {return false;}

    public ShopTab selectedTab;

    public TabsPanel tabsPanel;
    public EntryPanel entryPanel;
    public TextField moneyInfo;

    public MainShopScreen(){

        if(Shop.CLIENT.shopTabs.isEmpty()) selectedTab = null;
        else {
            selectedTab = null;
            for (ShopTab shopTab : Shop.CLIENT.shopTabs) {
                if(!shopTab.isLocked()) {
                    selectedTab = shopTab;
                    break;
                }
            }
        }
    }

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
        moneyInfo.setText(Component.literal(SDMShopR.moneyString(SDMShopR.getClientMoney())).withStyle(SDMShopRClient.getTheme().getMoneyTextColor().toStyle()));

        add(tabsPanel = new TabsPanel(this, 80, height - 20));
        add(entryPanel = new EntryPanel(this, this.width - 80, height));
    }

    @Override
    public void alignWidgets() {
        moneyInfo.setSize(80, 20);
        moneyInfo.setPos(2,3 + Minecraft.getInstance().font.lineHeight);
        tabsPanel.setPos(0,20);
        entryPanel.setPos(tabsPanel.width,0);
    }


    @Override
    public void drawOffsetBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        theme.drawString(graphics, I18n.get("sdm.shop.money"), (int) (x + (Minecraft.getInstance().font.getSplitter().stringWidth(I18n.get("sdm.shop.money")) / 2)), y + 2);
//        theme.drawString(graphics, SDMShopR.moneyString(SDMShopR.getClientMoney()), x + 2, y + 2 + Minecraft.getInstance().font.lineHeight);
    }

    @Override
    public void drawBackground(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
        SDMShopRClient.shopTheme.getShadow().draw(matrixStack, x, y, w, h + 4);
        SDMShopRClient.shopTheme.getBackground().draw(matrixStack, x + 1, y + 1, w - 2, h - 2);
        GuiHelper.drawHollowRect(matrixStack, x, y, w, h, SDMShopRClient.shopTheme.getReact(), false);
        GuiHelper.drawHollowRect(matrixStack, x - 1, y - 1, w + 2, h + 5, SDMShopRClient.shopTheme.getStoke(), false);
    }

    public static void refreshIfOpen() {
        if (Minecraft.getInstance().screen instanceof ScreenWrapper w && w.getGui() instanceof MainShopScreen mts) {
            mts.refreshWidgets();
        }
    }
}
