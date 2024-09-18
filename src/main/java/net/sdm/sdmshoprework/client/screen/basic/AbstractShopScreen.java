package net.sdm.sdmshoprework.client.screen.basic;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.ScreenWrapper;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.sdm.sdmshoprework.SDMShopClient;
import net.sdm.sdmshoprework.client.screen.basic.panel.AbstractShopEntriesPanel;
import net.sdm.sdmshoprework.client.screen.basic.panel.AbstractShopMoneyPanel;
import net.sdm.sdmshoprework.client.screen.basic.panel.AbstractShopTabPanel;
import net.sdm.sdmshoprework.client.screen.basic.widget.AbstractShopEntryButton;
import net.sdm.sdmshoprework.client.screen.legacy.createEntry.LegacyCreateEntryScreen;
import net.sdm.sdmshoprework.common.shop.ShopBase;
import net.sdm.sdmshoprework.common.shop.ShopTab;

import java.util.List;
import java.util.UUID;

public abstract class AbstractShopScreen extends BaseScreen {
    @Override public boolean drawDefaultBackground(GuiGraphics graphics) {return false;}


    public String searchField = "";
    public UUID selectedEntryID = null;
    public UUID selectedTabID = null;

    public AbstractShopEntriesPanel entriesPanel;
    public AbstractShopTabPanel tabsPanel;
    public AbstractShopMoneyPanel moneyPanel;

    public ShopTab selectedTab;

    public AbstractShopScreen() {
        onConstruct();
    }


    public void onConstruct() {
        if(ShopBase.CLIENT.getShopTabs().isEmpty()) selectedTab = null;
        else {
            selectedTab = null;
            for (ShopTab shopTab : ShopBase.CLIENT.getShopTabs()) {
                if(!shopTab.isLocked()) {
                    selectedTab = shopTab;
                    break;
                }
            }
        }
    }


    public void openCreateScreen() {
        new LegacyCreateEntryScreen(this).openGui();
    }

    @Override
    public void alignWidgets() {
        setProperties();
    }

    public void setProperties(){

    }

    public void setSelectedTab(ShopTab shopTab){
        this.selectedTab = shopTab;
        this.selectedEntryID = null;
    }

    @Override
    public boolean onInit() {
        setWidth(getScreen().getGuiScaledWidth() * 4/5);
        setHeight(getScreen().getGuiScaledHeight() * 4/5);

        closeContextMenu();
        return true;
    }

    public static void refreshIfOpen() {
        if (Minecraft.getInstance().screen instanceof ScreenWrapper w && w.getGui() instanceof AbstractShopScreen mts) {
            mts.refreshWidgets();
        }
    }

    public abstract void addEntriesButtons();
    public abstract void addTabsButtons();

    public abstract void calculatePositions(List<AbstractShopEntryButton> entryButtons);

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        SDMShopClient.getTheme().draw(graphics, x, y, w, h);
    }
}
