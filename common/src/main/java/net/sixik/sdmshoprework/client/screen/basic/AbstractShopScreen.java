package net.sixik.sdmshoprework.client.screen.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.ScreenWrapper;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.sixik.sdmshoprework.SDMShopClient;
import net.sixik.sdmshoprework.client.screen.basic.panel.AbstractShopEntriesPanel;
import net.sixik.sdmshoprework.client.screen.basic.panel.AbstractShopMoneyPanel;
import net.sixik.sdmshoprework.client.screen.basic.panel.AbstractShopTabPanel;
import net.sixik.sdmshoprework.client.screen.basic.widget.AbstractShopEntryButton;
import net.sixik.sdmshoprework.client.screen.legacy.createEntry.LegacyCreateEntryScreen;
import net.sixik.sdmshoprework.common.config.ConfigFile;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopTab;

import java.util.List;
import java.util.UUID;

public abstract class AbstractShopScreen extends BaseScreen {
    @Override public boolean drawDefaultBackground(PoseStack graphics) {return false;}


    public String searchField = "";
    public UUID selectedEntryID = null;
    public UUID selectedTabID = null;

    public AbstractShopEntriesPanel entriesPanel;
    public AbstractShopTabPanel tabsPanel;
    public AbstractShopMoneyPanel moneyPanel;

    public ShopTab selectedTab;

    public boolean isOpenCommand;

    public AbstractShopScreen(boolean isOpenCommand) {
        this.isOpenCommand = isOpenCommand;
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
        if(ConfigFile.CLIENT.disableKeyBind && !isOpenCommand) return false;

        setWidth(getScreen().getGuiScaledWidth() * 4/5);
        setHeight(getScreen().getGuiScaledHeight() * 4/5);

        this.openContextMenu((Panel)null);
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
    public void drawBackground(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        SDMShopClient.getTheme().draw(graphics, x, y, w, h);
    }
}
