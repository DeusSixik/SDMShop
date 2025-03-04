package net.sixik.sdmshoprework.client.screen.modern;


import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.ScrollBar;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshoprework.SDMShopClient;
import net.sixik.sdmshoprework.SDMShopR;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.api.shop.AbstractShopTab;
import net.sixik.sdmshoprework.client.screen.basic.AbstractShopScreen;
import net.sixik.sdmshoprework.client.screen.basic.widget.AbstractShopEntryButton;
import net.sixik.sdmshoprework.client.screen.modern.widget.ModernMarketButton;
import net.sixik.sdmshoprework.client.screen.modern.widget.ModernShopEntryButton;
import net.sixik.sdmshoprework.client.screen.modern.widget.ModernShopTabButton;
import net.sixik.sdmshoprework.common.shop.ShopBase;
import net.sixik.sdmshoprework.common.shop.ShopTab;
import net.sixik.sdmuilib.client.utils.math.Vector2;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

import java.util.ArrayList;
import java.util.List;


public class ModernShopScreen extends AbstractShopScreen {

    public PanelScrollBar tabsScrollPanel;
    public PanelScrollBar entryScrollPanel;
    public ModernShopPanels.TopEntriesPanel topEntriesPanel;
    public ModernShopPanels.TopPanel topPanel;
    public ModernShopPanels.BottomPanel bottomPanel;

    public ModernMarketButton marketButton;

    public ModernShopScreen(boolean isOpenCommand) {
        super(isOpenCommand);
    }

    @Override
    public void addWidgets() {
        if(SDMShopR.isMarketLoaded) {
            add(this.marketButton = new ModernMarketButton(this));
        }

        add(this.tabsPanel = new ModernShopTabPanel(this));
        add(this.tabsScrollPanel = new PanelScrollBar(this, ScrollBar.Plane.VERTICAL, tabsPanel) {
            @Override
            public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                SDMShopClient.getTheme().getReact().draw(graphics, x, y, w, h   );
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                RGBA.create(0,0,0, 255/2).draw(graphics,x,y,w,h,0);
            }
        });

        add(this.bottomPanel = new ModernShopPanels.BottomPanel(this));
        add(this.topPanel = new ModernShopPanels.TopPanel(this));
        add(this.entriesPanel = new ModernShopEntriesPanel(this));

