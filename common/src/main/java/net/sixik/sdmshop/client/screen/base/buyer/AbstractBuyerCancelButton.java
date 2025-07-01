package net.sixik.sdmshop.client.screen.base.buyer;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.SDMShopConstants;

public abstract class AbstractBuyerCancelButton extends SimpleTextButton {
    public AbstractBuyerCancelButton(Panel panel) {
        super(panel, Component.translatable(SDMShopConstants.CANCEL_KEY), Icon.empty());
    }


    @Override
    public void onClicked(MouseButton mouseButton) {
        if(mouseButton.isLeft()) {
            AbstractBuyerScreen screen = getBuyerScreen();
            screen.shopScreen.onRefresh();
            screen.closeGui();
        }

    }

    public AbstractBuyerScreen getBuyerScreen() {
        return (AbstractBuyerScreen) getGui();
    }
}
