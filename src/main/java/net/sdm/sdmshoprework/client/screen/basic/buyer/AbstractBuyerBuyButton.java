package net.sdm.sdmshoprework.client.screen.basic.buyer;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sdm.sdmshoprework.client.screen.basic.AbstractShopScreen;
import net.sdm.sdmshoprework.network.client.SendBuyShopEntryC2S;
import net.sixik.sdmuilibrary.client.utils.GLHelper;

public abstract class AbstractBuyerBuyButton extends SimpleTextButton {

    public AbstractBuyerBuyButton(Panel panel) {
        super(panel, Component.translatable("sdm.shop.buyer.button.accept"), Icon.empty());
    }

    @Override
    public void onClicked(MouseButton mouseButton) {
        if(mouseButton.isLeft()){
            var d1 = getBuyerScreen().shopEntry;
            new SendBuyShopEntryC2S(d1.getShopTab().shopTabUUID, d1.entryUUID, getBuyerScreen().count).sendToServer();
            getBuyerScreen().closeGui();
            AbstractShopScreen.refreshIfOpen();
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
