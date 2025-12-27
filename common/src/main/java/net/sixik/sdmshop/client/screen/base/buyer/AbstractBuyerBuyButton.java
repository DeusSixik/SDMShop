package net.sixik.sdmshop.client.screen.base.buyer;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.SDMShopConstants;
import net.sixik.sdmshop.network.server.SendBuyEntryC2S;
import net.sixik.sdmshop.utils.ShopDebugUtils;

public abstract class AbstractBuyerBuyButton extends SimpleTextButton {

    public AbstractBuyerBuyButton(Panel panel) {
        super(panel, Component.translatable(SDMShopConstants.ACCEPT_KEY), Icon.empty());
    }

    @Override
    public void onClicked(MouseButton mouseButton) {
        if(mouseButton.isLeft()){
            var screen = getBuyerScreen();

            ShopDebugUtils.log("Send buy packet: {}, {}, {}", screen.shopScreen.currentShop.getId(), screen.shopEntry.getId(), screen.count);

            new SendBuyEntryC2S(screen.shopScreen.currentShop.getId(), screen.shopEntry.getId(), screen.count).sendToServer();
            screen.shopScreen.onRefresh();
            screen.closeGui();
        }
    }

    @Override
    public boolean checkMouseOver(int mouseX, int mouseY) {
        return getBuyerScreen().count > 0 && super.checkMouseOver(mouseX, mouseY);
    }

    @Override
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        if(getBuyerScreen().count != 0) {
            super.draw(graphics, theme, x, y, w, h);
        }
    }

    public AbstractBuyerScreen getBuyerScreen() {
        return (AbstractBuyerScreen) getGui();
    }
}
