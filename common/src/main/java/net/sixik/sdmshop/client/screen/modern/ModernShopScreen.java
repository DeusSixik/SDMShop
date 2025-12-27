package net.sixik.sdmshop.client.screen.modern;

import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.ScrollBar;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.client.screen.base.widgets.AbstractShopEntryButton;
import net.sixik.sdmshop.client.screen.modern.buyer.ModernBuyerScreen;
import net.sixik.sdmshop.client.screen.modern.create_entry.ModernCreateEntryScreen;
import net.sixik.sdmshop.client.screen.modern.panels.ModernShopEntriesPanel;
import net.sixik.sdmshop.client.screen.modern.panels.ModernShopPanels;
import net.sixik.sdmshop.client.screen.modern.panels.ModernShopTabPanel;
import net.sixik.sdmshop.client.screen.modern.widgets.ModernShopEntryButton;
import net.sixik.sdmshop.client.screen.modern.widgets.ModernShopTabButton;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmuilib.client.utils.math.Vector2;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModernShopScreen extends AbstractShopScreen {

    public static final Vector2 DEFAULT_ENTRY_BUTTON_SIZE = new Vector2(38,38);

    protected ModernShopPanels.TopEntriesPanel topEntriesPanel;
    protected ModernShopPanels.TopPanel topPanel;
    protected ModernShopPanels.BottomPanel bottomPanel;

    public ModernShopScreen() {}

    @Override
    public void addWidgets() {

    }

    @Override
    public void alignWidgets() {
        onRefresh();
    }

    @Override
    public boolean _init() {
        setWidth(getScreen().getGuiScaledWidth() * 4/5);
        setHeight(getScreen().getGuiScaledHeight() * 4/5);
        closeContextMenu();
        return super._init();
    }

    @Override
    public void addEntriesButtons() {
        if(selectedTab != null) {
            List<AbstractShopEntryButton> widgets = new ArrayList<>();

            boolean isClientEdit = ShopUtils.isEditModeClient();

            boolean showBuyedEntries = currentShop.getShopParams().showEntryWitchCantBuy();

            for (ShopEntry entry : currentShop.findShopEntriesByTab(selectedTab)) {

                boolean canExecute = entry.getEntryType().canExecute(Minecraft.getInstance().player, entry, 1);
                boolean needHide = showBuyedEntries && !canExecute;
                boolean needHideClient = SDMShopClient.userData.showEntryWitchCantBuy(currentShop.getUuid()) && !canExecute;

                if((needHide || needHideClient) && !isClientEdit) continue;

                boolean locked = entry.isLockedAll(entry);
                boolean limited = entry.isLimitReached(Minecraft.getInstance().player);
                boolean searched = this.searchField.isEmpty() || entry.getEntryType().isSearch(this.searchField);

                if((locked || limited) && !isClientEdit) continue;

                if (searched) {
                    ModernShopEntryButton button = new ModernShopEntryButton(entryPanel, entry);
                    button.setSize(DEFAULT_ENTRY_BUTTON_SIZE.x, DEFAULT_ENTRY_BUTTON_SIZE.y);
                    widgets.add(button);
                }
            }

            widgets.sort(Comparator.comparing(AbstractShopEntryButton::isFavorite).reversed());

            if(isClientEdit) {

                ModernShopEntryButton button = new ModernShopEntryButton(entryPanel, null, true);
                button.setSize(DEFAULT_ENTRY_BUTTON_SIZE.x, DEFAULT_ENTRY_BUTTON_SIZE.y);
                widgets.add(button);

            }

            calculatePositions(widgets);

            entryPanel.getWidgets().clear();
            entryPanel.addAll(widgets);
            scrollEntryPanel.setValue(entryScrollPos);
        }
    }


    public void calculatePositions(List<AbstractShopEntryButton> entryButtons) {
        int maxElementsOnScreen = getCountInArray() - 1;
        int x = getStartPosX(maxElementsOnScreen);
        int y = 2;
        for (int i = 0; i < entryButtons.size(); i++) {
            AbstractShopEntryButton shopEntryButton = entryButtons.get(i);

            if(i > 0) {
                if (i % maxElementsOnScreen == 0) {
                    y += DEFAULT_ENTRY_BUTTON_SIZE.y + 6 + 8;
                    x = getStartPosX(maxElementsOnScreen);
                } else {
                    x += DEFAULT_ENTRY_BUTTON_SIZE.x + 3;
                }
                shopEntryButton.setPos(x,y);
            }
            else shopEntryButton.setPos(x,y);
        }

        var d1 = new ModernShopEntryButton(entryPanel, null) {

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
        int x = DEFAULT_ENTRY_BUTTON_SIZE.x;

        for (int i = 0; i < 1000; i++) {
            x1 = (x * i ) + (3 * i);
            if(x1 > this.entryPanel.width) return i - 1;
        }
        return 0;
    }

    public int getStartPosX(int count){
        int x = DEFAULT_ENTRY_BUTTON_SIZE.x;
        int x1 = 0;
        for (int i = 0; i < count + 1; i++) {
            x1 = (x * i ) + (3 * i);
        }

        return (this.entryPanel.width / 2 ) - (x1 / 2);
    }

    @Override
    public void addTabsButtons() {
        List<Widget> widgetList = new ArrayList<>();
        boolean isEditMode = ShopUtils.isEditModeClient();
        int y = 0;
        for (ShopTab shopTab : currentShop.getShopTabs()) {

            if((shopTab.isLockedAll(shopTab) || shopTab.isLimitReached(Minecraft.getInstance().player)) && !isEditMode) continue;

            ModernShopTabButton button = new ModernShopTabButton(tabPanel, shopTab);
            button.setSize(tabPanel.width - 2 - getScrollbarWidth(), 15);
            button.setPos(0, y);
            widgetList.add(button);
            y += button.height + 3;

        }

        if(isEditMode) {
            ModernShopTabButton button = new ModernShopTabButton(tabPanel, null, true);
            button.setSize(tabPanel.width - 2 - getScrollbarWidth(), 15);
            button.setPos(0, y);
            widgetList.add(button);
        }

        this.tabPanel.getWidgets().clear();
        this.tabPanel.addAll(widgetList);
        this.scrollTabPanel.setValue(0);
    }

    private int getScrollbarWidth() {
        return 2;
    }

    @Override
    public void _onRefresh() {
        selectTab(selectedTab, false);

        getWidgets().clear();

        add(this.tabPanel = new ModernShopTabPanel(this));
        add(this.scrollTabPanel = new PanelScrollBar(this, ScrollBar.Plane.VERTICAL, tabPanel) {
            @Override
            public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                SDMShopClient.someColor.draw(graphics, x, y, w, h   );
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                RGBA.create(0,0,0, 255/2).draw(graphics,x,y,w,h,0);
            }
        });

        add(this.bottomPanel = new ModernShopPanels.BottomPanel(this));
        add(this.topPanel = new ModernShopPanels.TopPanel(this));
        add(this.entryPanel = new ModernShopEntriesPanel(this));

        add(this.scrollEntryPanel = new PanelScrollBar(this, ScrollBar.Plane.VERTICAL, entryPanel) {
            @Override
            public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                SDMShopClient.someColor.draw(graphics, x, y, w, h   );
            }

            @Override
            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                RGBA.create(0,0,0, 255/2).draw(graphics,x,y,w,h,0);
            }
        });

        add(this.topEntriesPanel = new ModernShopPanels.TopEntriesPanel(this));

        this.tabPanel.setY(this.height / 7);
        this.tabPanel.setSize(this.width / 5, this.height - (this.height / 7 * 2));

        this.topPanel.setPos(0,0);
        this.topPanel.setSize(this.tabPanel.width, this.height / 7 - 2);
        this.topPanel.addWidgets();
        this.topPanel.alignWidgets();

        this.bottomPanel.setPos(0, tabPanel.getPosY() + 2 + tabPanel.height);
        this.bottomPanel.setSize(this.tabPanel.width, this.height / 7 - 2);
        this.bottomPanel.addWidgets();
        this.bottomPanel.alignWidgets();

        this.entryPanel.setPos(this.tabPanel.width + 2, this.tabPanel.posY);
        this.entryPanel.setSize(this.width - this.tabPanel.width - 2, this.height - topPanel.height - 2);

        this.topEntriesPanel.setPos(this.tabPanel.width + 2, 0);
        this.topEntriesPanel.setSize(this.width - this.tabPanel.width - 2, topPanel.height);
        this.topEntriesPanel.addWidgets();
        this.topEntriesPanel.alignWidgets();

        this.scrollTabPanel.setPosAndSize(
                this.tabPanel.getPosX() + this.tabPanel.getWidth() - this.getScrollbarWidth(),
                this.tabPanel.getPosY(),
                this.getScrollbarWidth(),
                this.tabPanel.getHeight()
        );

        this.scrollEntryPanel.setPosAndSize(
                this.entryPanel.getPosX() + this.entryPanel.getWidth() - this.getScrollbarWidth(),
                this.entryPanel.getPosY(),
                this.getScrollbarWidth(),
                this.entryPanel.getHeight() - 10
        );

        addTabsButtons();
        addEntriesButtons();

    }

    @Override
    public void onShopChange() {
        onRefresh();
    }

    @Override
    public void openBuyScreen(AbstractShopEntryButton entry) {
        new ModernBuyerScreen(this, entry).openGui();
    }

    @Override
    public void openCreateEntryScreen() {
        new ModernCreateEntryScreen(this).openGui();
    }
}
