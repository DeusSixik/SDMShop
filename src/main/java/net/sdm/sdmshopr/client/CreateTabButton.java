package net.sdm.sdmshopr.client;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.SDMShopRClient;
import net.sdm.sdmshopr.network.CreateShopTab;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.shop.tab.ShopTab;

public class CreateTabButton extends SimpleTextButton {

    public CreateTabButton(Panel panel) {
        super(panel, Component.literal("ADD"), Icons.ADD);
    }



    @Override
    public void onClicked(MouseButton mouseButton) {
        MainShopScreen screen = (MainShopScreen) getGui();
        if(mouseButton.isLeft() && SDMShopR.isEditModeClient()){
            ShopTab tab = new ShopTab(Shop.CLIENT);
            Shop.CLIENT.shopTabs.add(tab);
            new CreateShopTab(tab.serializeNBT()).sendToServer();
            screen.refreshWidgets();
        }
    }

    @Override
    public void drawBackground(GuiGraphics matrixStack, Theme theme, int x, int y, int w, int h) {
        SDMShopRClient.shopTheme.getShadow().draw(matrixStack, x, y, w, h + 4);
        SDMShopRClient.shopTheme.getBackground().draw(matrixStack, x + 1, y + 1, w - 2, h - 2);
        GuiHelper.drawHollowRect(matrixStack, x, y, w, h, SDMShopRClient.shopTheme.getReact(), false);

        if(isMouseOver)
            GuiHelper.drawHollowRect(matrixStack, x - 1, y - 1, w + 2, h + 5, Color4I.WHITE, false);
        else
            GuiHelper.drawHollowRect(matrixStack, x - 1, y - 1, w + 2, h + 5, SDMShopRClient.shopTheme.getStoke(), false);
    }
}
