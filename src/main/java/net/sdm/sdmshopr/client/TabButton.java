package net.sdm.sdmshopr.client;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ui.EditConfigScreen;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.SDMShopRClient;
import net.sdm.sdmshopr.network.EditShopEntry;
import net.sdm.sdmshopr.network.EditShopTab;
import net.sdm.sdmshopr.network.MoveShopEntry;
import net.sdm.sdmshopr.network.MoveShopTab;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.shop.tab.ShopTab;

import java.util.ArrayList;
import java.util.List;

public class TabButton extends SimpleTextButton {
    public ShopTab tab;
    public TabButton(Panel panel, ShopTab tab) {
        super(panel, tab.title, ItemIcon.getItemIcon(tab.icon));
        this.tab = tab;
    }



    @Override
    public void onClicked(MouseButton mouseButton) {
        MainShopScreen d1 = (MainShopScreen) getGui();
        if(mouseButton.isLeft()){
            d1.selectedTab = tab;
            d1.refreshWidgets();
        }
        if(mouseButton.isRight() && SDMShopR.isEditModeClient()){
            MainShopScreen screen = (MainShopScreen) getGui();
            List<ContextMenuItem> contextMenu = new ArrayList<>();

            contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.edit"), Icons.SETTINGS, () -> {
                ConfigGroup group = new ConfigGroup("sdmr", b -> {
                    openGui();

                    if(b){
                        new EditShopTab(tab, false).sendToServer();
                    }
                }).setNameKey("sidebar_button.sdmr.shop");


                ConfigGroup g = group.getOrCreateSubgroup("shop").getOrCreateSubgroup("tab");
                tab.getConfig(g);
                new EditConfigScreen(group).openGui();
                screen.refreshWidgets();
            }));

            contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.delete"), Icons.REMOVE, () -> {
                new EditShopTab(tab, true).sendToServer();
                tab.shopEntryList.remove(tab);
                screen.refreshWidgets();
            }));

            /*
            TODO: Сделать перемещение вкладок
            contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.move.up"), Icons.UP, () -> {
                moveNew(screen,true);
            }));
            contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.context.move.down"), Icons.DOWN, () -> {
                moveNew(screen, false);
            }));
*/

            screen.openContextMenu(contextMenu);
        }
    }

    public void moveNew(MainShopScreen screen, boolean isUp){
        try {
            int index = tab.getIndex();
            int newIndex = tab.getIndex();
            if (isUp) {
                newIndex -= 1;
            } else {
                newIndex += 1;
            }
            if (index < 0 || index >= Shop.CLIENT.shopTabs.size() || newIndex < 0 || newIndex >= Shop.CLIENT.shopTabs.size()) {
                SDMShopR.LOGGER.error("[MOVE] Index a broken !");
                return;
            }


            ShopTab f1 = Shop.CLIENT.shopTabs.get(index);
            ShopTab f2 = Shop.CLIENT.shopTabs.get(newIndex);
            Shop.CLIENT.shopTabs.set(newIndex, f1);
            Shop.CLIENT.shopTabs.set(index, f2);
            new MoveShopTab(index, isUp).sendToServer();
            screen.refreshWidgets();
        } catch (Exception e){
            SDMShopR.LOGGER.error(e.toString());
        }

    }

    @Override
    public void drawBackground(GuiGraphics matrixStack, Theme theme, int x, int y, int w, int h) {
        SDMShopRClient.shopTheme.getShadow().draw(matrixStack, x, y, w, h + 4);
        SDMShopRClient.shopTheme.getBackground().draw(matrixStack, x + 1, y + 1, w - 2, h - 2);
        GuiHelper.drawHollowRect(matrixStack, x, y, w, h, SDMShopRClient.shopTheme.getReact(), false);

        if(isMouseOver || ((MainShopScreen)parent.getParent()).selectedTab == tab)
            GuiHelper.drawHollowRect(matrixStack, x - 1, y - 1, w + 2, h + 5, Color4I.WHITE, false);
        else
            GuiHelper.drawHollowRect(matrixStack, x - 1, y - 1, w + 2, h + 5, SDMShopRClient.shopTheme.getStoke(), false);
    }
}
