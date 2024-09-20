package net.sixik.sdmshoprework.client.screen.basic;

import dev.ftb.mods.ftblibrary.ui.Panel;

public abstract class AbstractShopPanel extends Panel {
    public AbstractShopPanel(Panel panel) {
        super(panel);
    }


    public AbstractShopScreen getShopScreen() {
        return (AbstractShopScreen) getGui();
    }
}
