package net.sixik.sdmshoprework.client.screen.basic.buyer;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshoprework.client.screen.basic.AbstractShopScreen;
import net.sixik.sdmshoprework.client.screen.legacy.buyer.LegacyBuyerScreen;

public abstract class AbstractBuyerCancelButton extends SimpleTextButton {
    public AbstractBuyerCancelButton(Panel panel) {
        super(panel, Component.translatable("sdm.shop.buyer.button.cancel"), Icon.empty());
    }


    @Override
    public void onClicked(MouseButton mouseButton) {
        if(mouseButton.isLeft()) {
            AbstractBuyerScreen screen = getBuyerScreen();
            screen.closeGui();
            AbstractShopScreen.refreshIfOpen();
        }

    }

    public AbstractBuyerScreen getBuyerScreen() {
        return (AbstractBuyerScreen) getGui();
    }
}
