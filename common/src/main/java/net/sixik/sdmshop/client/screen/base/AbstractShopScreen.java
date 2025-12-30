package net.sixik.sdmshop.client.screen.base;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen.base.panels.AbstractShopEntryPanel;
import net.sixik.sdmshop.client.screen.base.panels.AbstractShopTabPanel;
import net.sixik.sdmshop.client.screen.base.widgets.AbstractShopEntrySearch;
import net.sixik.sdmshop.old_api.screen.BuyerScreenSupport;
import net.sixik.sdmshop.old_api.screen.EntryCreateScreenSupport;
import net.sixik.sdmshop.old_api.screen.InfoButtonSupport;
import net.sixik.sdmshop.old_api.screen.RefreshSupport;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.ShopUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractShopScreen extends BaseScreen implements EntryCreateScreenSupport,
        ShopBase.ShopChangeListener, BuyerScreenSupport, RefreshSupport, InfoButtonSupport {

    @Override
    public boolean drawDefaultBackground(GuiGraphics graphics) {
        return false;
    }

    protected double entryScrollPos = 0.0;
    protected double tabScrollPos = 0.0;

    public @Nullable UUID selectedEntryId;
    public @Nullable UUID selectedShopTab;

    protected PanelScrollBar scrollEntryPanel;
    protected PanelScrollBar scrollTabPanel;

    protected AbstractShopEntryPanel entryPanel;
    protected AbstractShopTabPanel tabPanel;
    protected AbstractShopEntrySearch entrySearch;

    public BaseShop currentShop;
    public UUID selectedTab;
    public String searchField = "";

    public AbstractShopScreen() {
        currentShop = Objects.requireNonNull(SDMShopClient.CurrentShop);
    }

    @Override
    public final boolean onInit() {
        var value = super.onInit() && _init();

        if (value)
            currentShop.getShopChangeListeners().add(this);

        return value;
    }

    @Override
    public void onClosed() {
        currentShop.getShopChangeListeners().remove(this);
        super.onClosed();
    }

    public boolean _init() {
        onConstruct();
        return true;
    }

    protected void onConstruct() {
        if (selectedTab != null) return;

        for (ShopTab shopTab : currentShop.getTabs()) {
            if (!shopTab.isLockedAll(shopTab) || ShopUtils.isEditModeClient()) {
                selectedTab = shopTab.getId();
                break;
            }
        }
    }

    @Override
    public void addWidgets() {

    }

    public @Nullable UUID getCurrentTabUuid() {
        return selectedTab;
    }

    public Optional<ShopTab> getCurrentTab() {
        return currentShop.getTabOptional(selectedTab);
    }

    public Optional<ShopTab> getSelectedTabId() {
        return currentShop.getTabOptional(selectedShopTab);
    }

    public Optional<ShopEntry> getSelectedEntryId() {
        return currentShop.getEntryOptional(selectedEntryId);
    }

    public void selectTab(UUID uuid) {
        selectTab(uuid, true);
    }

    public void selectTab(UUID uuid, boolean recreate) {
        if (Objects.equals(this.selectedTab, uuid)) {
            if (scrollEntryPanel != null)
                this.entryScrollPos = Math.min(scrollEntryPanel.getValue(), scrollEntryPanel.getMaxValue());
        } else {
            this.entryScrollPos = 0.0;
        }

        this.selectedTab = uuid;
        this.selectedEntryId = null;

        if (this.selectedTab != null && recreate)
            addEntriesButtons();
    }

    public abstract void addEntriesButtons();

    public abstract void addTabsButtons();

    public static void wortInProgress() {
        Minecraft.getInstance().player.sendSystemMessage(Component.literal("Work in progress").withStyle(ChatFormatting.RED));
    }

    @Override
    public void openInfoScreen() {

    }

    public abstract void _onRefresh();

    @Override
    public void onRefresh() {
        RefreshSupport.refreshIfOpened();
        _onRefresh();
    }

    public static void refreshIfOpen() {
        RefreshSupport.refreshIfOpened(AbstractShopScreen.class);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
    }
}