        add(this.entryScrollPanel = new PanelScrollBar(this, ScrollBar.Plane.VERTICAL, entriesPanel) {
            @Override
            public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                SDMShopClient.getTheme().getReact().draw(graphics, x, y, w, h   );
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                RGBA.create(0,0,0, 255/2).draw(graphics,x,y,w,h,0);
            }
        });

        add(this.topEntriesPanel = new ModernShopPanels.TopEntriesPanel(this));
        setProperties();
    }

    @Override
    public void setProperties() {

        if(marketButton != null) {
            marketButton.setPos(this.width + 2, 0);
        }

        this.tabsPanel.setY(this.height / 7);
        this.tabsPanel.setSize(this.width / 5, this.height - (this.height / 7 * 2));

        this.topPanel.setPos(0,0);
        this.topPanel.setSize(this.tabsPanel.width, this.height / 7 - 2);

        this.bottomPanel.setPos(0, tabsPanel.getPosY() + 2 + tabsPanel.height);
        this.bottomPanel.setSize(this.tabsPanel.width, this.height / 7 - 2);

        this.entriesPanel.setPos(this.tabsPanel.width + 2, this.tabsPanel.posY);
        this.entriesPanel.setSize(this.width - this.tabsPanel.width - 2, this.height - topPanel.height - 2);

        this.topEntriesPanel.setPos(this.tabsPanel.width + 2, 0);
        this.topEntriesPanel.setSize(this.width - this.tabsPanel.width - 2, topPanel.height);

        this.tabsScrollPanel.setPosAndSize(
                this.tabsPanel.getPosX() + this.tabsPanel.getWidth() - this.getScrollbarWidth(),
                this.tabsPanel.getPosY(),
                this.getScrollbarWidth(),
                this.tabsPanel.getHeight()
        );

        this.entryScrollPanel.setPosAndSize(
                this.entriesPanel.getPosX() + this.entriesPanel.getWidth() - this.getScrollbarWidth(),
                this.entriesPanel.getPosY(),
                this.getScrollbarWidth(),
                this.entriesPanel.getHeight() - 10
        );

        addTabsButtons();
        addEntriesButtons();
    }

    public Vector2 entryButtonSize = new Vector2(38,38);

    @Override
    public void addEntriesButtons() {
        if(selectedTab != null) {
            List<AbstractShopEntryButton> widgets = new ArrayList<>();

            for (AbstractShopEntry abstractShopEntry : selectedTab.getTabEntry()) {

                if((!abstractShopEntry.isLocked() || SDMShopR.isEditMode()) && (this.searchField.isEmpty() || abstractShopEntry.getEntryType().isSearch(this.searchField))) {

                    ModernShopEntryButton button = new ModernShopEntryButton(entriesPanel, abstractShopEntry);
                    button.setSize(entryButtonSize.x, entryButtonSize.y);
                    widgets.add(button);
                }
            }

            if(SDMShopR.isEditMode()) {

                ModernShopEntryButton button = new ModernShopEntryButton(entriesPanel, null);
                button.setSize(entryButtonSize.x, entryButtonSize.y);
                button.setEdit();
                widgets.add(button);

            }

            calculatePositions(widgets);

            entriesPanel.getWidgets().clear();
            entriesPanel.addAll(widgets);
            entryScrollPanel.setValue(entryScrollPos);
        }
    }

    @Override
    public void calculatePositions(List<AbstractShopEntryButton> entryButtons) {
        int maxElementsOnScreen = getCountInArray() - 1;
        int x = getStartPosX(maxElementsOnScreen);
        int y = 2;
        for (int i = 0; i < entryButtons.size(); i++) {
            AbstractShopEntryButton shopEntryButton = entryButtons.get(i);

            if(i > 0) {
                if (i % maxElementsOnScreen == 0) {
                    y += entryButtonSize.y + 6 + 8;
                    x = getStartPosX(maxElementsOnScreen);
                } else {
                    x += entryButtonSize.x + 3;
                }
                shopEntryButton.setPos(x,y);
            }
            else shopEntryButton.setPos(x,y);
        }

        var d1 = new ModernShopEntryButton(entriesPanel, null) {

            @Override
            public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

            }

            @Override
            public boolean checkMouseOver(int mouseX, int mouseY) {
                return false;
            }
        };
        d1.setPos(0, y + 40);

        entryButtons.add(d1);
    }

    public int getCountInArray(){
        int x1 = 0;
        int x = entryButtonSize.x;

        for (int i = 0; i < 1000; i++) {
            x1 = (x * i ) + (3 * i);
            if(x1 > this.entriesPanel.width) return i - 1;
        }
        return 0;
    }

    public int getStartPosX(int count){
        int x = entryButtonSize.x;
        int x1 = 0;
        for (int i = 0; i < count + 1; i++) {
            x1 = (x * i ) + (3 * i);
        }

        return (this.entriesPanel.width / 2 ) - (x1 / 2);
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {

    }

    @Override
    public void addTabsButtons() {
        List<Widget> widgetList = new ArrayList<>();
        int y = 0;
        for (ShopTab shopTab : ShopBase.CLIENT.getShopTabs()) {

            if(!shopTab.isLocked() || SDMShopR.isEditMode()) {
                ModernShopTabButton button = new ModernShopTabButton(tabsPanel, shopTab);
                button.setSize(tabsPanel.width - 2 - getScrollbarWidth(), 15);
                button.setPos(0, y);
                widgetList.add(button);
                y += button.height + 3;
            }
        }

        if(SDMShopR.isEditMode()) {
            ModernShopTabButton button = new ModernShopTabButton(tabsPanel, null);
            button.setSize(tabsPanel.width - 2 - getScrollbarWidth(), 15);
            button.setPos(0, y);
            button.setEdit();
            widgetList.add(button);
        }

        this.tabsPanel.getWidgets().clear();
        this.tabsPanel.addAll(widgetList);
        this.tabsScrollPanel.setValue(0);
    }

    protected int getScrollbarWidth() {
        return 2;
    }

    @Override
    public void setSelectedTab(AbstractShopTab shopTab) {
        if(this.selectedTab == null) {
            addEntriesButtons();
            return;
        }
        if(this.selectedTab.shopTabUUID.equals(shopTab.shopTabUUID)) {
            this.entryScrollPos = Math.min(entryScrollPanel.getValue(), entryScrollPanel.getMaxValue());
        } else {
            entryScrollPos = 0.0;
        }

        super.setSelectedTab(shopTab);
    }
}
