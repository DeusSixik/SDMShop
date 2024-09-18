package net.sdm.sdmshoprework.client.screen.legacy;

import dev.ftb.mods.ftblibrary.ui.*;
import net.minecraft.client.gui.GuiGraphics;
import net.sdm.sdmshoprework.SDMShopClient;
import net.sdm.sdmshoprework.SDMShopR;
import net.sdm.sdmshoprework.SDMShopRework;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntry;
import net.sdm.sdmshoprework.client.screen.basic.AbstractShopScreen;
import net.sdm.sdmshoprework.client.screen.basic.widget.AbstractShopEntryButton;
import net.sdm.sdmshoprework.client.screen.legacy.createEntry.LegacyCreateEntryScreen;
import net.sdm.sdmshoprework.client.screen.legacy.widget.LegacyShopEntryButton;
import net.sdm.sdmshoprework.client.screen.legacy.widget.LegacyShopTabButton;
import net.sdm.sdmshoprework.common.shop.ShopBase;
import net.sdm.sdmshoprework.common.shop.ShopTab;
import net.sixik.sdmuilibrary.client.utils.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class LegacyShopScreen extends AbstractShopScreen {

    public PanelScrollBar tabsScrollPanel;
    public PanelScrollBar entryScrollPanel;

    @Override
    public void addWidgets() {
        add(this.entriesPanel = new LegacyShopEntriesPanel(this));

        add(this.entryScrollPanel = new PanelScrollBar(this, ScrollBar.Plane.VERTICAL, entriesPanel) {
            @Override
            public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                SDMShopClient.getTheme().getShadow().draw(graphics, x + 1, y + 1, w - 2, h - 2);
                GuiHelper.drawHollowRect(graphics, x,y,w,h, SDMShopClient.getTheme().getReact(), false);
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                SDMShopClient.getTheme().getStoke().draw(graphics, x, y, w, h);
            }
        });
        add(this.tabsPanel = new LegacyShopTabPanel(this));
        add(this.tabsScrollPanel = new PanelScrollBar(this, ScrollBar.Plane.VERTICAL, tabsPanel) {
            @Override
            public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                SDMShopClient.getTheme().getShadow().draw(graphics, x + 1, y + 1, w - 2, h - 2);
                GuiHelper.drawHollowRect(graphics, x,y,w,h, SDMShopClient.getTheme().getReact(), false);
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                SDMShopClient.getTheme().getStoke().draw(graphics, x, y, w, h);
            }
        });
        add(this.moneyPanel = new LegacyShopMoneyPanel(this));
//        this.tabsScrollPanel.setCanAlwaysScroll(true);
        this.tabsScrollPanel.setScrollStep(19.0);
//        this.entryScrollPanel.setCanAlwaysScroll(true);
        this.entryScrollPanel.setScrollStep(19.0);
        setProperties();
    }


    @Override
    public void setProperties(){
        Vector2 size = new Vector2(getWidth() / 4, getHeight());
        Vector2 additionSize = new Vector2(getWidth() - size.x, getHeight() / 8);
        int pos = size.x;

        this.tabsPanel.setY(additionSize.y);
        this.tabsPanel.setSize(size.x, size.y - additionSize.y - 1);
        tabsPanel.alignWidgets();
        this.moneyPanel.setSize(size.x, additionSize.y);


        this.entriesPanel.setX(pos);
        this.entriesPanel.setSize(getWidth() - size.x, size.y);

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
                this.entriesPanel.getHeight()
        );

        addTabsButtons();
        addEntriesButtons();
    }

    public Vector2 entryButtonSize = new Vector2(48,48);

    @Override
    public void addEntriesButtons() {
        if(selectedTab != null) {
            List<AbstractShopEntryButton> widgets = new ArrayList<>();

            for (AbstractShopEntry abstractShopEntry : selectedTab.getTabEntry()) {

                if(!abstractShopEntry.isLocked() || SDMShopR.isEditMode()) {

                    LegacyShopEntryButton button = new LegacyShopEntryButton(entriesPanel, abstractShopEntry);
                    button.setSize(entryButtonSize.x, entryButtonSize.y);
                    widgets.add(button);
                }
            }

            if(SDMShopR.isEditMode()) {

                LegacyShopEntryButton button = new LegacyShopEntryButton(entriesPanel, null);
                button.setSize(entryButtonSize.x, entryButtonSize.y);
                button.setEdit();
                widgets.add(button);

            }

            calculatePositions(widgets);

            entriesPanel.getWidgets().clear();
            entriesPanel.addAll(widgets);
            entryScrollPanel.setValue(0.0);

        }
    }

    @Override
    public void calculatePositions(List<AbstractShopEntryButton> entryButtons) {
        int maxElementsOnScreen = getCountInArray();
        int x = getStartPosX(getCountInArray());
        int y = 2;
        for (int i = 0; i < entryButtons.size(); i++) {
            AbstractShopEntryButton shopEntryButton = entryButtons.get(i);

            if(i > 0) {
                if (i % maxElementsOnScreen == 0) {
                    y += entryButtonSize.y + 6;
                    x = getStartPosX(getCountInArray());
                } else {
                    x += entryButtonSize.x + 3;
                }
                shopEntryButton.setPos(x,y);
            }
            else shopEntryButton.setPos(x,y);
        }
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
    public void addTabsButtons() {
        List<Widget> widgetList = new ArrayList<>();
        int y = 2;
        for (ShopTab shopTab : ShopBase.CLIENT.getShopTabs()) {
            LegacyShopTabButton button = new LegacyShopTabButton(tabsPanel, shopTab);
            button.setSize(tabsPanel.width - 3 - getScrollbarWidth(), 18);
            button.setPos(2, y);
            widgetList.add(button);
            y+=24;
        }

        if(SDMShopR.isEditMode()) {
            LegacyShopTabButton button = new LegacyShopTabButton(tabsPanel, null);
            button.setSize(tabsPanel.width - 3 - getScrollbarWidth(), 18);
            button.setPos(2, y);
            button.setEdit();
            widgetList.add(button);
        }

        this.tabsPanel.getWidgets().clear();
        this.tabsPanel.addAll(widgetList);
        this.tabsScrollPanel.setValue(0);
    }

    protected int getScrollbarWidth() {
        return 4;
    }
}
