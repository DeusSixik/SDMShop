package net.sixik.sdmshoprework.client.screen.basic.buyer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshoprework.client.screen.basic.AbstractShopScreen;
import net.sixik.sdmshoprework.network.client.SendBuyShopEntryC2S;

public abstract class AbstractBuyerBuyButton extends SimpleTextButton {

    public AbstractBuyerBuyButton(Panel panel) {
        super(panel, Component.translatable("sdm.shop.buyer.button.accept"), Color4I.EMPTY);
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
    public void draw(PoseStack graphics, Theme theme, int x, int y, int w, int h) {
        if(getBuyerScreen().count != 0) {
            super.draw(graphics, theme, x, y, w, h);
        }
    }

    public AbstractBuyerScreen getBuyerScreen() {
        return (AbstractBuyerScreen) getGui();
    }
}
