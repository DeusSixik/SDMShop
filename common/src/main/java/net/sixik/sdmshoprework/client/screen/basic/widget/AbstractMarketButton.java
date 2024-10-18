package net.sixik.sdmshoprework.client.screen.basic.widget;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.network.chat.Component;
import net.sixik.sdmmarket.SDMMarketIcons;
import net.sixik.sdmmarket.client.gui.user.MarketUserScreen;
import net.sixik.sdmshoprework.SDMShopR;

public abstract class AbstractMarketButton extends SimpleTextButton {

    public AbstractMarketButton(Panel panel) {
        super(panel, Component.empty(), SDMMarketIcons.BASKET);
    }

    @Override
    public void onClicked(MouseButton mouseButton) {
        if(SDMShopR.isMarketLoaded && mouseButton.isLeft()) {
            new MarketUserScreen().openGui();
        }
    }
}
