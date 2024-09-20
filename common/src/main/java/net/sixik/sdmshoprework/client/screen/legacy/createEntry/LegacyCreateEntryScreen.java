package net.sixik.sdmshoprework.client.screen.legacy.createEntry;

import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.sixik.sdmshoprework.SDMShopClient;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.register.ShopContentRegister;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sixik.sdmshoprework.client.screen.basic.AbstractShopScreen;
import net.sixik.sdmshoprework.client.screen.basic.createEntry.AbstractCreateEntryButton;
import net.sixik.sdmshoprework.client.screen.basic.createEntry.AbstractCreateEntryScreen;
import net.sixik.sdmshoprework.client.screen.basic.widget.AbstractShopEntryButton;

import java.util.ArrayList;
import java.util.List;

public class LegacyCreateEntryScreen extends AbstractCreateEntryScreen {

    @Override public boolean drawDefaultBackground(GuiGraphics graphics) {return false;}

    public LegacyCreateEntryScreen(AbstractShopScreen shopScreen) {
        super(shopScreen);
    }

    @Override
    public void addWidgets() {
        setWidth(getScreen().getGuiScaledWidth() * 4/5);
        setHeight(getScreen().getGuiScaledHeight() * 4/5);

        add(this.entriesPanel = new LegacyCreateEntryPanel(this));
        add(this.entriesScrollPanel = new PanelScrollBar(this , ScrollBar.Plane.VERTICAL, this.entriesPanel) {
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

        add(this.backToShopButton = new AbstractBackButton(this));
        add(this.shopOnlyLoadedButton = new AbstractShowOnlyLoadedButton(this));

        closeContextMenu();
        setProperties();
    }

    @Override
    public void alignWidgets() {

        setProperties();
    }


    @Override
    public void setProperties() {
        this.entriesPanel.setSize(this.width - this.width / 6, this.height - this.height / 9);

        this.entriesScrollPanel.setPosAndSize(
                this.entriesPanel.getPosX() + this.entriesPanel.getWidth() - this.getScrollbarWidth(),
                this.entriesPanel.getPosY(),
                this.getScrollbarWidth(),
                this.entriesPanel.getHeight()
        );
        Theme theme = new Theme();
        backToShopButton.setPosAndSize(8, this.height - 24, 60, 16);
        shopOnlyLoadedButton.setPosAndSize(backToShopButton.posX + backToShopButton.width + 4, backToShopButton.posY, 20 + theme.getStringWidth(I18n.get("sdm.shop.entry.creator.info")) + 5, 16);

        addEntriesButtons();
    }



    @Override
    public void addEntriesButtons() {

        List<AbstractCreateEntryButton> widgetList = new ArrayList<>();
        for (IConstructor<AbstractShopEntryType> value : ShopContentRegister.SHOP_ENTRY_TYPES.values()) {
            AbstractShopEntryType shopEntryType = value.createDefaultInstance();

            LegacyCreateEntryButton button = new LegacyCreateEntryButton(this.entriesPanel, shopEntryType);
            button.setSize(sizeButton, sizeButton);

            if((showNotLoadedContent && !button.isActive()) || button.isActive()) {
                widgetList.add(button);
            }
        }

        calculatePositions(widgetList);

        entriesPanel.getWidgets().clear();
        entriesPanel.addAll(widgetList);
        entriesScrollPanel.setValue(0.0);
    }

    public void calculatePositions(List<AbstractCreateEntryButton>  entryButtons){
        int maxElementsOnScreen = getCountInArray();
        int x = getStartPosX(getCountInArray());
        int y = 2;
        for (int i = 0; i < entryButtons.size(); i++) {
            AbstractCreateEntryButton shopEntryButton = entryButtons.get(i);

            if(i > 0) {
                if (i % maxElementsOnScreen == 0) {
                    y += sizeButton + 6;
                    x = getStartPosX(getCountInArray());
                } else {
                    x += sizeButton + 3;
                }
                shopEntryButton.setPos(x,y);
            }
            else shopEntryButton.setPos(x,y);
        }
    }

    public int getCountInArray(){
        int x1 = 0;
        int x = sizeButton;

        for (int i = 0; i < 1000; i++) {
            x1 = (x * i ) + (3 * i);
            if(x1 > this.entriesPanel.width) return i - 1;
        }
        return 0;
    }

    public int getStartPosX(int count){
        int x = sizeButton;
        int x1 = 0;
        for (int i = 0; i < count + 1; i++) {
            x1 = (x * i ) + (3 * i);
        }

        return (this.entriesPanel.width / 2 ) - (x1 / 2) + 1;
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        SDMShopClient.getTheme().draw(graphics, x, y, w, h);
    }


    protected int getScrollbarWidth() {
        return 4;
    }
}
