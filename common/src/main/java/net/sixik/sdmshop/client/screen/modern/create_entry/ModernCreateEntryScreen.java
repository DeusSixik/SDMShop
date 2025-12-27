package net.sixik.sdmshop.client.screen.modern.create_entry;

import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.ScrollBar;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.client.screen.base.create_entry.AbstractCreateEntryButton;
import net.sixik.sdmshop.client.screen.base.create_entry.AbstractCreateEntryScreen;
import net.sixik.sdmshop.client.screen.modern.widgets.ModernBackButton;
import net.sixik.sdmshop.client.screen.modern.widgets.ModernShowOnlyLoadedButton;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.registers.ShopContentRegister;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ModernCreateEntryScreen extends AbstractCreateEntryScreen {
    public boolean drawDefaultBackground(GuiGraphics graphics) {
        return false;
    }

    public ModernCreateEntryScreen(AbstractShopScreen shopScreen) {
        super(shopScreen);
    }

    public void addWidgets() {
        this.setWidth(this.getScreen().getGuiScaledWidth() * 4 / 5);
        this.setHeight(this.getScreen().getGuiScaledHeight() * 4 / 5);
        this.add(this.entriesPanel = new ModernCreateEntryPanel(this));
        this.add(this.entriesScrollPanel = new PanelScrollBar(this, ScrollBar.Plane.VERTICAL, this.entriesPanel) {
            public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                SDMShopClient.someColor.draw(graphics, x, y, w, h);
            }

            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                RGBA.create(0, 0, 0, 127).draw(graphics, x, y, w, h, 0.0F);
            }
        });
        this.add(this.backToShopButton = new ModernBackButton(this));
        this.add(this.shopOnlyLoadedButton = new ModernShowOnlyLoadedButton(this));
        this.closeContextMenu();
        this.setProperties();
    }

    public void alignWidgets() {
        this.setProperties();
    }

    public void setProperties() {
        int h = this.height / 8;
        this.entriesPanel.setSize(this.width - this.getScrollbarWidth(), this.height - h);
        this.entriesScrollPanel.setPosAndSize(this.entriesPanel.getPosX() + this.entriesPanel.getWidth(), this.entriesPanel.getPosY(), this.getScrollbarWidth(), this.entriesPanel.getHeight());
        this.backToShopButton.setPosAndSize(8, this.height - h + h / 4, 60, 16);
        this.shopOnlyLoadedButton.setPosAndSize(this.backToShopButton.posX + this.backToShopButton.width + 4, this.backToShopButton.posY, 20 + Theme.DEFAULT.getStringWidth(I18n.get("sdm.shop.entry.creator.info", new Object[0])) + 5, 16);
        this.addEntriesButtons();
    }

    public void addEntriesButtons() {
        List<AbstractCreateEntryButton> widgetList = new ArrayList();

        for (Function<ShopEntry, AbstractEntryType> value : ShopContentRegister.getEntryTypes().values()) {
            ShopEntry entry = new ShopEntry(SDMShopClient.CurrentShop);
            AbstractEntryType entryType = value.apply(entry);
            ModernCreateEntryButton button = new ModernCreateEntryButton(this.entriesPanel, entryType);
            button.setSize(50, 50);
            if (this.showNotLoadedContent && !button.isActive() || button.isActive()) {
                widgetList.add(button);
            }
        }

        this.calculatePositions(widgetList);
        this.entriesPanel.getWidgets().clear();
        this.entriesPanel.addAll(widgetList);
        this.entriesScrollPanel.setValue((double)0.0F);
    }

    public void calculatePositions(List<AbstractCreateEntryButton> entryButtons) {
        int maxElementsOnScreen = this.getCountInArray();
        int x = this.getStartPosX(this.getCountInArray());
        int y = 2;

        for(int i = 0; i < entryButtons.size(); ++i) {
            AbstractCreateEntryButton shopEntryButton = entryButtons.get(i);
            if (i > 0) {
                if (i % maxElementsOnScreen == 0) {
                    y += 56;
                    x = this.getStartPosX(this.getCountInArray());
                } else {
                    x += 53;
                }

                shopEntryButton.setPos(x, y);
            } else {
                shopEntryButton.setPos(x, y);
            }
        }

    }

    public int getCountInArray() {
        int x1 = 0;
        int x = 50;

        for(int i = 0; i < 1000; ++i) {
            x1 = x * i + 3 * i;
            if (x1 > this.entriesPanel.width) {
                return i - 1;
            }
        }

        return 0;
    }

    public int getStartPosX(int count) {
        int x = 50;
        int x1 = 0;

        for(int i = 0; i < count + 1; ++i) {
            x1 = x * i + 3 * i;
        }

        return this.entriesPanel.width / 2 - x1 / 2 + 1;
    }

    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        RenderHelper.drawRoundedRectDown(graphics, x, y + this.entriesPanel.height + 2, this.entriesPanel.width, h - this.entriesPanel.height - 2, 10, RGBA.create(0, 0, 0, 127));
    }

    protected int getScrollbarWidth() {
        return 2;
    }

    @Override
    public void onRefresh() {

    }
}
