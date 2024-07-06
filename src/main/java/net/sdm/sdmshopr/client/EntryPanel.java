package net.sdm.sdmshopr.client;

import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.SDMShopRClient;
import net.sdm.sdmshopr.api.customization.APIShopEntryButton;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.entry.ShopEntry;

public class EntryPanel extends Panel {
    public MainShopScreen shopScreen;
    public int sizeButtonX = 48;
    public int sizeButtonY = 49;

    public EntryPanel(MainShopScreen panel, int w, int h) {
        super(panel);
        this.shopScreen = panel;
        setSize(w,h);
    }

    public int getCountInArray(){
        int x1 = 0;
        int x = sizeButtonX;

        for (int i = 0; i < 1000; i++) {
            x1 = (x * i ) + (3 * i);
            if(x1 > this.width) return i - 1;
        }
        return 0;
    }

    public int getStartPosX(int count){
        int x = sizeButtonX;
        int x1 = 0;
        for (int i = 0; i < count + 1; i++) {
            x1 = (x * i ) + (3 * i);
        }

        return (this.width / 2 ) - (x1 / 2);
    }

    @Override
    public void addWidgets() {
        if(this.shopScreen.selectedTab != null){
            int maxInArray = getCountInArray();
            int x = getStartPosX(getCountInArray());
            int y = 2;
            for (int i = 0; i < shopScreen.selectedTab.shopEntryList.size(); i++) {
                ShopEntry<?> entry = shopScreen.selectedTab.shopEntryList.get(i);
                if(entry.type == null) continue;
                if(!entry.isLocked()) {
                    APIShopEntryButton entryButton = entry.getButton().create(this,entry);
                    entryButton.setSize(sizeButtonX, sizeButtonY);
                    if (i > 0) {
                        if (i % maxInArray == 0) {
                            y += sizeButtonY + 6;
                            x = getStartPosX(getCountInArray());
                        } else {
                            x += sizeButtonX + 3;
                        }
                        entryButton.setPos(x, y);
                    } else entryButton.setPos(x, y);

                    add(entryButton);
                }
            }

            if(SDMShopR.isEditModeClient()){
                CreateEntryButton button = new CreateEntryButton(this);
                button.setSize(sizeButtonX,sizeButtonY);

                if(shopScreen.selectedTab.shopEntryList.isEmpty()) {
                    button.setPos(x, y);
                } else {
                    if(x + sizeButtonX * 2 + 3 > this.width){
                        y += sizeButtonY + 6;
                        x = getStartPosX(getCountInArray());
                    } else
                        x += sizeButtonX + 3;
                    button.setPos(x, y);
                }
                add(button);
            }
        }
    }

    @Override
    public void alignWidgets() {

    }

    @Override
    public void drawBackground(GuiGraphics matrixStack, Theme theme, int x, int y, int w, int h) {
        SDMShopRClient.shopTheme.getBackground().draw(matrixStack, x + 1, y + 1, w - 2, h - 2);
        GuiHelper.drawHollowRect(matrixStack, x, y, w, h, SDMShopRClient.shopTheme.getReact(), false);
    }
}
