package net.sixik.sdmshoprework.client.screen.basic.buyer;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;

public abstract class AbstractBuyerScreen extends BaseScreen {

    public AbstractBuyerBuyButton buyButton;
    public AbstractBuyerCancelButton cancelButton;

    public AbstractShopEntry shopEntry;
    public int count = 0;

    public void setProperty(){}
}
